function Pay() {};

Pay.callBack = function() {};

Pay.convertSendCodeReq = function() {
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exeSendCode);
	Pay.checkTransReverse("msc/pay/consume",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exeSendCode);
		});
	
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePay);
	Pay.checkTransReverse("msc/pay/consume",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePay);
		});
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
var needBackupAction = false;
Pay.transBackup = function(params){
	var txnId = ConsumptionData.dataForPayment.txnId;
	var req = {};
	window.RMS.read("transBackupInfo",reBackupInfo);
	function backup(){		
		needBackupAction = false;
		req.txnId = txnId;
   		req.resCode = params.resCode;
   		req.resMsg = params.resMessage;
   		req.refNo = params.rrn;
   		req.authNo = params.authNo;
   		req.issuerId = params.issuerId;
   		req.dateExpr = params.dateExpr;
   		req.stlmDate = params.stlmDate;
		window.RMS.save("transBackupInfo",req);
		Net.asynConnect("msc/txn/update",req,afterBackupInfo);	
	}
	function afterBackupInfo(data){
		if("0" == data.responseCode || "1" == data.responseCode){
			transBackupTime = 0;
			window.RMS.clear("transBackupInfo");
			if(needBackupAction == true){
				backup();
			}			
		}else{
			if(transBackupTime < 3){
				Net.asynConnect("msc/txn/update",req,afterBackupInfo);
				transBackupTime++;
			}else{
				transBackupTime = 0;
				if(needBackupAction == true){
					window.RMS.clear("transBackupInfo");
					backup();
				}
			}
		}
	}

	function reBackupInfo(reqParam){
		if (null != reqParam && (null != reqParam.txnId || null != reqParam.resCode)){
			needBackupAction = true;
			req = reqParam;
			Net.asynConnect("msc/txn/update",reqParam,afterBackupInfo);
		}else{
			backup();
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
	//Pay.transBackup(params);
	params.action = "msc/txn/update";
	params.txnId = ConsumptionData.dataForPayment.txnId;
	params.refNo = params.rrn;
	var data = JSON.stringify(params);
	ConsumptionData.saveProcessBatchTask(data);
	ConsumptionData.startSingleBatchTask(data);
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
		if("98" == params.resCode && "68" == params.resCode){
			Pay.reverseOrder(Pay.restart);
		}else if("C1" == params.resCode){
			Scene.alert(params.resMessage,function(){
				Pay.reverseOrder(Pay.restart);
				});
		}else if("C2" == params.resCode){
			Scene.alert(params.resMessage,function(){
				Pay.reverseOrder(Pay.restart);
				});
		}else if("C3" == params.resCode){
			Scene.alert(params.resMessage,function(){
				Pay.reverseOrder(Pay.restart);
				});
		}else if("C4" == params.resCode){
			Scene.alert(params.resMessage,function(){
				Pay.reverseOrder(Pay.restart);
				});
		}else{
			window.RMS.clear("savedTransData");
			if("22" == params.resCode){
//				Scene.alert(params.resMessage,batchErroProcess);
				batchErroProcess();
			}else if("A0" == params.resCode){
//				Scene.alert(params.resMessage,reSignAction);
				reSignAction();
			}else{				
				setTimeout(function() {
					Scene.alert(params.resMessage, function(){
						Pay.restart();
					});
				}, 300);
			}
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

	function reSignAction(){
		var indexParams = {
				"signature" : false,
			}
		RMS.save(ConsumptionData.dataForPayment.brhKeyIndex, indexParams);
		setTimeout(function(){
			window.util.exeActionWithSigninChecked(function(){
				if(ConsumptionData.dataForPayment.isExternalOrder){
					Pay.restart();
				}else{			
					Scene.goBack("Home");
				}
			},true);
		},300);
	}
	
	function batchErroProcess(){
		SettingsIndex.allTransBatch(SignIn.gotoSignOut);
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
			"txnId": ConsumptionData.dataForPayment.txnId,
			"couponAmount": ConsumptionData.dataForPayment.couponAmount
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
};

Pay.misposSuccRestart = function(params) {
	var data = JSON.parse(params);
	window.RMS.clear("savedTransData");
	ConsumptionData.dataForPayment.isExternalOrder = true;
	
	ConsumptionData.dataForPayment.rrn = data.refNo;
	ConsumptionData.dataForPayment.transTime = data.transTime;
	ConsumptionData.dataForPayment.paymentName = data.paymentName;
	ConsumptionData.dataForPayment.transAmount = data.transAmount;
	ConsumptionData.dataForPayment.paidAmount = data.paidAmount;
	ConsumptionData.dataForPayment.bankCardNum = data.bankCardNum;
	ConsumptionData.dataForPayment.transType = data.transType;
	ConsumptionData.dataForPayment.result = "success";
	Pay.restart();
};

Pay.misposErrRestart = function(params){
	var data = JSON.parse(params);
	ConsumptionData.dataForPayment.transAmount = data.transAmount;
	ConsumptionData.dataForPayment.paidAmount = "0";
	Pay.restart();
};

Pay.writeBackAPMPCouponData = function(params) {
	Scene.alert("JSLOG writeBackAPMPCouponData");
	var couponData = JSON.parse(params);
	var req = {
		"paymentId": ConsumptionData.dataForPayment.paymentId,
		"transType": couponData.transType,
		"batchNo": couponData.batchNo,
		"traceNo": couponData.traceNo,
		"transTime": couponData.transTime,
		"transAmount": couponData.couponPaidAmount,
		"oriTxnId": couponData.oriTxnId,
		"resCode": couponData.resCode,
		"resMsg": couponData.resMsg
	};
	Net.asynConnect("txn/" + ConsumptionData.dataForPayment.keyIndex, req, afterCouponWriteBack);
	
	function afterCouponWriteBack(data) {
		couponData.transAmount = ConsumptionData.dataForPayment.transAmount;
		ConsumptionData.dataForPayment.totalAmount = parseInt(couponData.transAmount);
		ConsumptionData.dataForPayment.couponAmount = parseInt(couponData.couponPaidAmount);
		var balance = parseInt(couponData.transAmount - couponData.couponPaidAmount);
		if (balance > 0) {
			ConsumptionData.dataForPayment.transAmount = balance;
		} else {
			ConsumptionData.dataForPayment.result = "success";
			ConsumptionData.dataForPayment.transAmount = parseInt(couponData.transAmount);
			Pay.restart();
		}
	}
};	

Pay.cashSuccRestart = function(params){
	var cashData = JSON.parse(params);
		if(parseInt(cashData.transAmount) > parseInt(cashData.cashPaidAmount)){
			Scene.alert("您输入的金额不足，请重新输入！");
			return;
		}
		ConsumptionData.dataForPayment.cashPay = true;
		ConsumptionData.dataForPayment.result = "success";
		ConsumptionData.dataForPayment.transAmount = cashData.transAmount;
		ConsumptionData.dataForPayment.paidAmount = cashData.cashPaidAmount;
		ConsumptionData.dataForPayment.changeAmount = cashData.changeAmount;
		ConsumptionData.dataForPayment.transTime = cashData.transTime;
		var req = {
			"paymentId": ConsumptionData.dataForPayment.paymentId,
			"transType": cashData.transType,
			"batchNo": cashData.batchNo,
			"traceNo": cashData.traceNo,
			"transTime": cashData.transTime,
			"transAmount": cashData.transAmount,
			"oriTxnId": cashData.oriTxnId,
			"resCode": cashData.resCode,
			"resMsg": cashData.resMsg
		}
		Net.asynConnect("txn/"+ConsumptionData.dataForPayment.brhKeyIndex,req,afterUpdateInfo);	


	function afterUpdateInfo(data){
		//var data = JSON.parse(params);
		Scene.alert("JSLOG,cash AfterUpdateInfo,data=" + JSON.stringify(data));
		var transTime = cashData.transTime;
		var tDate = transTime.substring(0, 8);
		var tTime = transTime.substring(8);
		tDate = tDate.substring(0,4) + "-" + tDate.substring(4,6) + "-" + tDate.substring(6);
		tTime = tTime.substring(0, 2) + ":" + tTime.substring(2, 4) + ":" + tTime.substring(4);
		transTime = tDate + " " + tTime;
		if("0" == data.responseCode){
			ConsumptionData.dataForPayment.rrn = data.refNo;
			var msg = {
				"refNo": ConsumptionData.dataForPayment.rrn,
				"orderStateDesc": "成功",
				"payTypeDesc": ConsumptionData.dataForPayment.paymentName,
				"transAmount": util.formatAmountStr(cashData.transAmount),
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
				"typeId": ConsumptionData.dataForPayment.typeId,
				"misc": ConsumptionData.dataForPayment.misc
			};
			msg.confirm = "Pay.restart";
			Scene.showScene("OrderDetail", "", msg);
		}else{
			Scene.alert(data.errorMsg,Pay.restart);
		}
	}
};


Pay.restart = function(params) {

	var order = {
		"refNo" : ConsumptionData.dataForPayment.rrn,
		"orderStateDesc" : ConsumptionData.dataForPayment.result == "success" ? "完成" : "失败",
		// "payTime" : ConsumptionData.dataForPayment.transTime,
		"transTime" : ConsumptionData.dataForPayment.transTime,
		"paymentId" : ConsumptionData.dataForPayment.paymentId,
		// "payTypeDesc" : "" + ConsumptionData.dataForPayment.paymentName,
		"paymentIdDesc" : "" + ConsumptionData.dataForPayment.paymentName,
		"transAmount" : ConsumptionData.dataForPayment.transAmount,
		// "bankCardNum" : ConsumptionData.dataForPayment.bankCardNum,
		"accountNo" : ConsumptionData.dataForPayment.bankCardNum,
		"issuerName" : ConsumptionData.dataForPayment.issuerName,
		"alipayAccount" : ConsumptionData.dataForPayment.alipayAccount,
		"alipayPID" : ConsumptionData.dataForPayment.alipayPID,
		"orderID" : ConsumptionData.dataForPayment.apOrderId,
		"alipayTransactionID" : ConsumptionData.dataForPayment.alipayTransactionID,
		// "openBrh" : ConsumptionData.dataForPayment.openBrh,
		"acquId" : ConsumptionData.dataForPayment.openBrh,
		"acquIdDesc" : ConsumptionData.dataForPayment.openBrhName,
		"transType": ConsumptionData.dataForPayment.transType,
		"merchantId": ConsumptionData.dataForPayment.brhMchtId,
		"terminalId": ConsumptionData.dataForPayment.brhTermId,
		"txnId" : ConsumptionData.dataForPayment.txnId
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
					"couponAmount" : ConsumptionData.dataForPayment.couponAmount,
					"orderList" : ConsumptionData.dataForMultiPay.orderList,
					});
				ConsumptionData.resetMultiData();
			} else if(ConsumptionData.dataForPayment.cashPay == true){
			    Scene.alert("JSLOG,CASH PAY." + ConsumptionData.dataForPayment.transAmount + "#" + ConsumptionData.dataForPayment.paidAmount);
				Scene.goBack("first", {
					"totalAmount" : ConsumptionData.dataForPayment.transAmount,
					"paidAmount" : ConsumptionData.dataForPayment.paidAmount,
					"changeAmount": ConsumptionData.dataForPayment.changeAmount,
					"result" : ConsumptionData.dataForPayment.result == "success" ? "2" : "0",
					"orderList" : [order],
				});
			}else {
				Scene.goBack("first", {
					"totalAmount" : ConsumptionData.dataForPayment.transAmount,
					"paidAmount" : ConsumptionData.dataForPayment.result == "success" ? ConsumptionData.dataForPayment.transAmount : "0",
					"couponAmount" : ConsumptionData.dataForPayment.couponAmount,
					"orderNo" : ConsumptionData.dataForPayment.orderNo,
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuth);
	Pay.checkTransReverse("msc/pay/prepaid",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuth);
		});
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthComplete);
	Pay.checkTransReverse("msc/pay/prepaid/over",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthComplete);
		});
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
		   		"oriTxnId": ConsumptionData.dataForPayment.txnId,
		   		"oriBatchNo": params.oriBatchNo,
		   		"oriTraceNo": params.oriTraceNo,
		   		"oriTransTime": params.oriTransTime,
		   	};
	if(params.cardNo != ConsumptionData.dataForPayment.F02){
		Scene.alert("刷卡错误，请刷原卡!",function(){
			Scene.goBack("OrderDetail");
		});
		return;
	}
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthSettlement);
	Pay.checkTransReverse("msc/pay/prepaid/over/offline",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthSettlement);
		});
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
		   		"oriTxnId": ConsumptionData.dataForPayment.txnId,
		   		"oriBatchNo": params.oriBatchNo,
		   		"oriTraceNo": params.oriTraceNo,
		   		"oriTransTime": params.oriTransTime,
		   	};
	if(params.cardNo != ConsumptionData.dataForPayment.F02){
		Scene.alert("刷卡错误，请刷原卡!",function(){
			Scene.goBack("OrderDetail");
		});
		return;
	}
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCancel);
	Pay.checkTransReverse("msc/pay/prepaid/cancel",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCancel);
		});
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
		   		"oriTxnId": ConsumptionData.dataForPayment.txnId,
		   		"oriBatchNo": params.oriBatchNo,
		   		"oriTraceNo": params.oriTraceNo,
		   		"oriTransTime": params.oriTransTime,
		   	};
	if(params.cardNo != ConsumptionData.dataForPayment.F02){
		Scene.alert("刷卡错误，请刷原卡!",function(){
			Scene.goBack("OrderDetail");
		});
		return;
	}
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
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCompleteCancel);
	Pay.checkTransReverse("msc/pay/prepaid/over/cancel",function(){
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCompleteCancel);
		});	
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
		   		"oriTxnId": ConsumptionData.dataForPayment.txnId,
		   		"oriBatchNo": params.oriBatchNo,
		   		"oriTraceNo": params.oriTraceNo,
		   		"oriTransTime": params.oriTransTime,
		   	};
	if(params.cardNo != ConsumptionData.dataForPayment.F02){
		Scene.alert("刷卡错误，请刷原卡!",function(){
			Scene.goBack("OrderDetail");
		});
		return;
	}
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

