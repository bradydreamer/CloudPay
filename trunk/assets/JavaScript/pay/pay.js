function Pay() {};

Pay.callBack = function() {};

Pay.convertSendCodeReq = function() {
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exeSendCode);
};

Pay.exeSendCode = function(params) {
	var req = {
		"data": params.data8583,
	};
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	Net.connect("msc/pay/consume", req, actionAfterSend, true);

	function actionAfterSend(params) {
		window.RMS.clear("savedTransData");

		if (params.responseCode == "0") {
			ConsumptionData.dataForPayment.isSendCode = null;

			var convertData = {
				"data8583": params.data,
			};
			window.data8583.convert8583(convertData, actionAfterConvert);
		} else {
			Scene.alert(params.errorMsg,Pay.errRestart);
		}
	}

	function actionAfterConvert(params) {
		if ("00" == params.resCode) {
			ConsumptionData.dataForPayment["F40_6F10"] = params.apOrderId;
			ConsumptionData.dataForPayment["F40_6F08"] = params.payOrderBatch;
			ConsumptionData.dataForPayment.needAuthCode = true;
			var currentStep = ConsumptionData.dataForPayment.step;
			ConsumptionData.dataForPayment.step = currentStep + 1;

			Pay.gotoPayFlow();
		} else {
			Scene.alert(params.resMessage,Pay.errRestart);
		}
	};
};

Pay.convertPayReq = function() {
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePay);
};

Pay.exePay = function(params) {
	var req = {
		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
	Net.connect("msc/pay/consume", req, actionAfterPay, true);

	function actionAfterPay(params) {
		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};
			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			//Scene.alert(params.errorMsg,errorProcess);
			Pay.reverseOrder(Pay.restart);
		}
	}
};

var transBackupTime = 0;
Pay.transBackup = function(params){
	
	//window.RMS.read("transBackupInfo",);
	var req = {
   		"txnId": ConsumptionData.dataForPayment.txnId,
   		"resCode": params.resCode,
   		"resMsg": params.resMessage,
   		"refNo": params.rrn,
   		"authNo": params.authNo,
   		"issuerId": params.issuerId,
   		"dateExpr": params.dateExpr,
   		"stlmDate": params.stlmDate,
   	};	
	window.RMS.save("transBackupInfo",req);
	
	Net.asynConnect("msc/txn/update",req,afterBackupInfo);	
	function afterBackupInfo(data){
		if("0" == data.responseCode){
			transBackupTime = 0;
			window.RMS.clear("transBackupInfo");
		}else{
			if(transBackupTime < 3){
				Net.asynConnect("msc/txn/update",req,afterBackupInfo);
				transBackupTime++;
			}else{
				transBackupTime = 0;
			}
		}	
	}
};

Pay.backupInfo = function(params){
	
	ConsumptionData.dataForPayment.rrn = params.rrn;
	ConsumptionData.dataForPayment.transTime = params.transTime;
	ConsumptionData.dataForPayment.bankCardNum = params.cardNum;
	ConsumptionData.dataForPayment.issuerName = params.iusserName;
	ConsumptionData.dataForPayment.alipayAccount = params.alipayAccount;
	ConsumptionData.dataForPayment.alipayPID = params.alipayPID;
	ConsumptionData.dataForPayment.apOrderId = params.apOrderId;
	ConsumptionData.dataForPayment.alipayTransactionID = params.alipayTransactionID;
	ConsumptionData.dataForPayment.authNo = params.authNo;
	window.Database.insertTransData8583(
		ConsumptionData.dataForPayment.txnId,
		ConsumptionData.dataForPayment.req8583,
		ConsumptionData.dataForPayment.res8583
	);
	Pay.payResult(params);
	Pay.transBackup(params);
};

