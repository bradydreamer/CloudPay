
var transType_Consume = 1021;
var transType_ConsumeCancel = 3021;
var transType_Refund = 3051;
var transType_PreAuth = 1011;
var transType_preAuthComplete= 1031;
var transType_preAuthSettlement = 1091;
var transType_preAuthCancel = 3011;
var transType_preAuthCompleteCancel = 3031;

Pay.reverseCallBack = function() {
};
Pay.reverseOrder = function(callBack) {
	Pay.reverseCallBack = callBack;
	window.RMS.read("savedTransData", reverseOrCloseIfNeed);

	function reverseOrCloseIfNeed(params) {
		if (null != params && null != params.data) {

			if ("reverse" == params.type) {
				var reverseData = {
					"data8583" : params.data,
					"transDate" : params.transDate,
					"subType" : params.subType,
					"typeOf8583" : "chongZheng",
					"paymentId": params.paymentId,
				};
				ConsumptionData.dataForPayment.brhKeyIndex = params.brhKeyIndex;
				//window.data8583.get8583(reverseData, afterGetReverse8583);
				Pay.checkTransReverse("msc/pay/reverse",function(){
					window.data8583.get8583(reverseData, afterGetReverse8583);
				});	
			}
		} else if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		}

		function afterGetReverse8583(params) {
			var req = {
				"data" : params.data8583,
				"paymentId": params.paymentId,
				"transType": params.transType,
				"batchNo": params.batchNo,
				"traceNo": params.traceNo,
				"transTime": params.transTime,
				"cardNo": params.cardNo,
				"transAmount": params.transAmount,
				"oriTxnId": params.oriTxnId,
				"oriBatchNo": params.oriBatchNo,
				"oriTraceNo": params.oriTraceNo,
				"oriTransTime": params.oriTransTime,
			};		
			Net.connect("msc/pay/reverse", req, Pay.reverseResult, true);
		}

	}

};

Pay.reverseResult = function(params) {
	ConsumptionData.dataForPayment.txnId = params.txnId;
	if (params.responseCode == "0") {
		var params = {
			data8583 : params.data,
		};		
		window.data8583.convert8583(params, afterConvertBackup);
	} else if(params.responseCode == "1"){
		window.RMS.clear("savedTransData");
		if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		};
	}else{	
		Scene.alert("冲正失败，请联系代理机构");
		if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		};
	}
	
	function afterConvertBackup(params){
		params.action = "msc/txn/update";
		params.txnId = ConsumptionData.dataForPayment.txnId;
		params.refNo = params.rrn;
		var data = JSON.stringify(params);
		ConsumptionData.saveProcessBatchTask(data);
		ConsumptionData.startSingleBatchTask(data);
		afterConvertReverseRes(params);
		
	}
	
	function afterConvertReverseRes(params) {
		if ("00" == params.resCode || "25" == params.resCode 
			|| "12" == params.resCode || "40" == params.resCode
			|| "B1" == params.resCode || "B2" == params.resCode
			|| "B3" == params.resCode || "B4" == params.resCode) {
			window.RMS.clear("savedTransData");
		} else {
			Scene.alert(params.resMessage);
		}
		if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		};
	}

};

var oriData = {};
Pay.cancelCallBack = function() {	
};
Pay.authTransCallBack = function(){
};

Pay.authCancelOrder = function(params,callBack){
	oriData.params = params;
	oriData.transType = transType_preAuthCancel;
	if(params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已过期");
		return;
	}
	Pay.authTransCallBack = callBack;
	ConsumptionData.resetConsumptionData();
	ConsumptionData.dataForPayment.txnId = params.txnId; // for cancel.
	if(window.merchSettings == null){
		window.RMS.read("merchSettings",Pay.getProductInfo);
	}else{
		Pay.getProductInfo(window.merchSettings);
	}	
};

Pay.authCompleteCancelOrder = function(params,callBack){
	oriData.params = params;
	oriData.transType = transType_preAuthCompleteCancel;
	if(params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已过期");
		return;
	}
	Pay.authTransCallBack = callBack;
	ConsumptionData.resetConsumptionData();
	ConsumptionData.dataForPayment.txnId = params.txnId; // for cancel.
	if(window.merchSettings == null){
		window.RMS.read("merchSettings",Pay.getProductInfo);
	}else{
		Pay.getProductInfo(window.merchSettings);
	}	
};

