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
	Net.connect("merchant/pay", req, actionAfterSend, true);

	function actionAfterSend(params) {
		window.RMS.clear("savedTransData");

		if (params.responseCode == "0") {
			ConsumptionData.dataForPayment.isSendCode = null;

			var convertData = {
				"data8583": params.data,
			};
			window.data8583.convert8583(convertData, actionAfterConvert);
		} else {
			Scene.alert(params.errorMsg);
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
			Scene.alert(params.resMessage);
		}
	};
};

Pay.convertPayReq = function() {
	window.data8583.get8583(ConsumptionData.dataForPayment, Pay.exePay);
};

Pay.exePay = function(params) {
	var req = {
		"data": params.data8583,
	};
	ConsumptionData.dataForPayment.req8583 = params.data8583;

	Net.connect("merchant/pay", req, actionAfterPay, true);

	function actionAfterPay(params) {
		if (params.responseCode == "0") {
			var convertData = {
				"data8583": params.data,
			};

			ConsumptionData.dataForPayment.res8583 = params.data;

			window.data8583.convert8583(convertData, Pay.payResult);
		} else {
			Pay.reverseOrder(Pay.restart);
		}
	}

};

Pay.payResult = function(params) {
	// params.resCode ＝ "98";
	ConsumptionData.dataForPayment.rrn = params.rrn;
	ConsumptionData.dataForPayment.transTime = params.transTime;

	window.Database.insertTransData8583(
		ConsumptionData.dataForPayment.rrn,
		ConsumptionData.dataForPayment.req8583,
		ConsumptionData.dataForPayment.res8583
	);

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
		setTimeout(function() {
			window.posPrint.printTrans(ConsumptionData.dataForPayment.rrn);
		}, 300);
		showDetail();
	}

	function showDetail() {
		var payTypeDesc = "" + ConsumptionData.dataForPayment.paymentName;
		ConsumptionData.dataForPayment.payTypeDesc = payTypeDesc;
		var transTime = util.formatDateTime(ConsumptionData.dataForPayment.transTime);
		var transAmount = util.formatAmountStr(ConsumptionData.dataForPayment.transAmount);

		var msg = {
			"ref": ConsumptionData.dataForPayment.rrn,
			"orderStateDesc": "完成",
			"payTypeDesc": payTypeDesc,
			"transAmount": transAmount,
			"transTime": transTime,
			"transTypeDesc": "消费",
			"openBrh": ConsumptionData.dataForPayment.openBrh,
			"paymentId": ConsumptionData.dataForPayment.paymentId,
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

Pay.restart = function(params) {

	var order = {
		"ref" : ConsumptionData.dataForPayment.rrn,
		"result" : ConsumptionData.dataForPayment.result,
		"orderStateDesc" : ConsumptionData.dataForPayment.result == "success" ? "完成" : "失败",
		"payTypeDesc" : "" + ConsumptionData.dataForPayment.paymentName,
		"transAmount" : ConsumptionData.dataForPayment.transAmount,
		"showAmount" : util.formatAmountStr(ConsumptionData.dataForPayment.transAmount),
	};

	if (ConsumptionData.isMultiPay == true) {
		var orderStateDesc = "完成";
		if (ConsumptionData.dataForPayment.result == "success") {
			var paidAmount = 0 + ConsumptionData.dataForMultiPay.paidAmount;
			paidAmount += ConsumptionData.dataForPayment.transAmount;
			ConsumptionData.dataForMultiPay.paidAmount = "" + paidAmount;
		} else {
			orderStateDesc = "失败";
		}
		ConsumptionData.dataForMultiPay.orderList.push(order);
		var formData = {
			"totalAmount" : util.formatAmountStr(ConsumptionData.dataForMultiPay.totalAmount),
			"paidAmount" : util.formatAmountStr(ConsumptionData.dataForMultiPay.paidAmount),
			"orderList" : ConsumptionData.dataForMultiPay.orderList,
		};
		Scene.goBack("MultiPayRecord", formData);
		if (ConsumptionData.dataForPayment.isExternalOrder == true) {
			return;
		}
	} else {
		if (ConsumptionData.dataForPayment.isExternalOrder) {
			if (ConsumptionData.dataForMultiPay.completed == true) {
				Scene.goBack("first", ConsumptionData.dataForMultiPay);
				ConsumptionData.resetMultiData();
			} else {
				Scene.goBack("first", {
					"totalAmount" : ConsumptionData.dataForPayment.transAmount,
					"paidAmount" : ConsumptionData.dataForPayment.transAmount,
					"orderList" : [order],
				});
			}
		} else {
			Scene.goBack("Home");
		};
	}
	ConsumptionData.resetConsumptionData();
}; 