Pay.payResult = function(params) {
	// params.resCode ＝ "98";
	//	ConsumptionData.dataForPayment.rrn = params.rrn;
	//	ConsumptionData.dataForPayment.transTime = params.transTime;
	//	ConsumptionData.dataForPayment.bankCardNum = params.cardNum;
	//	ConsumptionData.dataForPayment.issuerName = params.iusserName;
	//	ConsumptionData.dataForPayment.alipayAccount = params.alipayAccount;
	//	ConsumptionData.dataForPayment.alipayPID = params.alipayPID;
	//	ConsumptionData.dataForPayment.apOrderId = params.apOrderId;
	//	ConsumptionData.dataForPayment.alipayTransactionID = params.alipayTransactionID;
	//
	//	window.Database.insertTransData8583(
	//		ConsumptionData.dataForPayment.rrn,
	//		ConsumptionData.dataForPayment.req8583,
	//		ConsumptionData.dataForPayment.res8583
	//	);

	if ("00" != params.resCode) {
		if ("98" != params.resCode) {
			window.RMS.clear("savedTransData");
			
			setTimeout(function() {
				Scene.alert(params.resMessage, function(){
					Pay.restart();
				});
			}, 300);
		} else {
			Pay.reverseOrder(Pay.restart);
		}
	} else {
		window.RMS.clear("savedTransData");
		if (Pay.authTransCallBack) {
			Pay.authTransCallBack();
			Pay.authTransCallBack = null;
		};
		setTimeout(function() {
			window.posPrint.printTrans(ConsumptionData.dataForPayment.txnId);
		}, 300);
		showDetail();
	}

	function showDetail() {
		var payTypeDesc = "" + ConsumptionData.dataForPayment.paymentName;
		ConsumptionData.dataForPayment.payTypeDesc = payTypeDesc;
		var transTime = util.formatDateTime(ConsumptionData.dataForPayment.transTime);
		var transAmount = util.formatAmountStr(ConsumptionData.dataForPayment.transAmount);

		var msg = {
			"refNo": ConsumptionData.dataForPayment.rrn,
			"orderStateDesc": "成功",
			"payTypeDesc": payTypeDesc,
			"transAmount": transAmount,
			"transTime": transTime,
			"transTypeDesc": ConsumptionData.dataForPayment.typeName,
			"openBrh": ConsumptionData.dataForPayment.openBrh,
			"paymentId": ConsumptionData.dataForPayment.paymentId,
			"paymentOrder": 1,
			"openBrhName": ConsumptionData.dataForPayment.openBrhName,
			"brhMchtId": ConsumptionData.dataForPayment.brhMchtId,
			"brhTermId": ConsumptionData.dataForPayment.brhTermId,
			"merchId": ConsumptionData.dataForPayment.merchId,
			"iposId": ConsumptionData.dataForPayment.iposId,
			"transType": ConsumptionData.dataForPayment.transType,
			"authNo": ConsumptionData.dataForPayment.authNo,
		};
		
		if(ConsumptionData.dataForPayment.preScene == "PayAccount" || ConsumptionData.dataForPayment.preScene == "PinPad"){
			msg.shouldRemoveCurCtrl = true;
			ConsumptionData.dataForPayment.preScene = null;
		}
		
		msg.confirm = "Pay.succRestart";
		Scene.showScene("OrderDetail", "", msg);
	}
};

Pay.succRestart = function() {
	window.RMS.clear("savedTransData");
	ConsumptionData.dataForPayment.result = "success";
	Pay.restart();
};

Pay.errRestart = function(){
	Pay.restart();
}

Pay.misposSuccRestart = function(params) {
	var data = JSON.parse(params);
	window.RMS.clear("savedTransData");
	ConsumptionData.dataForPayment.isExternalOrder = true;
	
	ConsumptionData.dataForPayment.rrn = data.refNo;
	ConsumptionData.dataForPayment.transTime = data.transTime;
	ConsumptionData.dataForPayment.paymentName = data.paymentName;
	ConsumptionData.dataForPayment.transAmount = data.transAmount;
	ConsumptionData.dataForPayment.bankCardNum = data.bankCardNum;
	ConsumptionData.dataForPayment.result = "success";
	Pay.restart();
};

Pay.misposErrRestart = function(params){
	var data = JSON.parse(params);
	ConsumptionData.dataForPayment.transAmount = data.transAmount;
	Pay.restart();
};