Pay.authCompleterOrder = function(params,callBack){
	oriData.params = params;
	oriData.transType = transType_preAuthComplete;
	if(params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已过期");
		return;
	}
	Pay.authTransCallBack = callBack;
	ConsumptionData.resetConsumptionData();
	ConsumptionData.dataForPayment.txnId = params.txnId; // for cancel.
	if(window.merchSettings == null){
		window.RMS.read("merchSettings",Pay.getProductInfo);
	}else{
		Pay.getProductInfo(window.merchSettings);
	}	
};

Pay.authSettlementOrder = function(params,callBack){
	oriData.params = params;
	oriData.transType = transType_preAuthSettlement;
	if(params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已过期");
		return;
	}
	Pay.authTransCallBack = callBack;
	ConsumptionData.resetConsumptionData();
	ConsumptionData.dataForPayment.txnId = params.txnId; // for cancel.
	if(window.merchSettings == null){
		window.RMS.read("merchSettings",Pay.getProductInfo);
	}else{
		Pay.getProductInfo(window.merchSettings);
	}	
};

Pay.cancelOrder = function(params, callBack) {
	oriData.params = params;
	oriData.transType = transType_ConsumeCancel;
	if (params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已经过期", function() {
			if (ConsumptionData.dataForPayment.isExternalOrder) {
				Pay.errRestart();
			}
		});
		return;
	};
	Pay.cancelCallBack = callBack;
	ConsumptionData.resetDataForCancellingOrder();
	ConsumptionData.dataForCancellingOrder.txnId = params.txnId
	ConsumptionData.dataForPayment.txnId = params.txnId; // for cancel.
	if (window.merchSettings == null) {
		window.RMS.read("merchSettings", Pay.getProductInfo);
	} else {
		Pay.getProductInfo(window.merchSettings);
	}
};

