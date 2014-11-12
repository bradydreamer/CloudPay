;
(function() {
	if (window.ConsumptionData) {
		return;
	};

	var dataForPayment;

	var dataForCancellingOrder;

	var dataForBalance;
	
	var dataForMultiPay;
	
	var dataForBatchTask;			//use for caching single batch in memory
	var dataForBatchArray;			//use for caching local batch tasks in memory
	

	function resetConsumptionData() {
		ConsumptionData.dataForPayment = {
			"typeOf8583": "pay",
			"openBrh": "",
			"paymentId": "",
			"flowList": [],
			"step": 0
		};
	}

	function resetDataForCancellingOrder() {
		ConsumptionData.dataForCancellingOrder = {
			"rrn": "",
			"openBrh": "",
			"paymentId": "",
			"transData8583": "",
			"req8583": "",
			"res8583": "",
			"step": 0,
		};
	}
	
	function saveProcessBatchTask (data) {
		var msg = JSON.parse(data);
		msg.pk_id = util.uniqueId();
		ConsumptionData.uniqueId = msg.pk_id;
		ConsumptionData.singleTask = true;
	  	window.RMS.save("batchCache", msg);
	}
	
	function startSingleBatchTask (data) {
		var msg = JSON.parse(data);
		var action = msg.action;
		var req = {
	  		txnId : msg.txnId,
	  		oriTxnId : msg.oriTxnId,
	  		resCode: msg.resCode,
			resMsg: msg.resMsg,
	  		refNo: msg.refNo,
	  		authNo: msg.authNo,
	  		issuerId: msg.issuerId,
	  		dateExpr: msg.dateExpr,
	  		stlmDate: msg.stlmDate,
	  		
	  		cardNo: msg.cardNo,
	  		paymentId: msg.paymentId,
	  		transType: msg.transType,
	  		batchNo: msg.batchNo,
	  		traceNo: msg.traceNo,
	  		transTime: msg.transTime,
	  		transAmount: msg.transAmount
	  	};
	  	
	  	//cache processing data in memory
	  	ConsumptionData.dataForBatchTask = msg;
	  	
	  	if (ConsumptionData.singleTask) {
		  	ConsumptionData.dataForBatchTask.pk_id = ConsumptionData.uniqueId;
		  	//clear pk_id cache
		  	ConsumptionData.uniqueId = null;
	  	}
	  	Net.asynConnect(action, req, actionAfterProcess, true);
	}
	
	function actionAfterProcess (data) {
	  	if (data.responseCode != "0") {
		    // Scene.alert(data.errorMsg);
		    //save original data on processing batch failure 
		    //saveProcessBatchTask(ConsumptionData.dataForBatchTask);
		    if (data.responseCode == "1" || data.responseCode == "13") {
		    	window.RMS.rmBatchByPrimaryKey(ConsumptionData.dataForBatchTask);
		    }
	  	} else {
	  		//cache txnId for mispos --start mod by Teddy on 10th November
	  		ConsumptionData.dataForPayment.txnId = data.txnId;
	  		//cache txnId for mispos --end mod by Teddy on 10th November
	  		
		  	//remove the record from cache db
		  	window.RMS.rmBatchByPrimaryKey(ConsumptionData.dataForBatchTask);
	  	}
	  	
	  	//remove item from array
		removeItemFromArray(ConsumptionData.dataForBatchTask);
		  
		if (ConsumptionData.dataForBatchArray != null && ConsumptionData.dataForBatchArray.length > 0) {
			dealingBatch(ConsumptionData.dataForBatchArray);
		}
	}
	
	function removeItemFromArray (data) {
		if (ConsumptionData.dataForBatchArray != null && ConsumptionData.dataForBatchArray.length > 0) {
			ConsumptionData.dataForBatchArray.splice(0, 1);
		}
		
	}
	
	function startProcessBatchTasks () {
	  	ConsumptionData.singleTask = false;
	  	//get all cached batch
	  	window.RMS.readLocalBatch(actionAfterGetBatch);
	}
	
	function actionAfterGetBatch (data) {
		ConsumptionData.dataForBatchArray = data;
		dealingBatch(data);
	}
	
	function dealingBatch (data) {
		if (data != null && data.length > 0) {
			
	  		startSingleBatchTask(JSON.stringify(data[0]));
		}
		
	}

	function resetDataForBalance() {
		ConsumptionData.dataForBalance = {
			"typeOf8583": "chaxunyue",
			"track2": "",
			"track3": "",
			"balancePwd": "",
			"cardID": "",
			"openBrh": "",
			"paymentId": ""
		};
	}
	
	function resetMultiData() {		
		ConsumptionData.dataForMultiPay = {
			"orderList": [],
			"flowList" : [{
				"methods" : ["10"],
				"desc" : "请输入交易总金额",
				"matchRegex" : "",
				"inputRegex" : "",
				"packTag" : "totalAmount"
			}],
			"step" : 0,
			"completed": false,
		}; 
	}

	window.ConsumptionData = {
		"dataForPayment": dataForPayment,
		"resetConsumptionData": resetConsumptionData,
		"dataForCancellingOrder": dataForCancellingOrder,
		"resetDataForCancellingOrder": resetDataForCancellingOrder,
		"dataForBalance": dataForBalance,
		"resetDataForBalance": resetDataForBalance,
		"dataForMultiPay": dataForMultiPay,
		"resetMultiData": resetMultiData,
		"dataForBatchTask": dataForBatchTask,
		"dataForBatchArray": dataForBatchArray,
		"saveProcessBatchTask": saveProcessBatchTask,
		"startProcessBatchTasks": startProcessBatchTasks,
		"startSingleBatchTask": startSingleBatchTask
	};

	resetConsumptionData();
	resetDataForCancellingOrder();
	resetDataForBalance();
	resetMultiData();
})();