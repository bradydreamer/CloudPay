//app/Common/ConsumptionData.js
define(['Moo', 'RMS'], function(Moo, RMS) {
	var dataForPayment;
	var dataForCancellingOrder;
	var dataForBalance;
	var dataForMultiPay;
	var dataForBatchTask;
	//use for caching single batch in memory
	var dataForBatchArray;
	//use for caching local batch tasks in memory

	var ConsumptionData = new Class({

		initialize : function() {

		}
	});

	ConsumptionData.resetConsumptionData = function() {
		dataForPayment = {
			"typeOf8583" : "pay",
			"openBrh" : "",
			"paymentId" : "",
			"flowList" : [],
			"step" : 0,
		};
	};

	ConsumptionData.resetConsumptionData = function() {
		dataForPayment = {
			"typeOf8583" : "pay",
			"openBrh" : "",
			"paymentId" : "",
			"flowList" : [],
			"step" : 0,
		};
	};

	ConsumptionData.resetDataForCancellingOrder = function() {
		dataForCancellingOrder = {
			"rrn" : "",
			"openBrh" : "",
			"paymentId" : "",
			"transData8583" : "",
			"req8583" : "",
			"res8583" : "",
			"step" : 0,
		};
	};

	ConsumptionData.saveProcessBatchTask = function(data) {
		var msg = JSON.parse(data);
		var req = {
			saveCacheBatch : true,
			txnId : msg.txnId,
			resCode : msg.resCode,
			resMsg : msg.resMsg,
			refNo : msg.refNo,
			authNo : msg.authNo,
			issuerId : msg.issuerId,
			dateExpr : msg.dateExpr,
			stlmDate : msg.stlmDate
		};
		RMS.save("batchCache", req);
	};

	ConsumptionData.startSingleBatchTask = function(data) {
		var msg = JSON.parse(data);
		var action = msg.action;
		var req = {
			txnId : msg.txnId,
			oriTxnId : msg.oriTxnId,
			resCode : msg.resCode,
			resMsg : msg.resMsg,
			refNo : msg.refNo,
			//authNo: msg.authNo,
			issuerId : msg.issuerId,
			dateExpr : msg.dateExpr,
			stlmDate : msg.stlmDate,

			cardNo : msg.cardNo,
			paymentId : msg.paymentId,
			transType : msg.transType,
			batchNo : msg.batchNo,
			traceNo : msg.traceNo,
			transTime : msg.transTime,
			transAmount : msg.transAmount
		};
		//cache processing data in memory
		dataForBatchTask = req;
		Net.asynConnect(action, req, actionAfterProcess);
	};

	ConsumptionData.actionAfterProcess = function(data) {
		if (data.responseCode != "0") {
			// Scene.alert(data.errorMsg);
			//save original data on processing batch failure
			// saveProcessBatchTask(ConsumptionData.dataForBatchTask);
		} else {
			//remove the record from cache db
			// window.RMS.rmBatchByTxnId(ConsumptionData.dataForBatchTask);
		}

		//remove item from array
		/*
		 removeItemFromArray(ConsumptionData.dataForBatchTask);
		 if (ConsumptionData.dataForBatchArray != null && ConsumptionData.dataForBatchArray.length > 0) {
		 setTimeout(function() {
		 startSingleBatchTask(ConsumptionData.dataForBatchArray[0]);
		 }, 1000);
		 }*/

	};

	ConsumptionData.removeItemFromArray = function(data) {
		var index = dataForBatchArray.indexOf(data);
		if (index > -1) {
			dataForBatchArray.splice(index, 1);
		}
	};

	ConsumptionData.startProcessBatchTasks = function() {
		//get all cached batch
		window.RMS.readLocalBatch(actionAfterGetBatch);
	};

	ConsumptionData.actionAfterGetBatch = function(data) {
		var dataArray = JSON.parse(data);
		ConsumptionData.dataForBatchArray = dataArray;
		if (dataArray != null && dataArray.length > 0) {
			startSingleBatchTask(dataArray[0]);
		}
	};

	ConsumptionData.resetDataForBalance = function() {
		ConsumptionData.dataForBalance = {
			"typeOf8583" : "chaxunyue",
			"track2" : "",
			"track3" : "",
			"balancePwd" : "",
			"cardID" : "",
			"openBrh" : "",
			"paymentId" : "",
		};
	};

	ConsumptionData.resetMultiData = function() {
		ConsumptionData.dataForMultiPay = {
			"orderList" : [],
			"flowList" : [{
				"methods" : ["10"],
				"desc" : "请输入交易总金额",
				"matchRegex" : "",
				"inputRegex" : "",
				"packTag" : "totalAmount",
			}],
			"step" : 0,
			"completed" : false,
		};
	};

	return ConsumptionData;
});