Pay.getProductInfo = function(data){
	var payType;
	var product = window.util.getProductInfo(data, oriData.params.openBrh, oriData.params.paymentId);
	ConsumptionData.dataForPayment.brhKeyIndex = product.brhKeyIndex; // for sign in
	ConsumptionData.dataForCancellingOrder.brhKeyIndex = product.brhKeyIndex;
	ConsumptionData.dataForCancellingOrder.paymentName = product.paymentName;
	ConsumptionData.dataForCancellingOrder.openBrh = oriData.params.openBrh;
	if(oriData.transType == transType_ConsumeCancel){
		payType = transType_ConsumeCancel;
	}else if(oriData.transType == transType_preAuthComplete){
		payType = transType_preAuthComplete;
	}else if(oriData.transType == transType_preAuthSettlement){
		payType = transType_preAuthSettlement;
	}else if(oriData.transType == transType_preAuthCancel){
		payType = transType_preAuthCancel;
	}else if(oriData.transType == transType_preAuthCompleteCancel){
		payType = transType_preAuthCompleteCancel;
	}	
	if (product == null || product[payType] == null || product[payType].length == 0) {
		Scene.alert("不支持该交易！" + JSON.stringify(product));
		return;
	}
	
	if (window.payTemplates == null) {
		window.RMS.read("templateList", function(data) {
			window.payTemplates = data;
			confirmMethod(product, window.payTemplates[product[payType]]);
		});
	} else {
		confirmMethod(product, window.payTemplates[product[payType]]);
	}
	
	function confirmMethod(product, flowList) {
		if (flowList != null && typeof (flowList) == "string") {
			flowList = JSON.parse(flowList);
			flowList = JSON.parse(flowList);
		}
		if(oriData.transType == transType_ConsumeCancel){
			ConsumptionData.dataForCancellingOrder.rrn = oriData.params.rrn;
			ConsumptionData.dataForCancellingOrder.transTime = oriData.params.transTime;
			ConsumptionData.dataForCancellingOrder.transDate = oriData.params.transTime.substring(4, 8);
			ConsumptionData.dataForCancellingOrder.transData8583 = oriData.params.transData8583;
			ConsumptionData.dataForCancellingOrder.transAmount = oriData.params.transAmount;
			ConsumptionData.dataForCancellingOrder.paymentId = oriData.params.paymentId;
			ConsumptionData.dataForCancellingOrder.transType = oriData.params.transType;
			ConsumptionData.dataForCancellingOrder.typeOf8583 = "cheXiao";
			ConsumptionData.dataForCancellingOrder.flowList = flowList;	
			ConsumptionData.dataForCancellingOrder.step = 0;
			Pay.gotoCancelFlow();
		}else if(oriData.transType == transType_preAuthComplete){
			ConsumptionData.dataForPayment.typeOf8583 = "preAuthComplete";
			ConsumptionData.dataForPayment.rrn = oriData.params.rrn;
			ConsumptionData.dataForPayment.transDate = oriData.params.transTime.substring(4, 8);
			ConsumptionData.dataForPayment.transData8583 = oriData.params.transData8583;
			//ConsumptionData.dataForPayment.transAmount = oriData.params.transAmount;
			ConsumptionData.dataForPayment.paymentId = oriData.params.paymentId;	
			ConsumptionData.dataForPayment.transType = oriData.params.transType;
			ConsumptionData.dataForPayment.flowList = flowList;	
			ConsumptionData.dataForPayment.step = 0;
			Pay.gotoPreAuthCompleteFlow();
		}else if(oriData.transType == transType_preAuthSettlement){
			ConsumptionData.dataForPayment.typeOf8583 = "preAuthSettlement";
			ConsumptionData.dataForPayment.rrn = oriData.params.rrn;
			ConsumptionData.dataForPayment.transDate = oriData.params.transTime.substring(4, 8);
			ConsumptionData.dataForPayment.transData8583 = oriData.params.transData8583;
			//ConsumptionData.dataForPayment.transAmount = oriData.params.transAmount;
			ConsumptionData.dataForPayment.paymentId = oriData.params.paymentId;
			ConsumptionData.dataForPayment.transType = oriData.params.transType;
			ConsumptionData.dataForPayment.flowList = flowList;	
			ConsumptionData.dataForPayment.step = 0;
			Pay.gotoPreAuthSettlementFlow();
		}else if(oriData.transType == transType_preAuthCancel){
			ConsumptionData.dataForPayment.typeOf8583 = "preAuthCancel";
			ConsumptionData.dataForPayment.rrn = oriData.params.rrn;
			ConsumptionData.dataForPayment.transDate = oriData.params.transTime.substring(4, 8);
			ConsumptionData.dataForPayment.transData8583 = oriData.params.transData8583;
			ConsumptionData.dataForPayment.transAmount = oriData.params.transAmount;
			ConsumptionData.dataForPayment.paymentId = oriData.params.paymentId;
			ConsumptionData.dataForPayment.transType = oriData.params.transType;
			ConsumptionData.dataForPayment.flowList = flowList;	
			ConsumptionData.dataForPayment.step = 0;
			Pay.gotoPreAuthCancelFlow();
		}else if(oriData.transType == transType_preAuthCompleteCancel){
			ConsumptionData.dataForPayment.typeOf8583 = "preAuthCompleteCancel";
			ConsumptionData.dataForPayment.rrn = oriData.params.rrn;
			ConsumptionData.dataForPayment.transDate = oriData.params.transTime.substring(4, 8);
			ConsumptionData.dataForPayment.transData8583 = oriData.params.transData8583;
			ConsumptionData.dataForPayment.transAmount = oriData.params.transAmount;
			ConsumptionData.dataForPayment.paymentId = oriData.params.paymentId;	
			ConsumptionData.dataForPayment.transType = oriData.params.transType;
			ConsumptionData.dataForPayment.flowList = flowList;	
			ConsumptionData.dataForPayment.step = 0;
			Pay.gotoPreAuthCompleteCancelFlow();
		}
		
	}

};

Pay.cancelOrderConvertReq = function(params) {
	//window.data8583.get8583(ConsumptionData.dataForCancellingOrder, Pay.cancelOrderExe);
	Pay.checkTransReverse("msc/pay/consume/cance",function(){
		window.data8583.get8583(ConsumptionData.dataForCancellingOrder, Pay.cancelOrderExe);
	});	
};