Pay.transferReq = function() {
	//window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePreAuthCompleteCancel);
	Pay.checkTransReverse("msc/pay/transfer", function() {
		window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exeSuperTransfer);
	});	
}

Pay.exeSuperTransfer = function(params) {
	var req = {
		   		"data": params.data8583,
		   		"paymentId": params.paymentId,
		   		"transType": params.transType,
		   		"batchNo": params.batchNo,
		   		"traceNo": params.traceNo,
		   		"transTime": params.transTime,
		   		"cardNo": params.cardNo,
		   		"transAmount": params.transAmount,
		   		"oriTxnId": ConsumptionData.dataForPayment.txnId,
		   		"oriBatchNo": params.oriBatchNo,
		   		"oriTraceNo": params.oriTraceNo,
		   		"oriTransTime": params.oriTransTime,
		   	};
/*	if(params.cardNo != ConsumptionData.dataForPayment.F02) {
		Scene.alert("刷卡错误，请刷原卡!", function(){
			Scene.goBack("Home");
		});
		return;
	}*/
	ConsumptionData.dataForPayment.req8583 = params.data8583;
	ConsumptionData.dataForPayment.transType = params.transType;
	
   	Net.connect("msc/pay/consume", req, actionAfterSuperTransfer, true);   	
   	
   	function actionAfterSuperTransfer(params) {
   		Scene.alert("JSLOG, actionAfterSuperTransfer response params:" + JSON.stringify(params));
   		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data
			};
			ConsumptionData.dataForPayment.res8583 = params.data;			
			ConsumptionData.dataForPayment.txnId = params.txnId;			
			window.data8583.convert8583(convertData, afterConfvertTransferData);
//			transferSuccess();
		} else {
			transferFailure();
		}
		
   	}
   	function afterConfvertTransferData(params) {
   		Scene.alert("JSLOG, afterConfvertTransferData params:" + JSON.stringify(params));
   		Scene.alert("JSLOG, afterConfvertTransferData dataForPayemnt:" + JSON.stringify(ConsumptionData.dataForPayment));
   		
   		if ("00" != params.resCode ) {
			if ("98" != params.resCode) {
				setTimeout(function() {
					Scene.alert(params.resMessage, goBackHome);
				}, 300);
			} else {
				Scene.alert(params.resMessage, goBackHome);
			}
		} else {
			
			window.Database.insertTransData8583(
				ConsumptionData.dataForPayment.txnId,
				ConsumptionData.dataForPayment.req8583,
				ConsumptionData.dataForPayment.res8583
			);
			
			setTimeout(function() {
				window.posPrint.printTrans(ConsumptionData.dataForPayment.txnId);
			}, 300);
			
			transferSuccess();
		}
   	}
   	
   	function transferSuccess() {
   		Scene.alert("转账成功", goBackHome);
   	}
   	
   	function transferFailure() {
   		Scene.alert("转账失败", goBackHome);
   	}
   	
   	function goBackHome() {
   		Scene.goBack("Home");
   	}
}


Pay.checkTransReverse =  function(action,callBack){

	if (!isActionEqual("base/version/verify", action)
    	&& !isActionEqual("base/echo-test", action)
        && !isActionEqual("msc/user/login", action)
        && !isActionEqual("msc/pay/reverse", action)
        && !isActionEqual("msc/pay/signin", action)
        && !isActionEqual("msc/pay/signout", action)
        && !isActionEqual("msc/pay/batch/settle", action)
        && !isActionEqual("msc/txn/update",action)
        && !isActionEqual("msc/payment/info/query", action)
        && !isActionEqual("msc/payment/template/query", action)
        && !isActionEqual("msc/pay/transfer", action)
        && !isActionEqual("msc/cust/info/query", action)) {
      Pay.reverseOrder(callBack);
    } else {
      if (callBack) {
        callBack();
      }
    }

	function isActionEqual(specAction, action) {
		if (null != action && -1 != action.search(specAction)) {
	      return true
	    } else {
	      return false
	    }
  	}
}