Pay.restart = function(params) {

	var order = {
		"refNo" : ConsumptionData.dataForPayment.rrn,
		//"result" : ConsumptionData.dataForPayment.result,
		"orderStateDesc" : ConsumptionData.dataForPayment.result == "success" ? "完成" : "失败",
		"payTime" : ConsumptionData.dataForPayment.transTime,
		"payTypeDesc" : "" + ConsumptionData.dataForPayment.paymentName,
		"transAmount" : ConsumptionData.dataForPayment.transAmount,
		"bankCardNum" : ConsumptionData.dataForPayment.bankCardNum,
		"issuerName" : ConsumptionData.dataForPayment.issuerName,
		"alipayAccount" : ConsumptionData.dataForPayment.alipayAccount,
		"alipayPID" : ConsumptionData.dataForPayment.alipayPID,
		"orderID" : ConsumptionData.dataForPayment.apOrderId,
		"alipayTransactionID" : ConsumptionData.dataForPayment.alipayTransactionID,
		//"showAmount" : ConsumptionData.dataForPayment.transAmount,
	};

	if (ConsumptionData.isMultiPay == true) {
		var orderStateDesc = "成功";
		if (ConsumptionData.dataForPayment.result == "success") {
			var paidAmount = parseInt(ConsumptionData.dataForMultiPay.paidAmount);
			paidAmount += parseInt(ConsumptionData.dataForPayment.transAmount);
			ConsumptionData.dataForMultiPay.paidAmount = "" + paidAmount;
		} else {
			orderStateDesc = "失败";
		}
		ConsumptionData.dataForMultiPay.orderList.push(order);
		var balance = parseInt(ConsumptionData.dataForMultiPay.totalAmount)-parseInt(ConsumptionData.dataForMultiPay.paidAmount);
		var result = "";
		if(balance == parseInt(ConsumptionData.dataForMultiPay.totalAmount)){
			ConsumptionData.dataForMultiPay.result = "0";
		}else if(balance > 0 && balance < parseInt(ConsumptionData.dataForMultiPay.totalAmount)){
			ConsumptionData.dataForMultiPay.result = "1";
		}else if(balance == 0){
			ConsumptionData.dataForMultiPay.result = "2";
		}
		var formData = {
			"totalAmount" : ConsumptionData.dataForMultiPay.totalAmount,
			"paidAmount" : ConsumptionData.dataForMultiPay.paidAmount,
			"result" : ConsumptionData.dataForMultiPay.result,
			"orderList" : ConsumptionData.dataForMultiPay.orderList,
		};
		Scene.goBack("MultiPayRecord", formData);
		if (ConsumptionData.dataForPayment.isExternalOrder == true) {
			return;
		}
	} else {
		if (ConsumptionData.dataForPayment.isExternalOrder) {			
			if (ConsumptionData.dataForMultiPay.completed == true) {
				Scene.goBack("first", {
					"totalAmount" : ConsumptionData.dataForMultiPay.totalAmount,
					"paidAmount" : ConsumptionData.dataForMultiPay.paidAmount,
					"result" : ConsumptionData.dataForMultiPay.result,
					"orderList" : ConsumptionData.dataForMultiPay.orderList,
					});
				ConsumptionData.resetMultiData();
			} else {
				Scene.goBack("first", {
					"totalAmount" : ConsumptionData.dataForPayment.transAmount,
					"paidAmount" : ConsumptionData.dataForPayment.transAmount,
					"result" : ConsumptionData.dataForPayment.result == "success" ? "2" : "0",
					"orderList" : [order],
				});
			}
		} else {
			ConsumptionData.resetMultiData();
			Scene.goBack("Home");
		};
	}
	ConsumptionData.resetConsumptionData();
}; 


Pay.preAuthReq = function(){
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuth);
}

Pay.exePreAuth = function(params){
	var req = {
	   		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
   	Net.connect("msc/pay/prepaid", req, actionAfterPreAuth, true);   	
   	
	function actionAfterPreAuth(params) {
		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			Scene.alert(params.errorMsg,errorProcess);
			//Pay.reverseOrder(Pay.restart);
		}
	}
	
	function errorProcess(){
		Pay.reverseOrder(Pay.restart);
	}
}

Pay.preAuthCompleteReq = function(){
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthComplete);
}

Pay.exePreAuthComplete = function(params){
	var req = {
		   		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
   	Net.connect("msc/pay/prepaid/over", req, actionAfterPreAuthComplete, true);   	
   	
   	function actionAfterPreAuthComplete(params){
   		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			//Scene.alert(params.errorMsg);
			Pay.reverseOrder(Pay.restart);
		}
   	}
   	
}

Pay.preAuthSettlementReq = function(){
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthSettlement);
}

Pay.exePreAuthSettlement = function(params){
	var req = {
		   		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
   	Net.connect("msc/pay/prepaid/over/offline", req, actionAfterPreAuthSettlement, true);   	
   	
   	function actionAfterPreAuthSettlement(params){
   		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			//Scene.alert(params.errorMsg);
			Pay.reverseOrder(Pay.restart);
		}
   	}
   	
}

Pay.preAuthCancelReq = function(){
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCancel);
}

Pay.exePreAuthCancel = function(params){
	var req = {
		   		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
   	Net.connect("msc/pay/prepaid/cancel", req, actionAfterPreAuthCancel, true);   	
   	
   	function actionAfterPreAuthCancel(params){
   		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			//Scene.alert(params.errorMsg);
			Pay.reverseOrder(Pay.restart);
		}
   	}
   	
}

Pay.preAuthCompleteCancelReq = function(){
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCompleteCancel);
}

Pay.exePreAuthCompleteCancel = function(params){
	var req = {
		   		"data": params.data8583,
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
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
   	Net.connect("msc/pay/prepaid/over/cancel", req, actionAfterPreAuthCompleteCancel, true);   	
   	
   	function actionAfterPreAuthCompleteCancel(params){
   		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, Pay.backupInfo);
		} else {
			//Scene.alert(params.errorMsg);
			Pay.reverseOrder(Pay.restart);
		}
   	}
   	
}