Pay.cancelOrderExe = function(params) {
	var req = {
		"data" : params.data8583,
		"paymentId": params.paymentId,
		"transType": params.transType,
		"batchNo": params.batchNo,
		"traceNo": params.traceNo,
		"transTime": params.transTime,
		"cardNo": params.cardNo,
		"transAmount": params.transAmount,
		"oriTxnId": ConsumptionData.dataForCancellingOrder.txnId,
		"oriBatchNo": params.oriBatchNo,
		"oriTraceNo": params.oriTraceNo,
		"oriTransTime": params.oriTransTime,
	};

	if(params.cardNo != "9999999999999999" && params.cardNo != ConsumptionData.dataForCancellingOrder.F02){
		Scene.alert("刷卡错误，请刷原卡!",function(){
//			Scene.goBack("OrderDetail");
			if (ConsumptionData.dataForPayment.isExternalOrder) {
				Pay.errRestart();
			} else {
				Scene.goBack("OrderDetail");
			}
		});
		return;
	}
	
	if (!isTodayOrder(ConsumptionData.dataForCancellingOrder)) {
		Scene.alert("交易已经过期", function() {
//			Scene.goBack("OrderDetail");
			if (ConsumptionData.dataForPayment.isExternalOrder) {
				Pay.errRestart();
			} else {
				Scene.goBack("OrderDetail");
			}
		});
		return;
	}
	
	ConsumptionData.dataForCancellingOrder.req8583 = req.data;

	var action = "msc/pay/consume/cancel";
	if (ConsumptionData.dataForCancellingOrder.typeOf8583 == "refund") {
		action = "msc/pay/refund";
	}
	Net.connect(action, req, afterCancelPayOrder, true);

	function afterCancelPayOrder(data) {
		if (data.responseCode == "0") {
			var params = {
				"data8583" : data.data,
			};
			ConsumptionData.dataForCancellingOrder.res8583 = data.data;
			ConsumptionData.dataForPayment.txnId = data.txnId;
			ConsumptionData.dataForCancellingOrder.txnId = data.txnId;
			window.data8583.convert8583(params, afterConvert8583);
		} else {
			Pay.reverseOrder(Pay.cancelEnd);
		}
	}

	function afterConvert8583(params){
		//Pay.transBackup(params);
		params.action = "msc/txn/update";
		params.txnId = ConsumptionData.dataForCancellingOrder.txnId;
		params.refNo = params.rrn;
		var data = JSON.stringify(params);
		ConsumptionData.saveProcessBatchTask(data);
		ConsumptionData.startSingleBatchTask(data);
		Pay.cancelOrderResult(params);		
	}
	
	function isTodayOrder(params) {
		//check date on reverse --start mod by Teddy 11th July
		var currentDate = new Date();
		var currentYear = currentDate.getFullYear();
		var currentMonth = currentDate.getMonth();
		var currentDay = currentDate.getDate();
		
		var formatedTransDateStr = params.transTime.substring(0,4) + "," + params.transTime.substring(4,6) + "," + params.transTime.substring(6,8);
		var formatedTransDate = new Date(formatedTransDateStr);
		var transYear = formatedTransDate.getFullYear();
		var transMonth = formatedTransDate.getMonth();
		var transDay = formatedTransDate.getDate();
		Scene.alert("JSLOG, isTodayOrder:" + formatedTransDateStr);
		//fix smtp-151
		if (transYear != currentYear || transMonth != currentMonth || transDay != currentDay) {
			
			return false;
		} else {
			return true;
		}
	}

};

Pay.cashCancelOrder = function(params,cashCancelCallback){
	var transAmount = util.yuan2fenStr(params.transAmount);
	var req ={
			"transType": params.transType,
			"paymentId": params.paymentId,
			"batchNo": params.batchNo,
			"traceNo": params.traceNo,
			"transTime": params.oriTransTime,
			"transAmount": transAmount,
			"oriTxnId": params.txnId,
			"resCode": params.resCode,
			"resMsg": params.resMsg
		}
	Net.asynConnect("txn/"+params.payKeyIndex,req,afterCashCancelOrder);
	
	function afterCashCancelOrder(params){
		if(params.responseCode == "0"){
			if(cashCancelCallback){
				cashCancelCallback();
			}		
		}else{
			Scene.goBack("OrderDetail");
			Scene.alert(params.resMessage);
		}			
	}	
}

Pay.cancelOrderResult = function(params) {
	window.Database.insertTransData8583(ConsumptionData.dataForCancellingOrder.txnId, ConsumptionData.dataForCancellingOrder.req8583, ConsumptionData.dataForCancellingOrder.res8583);
	if ("00" == params.resCode) {
		window.RMS.clear("savedTransData");
		
		//set revoke data --start
		ConsumptionData.dataForPayment.transType = ConsumptionData.dataForCancellingOrder.transType;
		setConsumptionData();
		ConsumptionData.dataForPayment.result = "success";
		ConsumptionData.dataForPayment.paymentId = ConsumptionData.dataForCancellingOrder.paymentId;
		ConsumptionData.dataForPayment.openBrh = ConsumptionData.dataForCancellingOrder.openBrh;
		ConsumptionData.dataForPayment.paymentName = ConsumptionData.dataForCancellingOrder.paymentName;
		//set revoke data --end
		
		setTimeout(function() {
			window.posPrint.printTrans(ConsumptionData.dataForCancellingOrder.txnId);
			ConsumptionData.resetDataForCancellingOrder();
		}, 300);
		
		if (Pay.cancelCallBack) {
			Pay.cancelCallBack();
			Pay.cancelCallBack = null;
		};
	} else if ("98" == params.resCode || "68" == params.resCode) {
		Pay.reverseOrder(Pay.cancelEnd);
	} else if("C1" == params.resCode){
		Scene.alert(params.resMessage,function(){
			Pay.reverseOrder(Pay.restart);
			});
	}else if("C2" == params.resCode){
		Scene.alert(params.resMessage,function(){
			Pay.reverseOrder(Pay.cancelEnd);
			});
	}else if("C3" == params.resCode){
		Scene.alert(params.resMessage,function(){
			Pay.reverseOrder(Pay.cancelEnd);
			});
	}else if("C4" == params.resCode){
		Scene.alert(params.resMessage,function(){
			Pay.reverseOrder(Pay.cancelEnd);
			});
	}else {
		Pay.cancelEnd();
		setTimeout(function() {
			Scene.alert(params.resMessage);
		}, 300);
	}
	
	//init consumption data for revoke order add by Teddy on 19th August -- start
	function setConsumptionData() {
		ConsumptionData.dataForPayment.rrn = params.rrn;
		// ConsumptionData.dataForPayment.result = "success"; 
		ConsumptionData.dataForPayment.transTime = params.transTime;
		ConsumptionData.dataForPayment.transAmount = params.transAmount;
		ConsumptionData.dataForPayment.bankCardNum = params.cardNum;
		ConsumptionData.dataForPayment.issuerName = params.iusserName;
		ConsumptionData.dataForPayment.alipayAccount = params.alipayAccount;
		ConsumptionData.dataForPayment.alipayPID = params.alipayPID;
		ConsumptionData.dataForPayment.apOrderId = params.apOrderId;
		ConsumptionData.dataForPayment.alipayTransactionID = params.alipayTransactionID;
		ConsumptionData.dataForPayment.txnId = ConsumptionData.dataForCancellingOrder.txnId;
	}
	//init consumption data for revoke order add by Teddy on 19th August -- end
};

Pay.cancelEnd = function() {
//	Scene.goBack("OrderDetail");
	Pay.errRestart();
};

Pay.refundOrder = function(params, callBack) {
	if (params.transData8583 == null || params.transData8583 == "") {
		Scene.alert("交易已经过期");
		return;
	};
	Pay.cancelCallBack = callBack;

	ConsumptionData.resetDataForCancellingOrder();

	var open_brh = params.openBrh;
	var payment_id = params.paymentId;

	if (window.merchSettings == null) {
		window.RMS.read("merchSettings", getProductInfo);
	} else {
		getProductInfo(window.merchSettings);
	}

	function getProductInfo(data) {
		var product = window.util.getProductInfo(data, open_brh, payment_id);
		if (product == null || product.refundTemplate == null || product.refundTemplate.length == 0) {
			Scene.alert("该交易不支持退货" + JSON.stringify(product));
			return;
		}
		if (window.refundTemplates == null) {
			window.RMS.read("refundTemplates", function(data) {
				window.refundTemplates = data;
				confirmMethod(product, window.refundTemplates[product.refundTemplate]);
			});
		} else {
			confirmMethod(product, window.refundTemplates[product.refundTemplate]);
		}
	}

	function confirmMethod(product, flowList) {
		if (flowList != null && typeof (flowList) == "string") {
			flowList = JSON.parse(flowList);
		}
		ConsumptionData.dataForCancellingOrder.rrn = params.rrn;
		ConsumptionData.dataForCancellingOrder.transDate = params.transTime.substring(4, 8);
		ConsumptionData.dataForCancellingOrder.transData8583 = params.transData8583;
		ConsumptionData.dataForCancellingOrder.transAmount = params.transAmount;
		ConsumptionData.dataForCancellingOrder.paymentId = params.paymentId;

		ConsumptionData.dataForCancellingOrder.typeOf8583 = "refund";
		ConsumptionData.dataForCancellingOrder.flowList = flowList;

		Pay.gotoCancelFlow();
	}

};

