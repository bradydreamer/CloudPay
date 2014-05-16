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
				};
				window.data8583.get8583(reverseData, afterGetReverse8583);
			}
		} else if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		}
		;

		function afterGetReverse8583(params) {
			var req = {
				"data" : params.data8583,
			};
			Net.connect("merchant/reverse", req, Pay.reverseResult, true);
		}

	}

};

Pay.reverseResult = function(params) {
	if (params.responseCode == "0") {
		var params = {
			data8583 : params.data,
		};
		window.data8583.convert8583(params, afterConvertReverseRes);
	} else {
		Scene.alert("冲正失败，请联系客服");
		if (Pay.reverseCallBack) {
			Pay.reverseCallBack();
			Pay.reverseCallBack = null;
		};
	}

	function afterConvertReverseRes(params) {
		if ("00" == params.resCode || "25" == params.resCode) {
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

Pay.cancelCallBack = function() {
};

Pay.cancelOrder = function(params, callBack) {
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
		if (product == null || product.voidTemplate == null || product.voidTemplate.length == 0) {
			Scene.alert("该交易不支持撤销" + JSON.stringify(product));
			return;
		}
		if (window.voidTemplates == null) {
			window.RMS.read("voidTemplates", function(data) {
				window.voidTemplates = data;
				confirmMethod(product, window.voidTemplates[product.voidTemplate]);
			});
		} else {
			confirmMethod(product, window.voidTemplates[product.voidTemplate]);
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

		ConsumptionData.dataForCancellingOrder.typeOf8583 = "cheXiao";
		ConsumptionData.dataForCancellingOrder.flowList = flowList;

		Pay.gotoCancelFlow();
	}

};

Pay.cancelOrderConvertReq = function(params) {
	window.data8583.get8583(ConsumptionData.dataForCancellingOrder, Pay.cancelOrderExe);
};

Pay.cancelOrderExe = function(params) {
	var req = {
		"data" : params.data8583,
	};
	ConsumptionData.dataForCancellingOrder.req8583 = req.data;

	var action = "merchant/cancel";
	if (ConsumptionData.dataForCancellingOrder.typeOf8583 == "refund") {
		action = "merchant/refund";
	}
	Net.connect(action, req, afterCancelPayOrder, true);

	function afterCancelPayOrder(data) {
		if (data.responseCode == "0") {
			var params = {
				"data8583" : data.data,
			};
			ConsumptionData.dataForCancellingOrder.res8583 = data.data;
			window.data8583.convert8583(params, Pay.cancelOrderResult);
		} else {
			Pay.reverseOrder(Pay.cancelEnd);
		}
	}

};

Pay.cancelOrderResult = function(params) {
	window.Database.updateTransData8583(ConsumptionData.dataForCancellingOrder.rrn, ConsumptionData.dataForCancellingOrder.req8583, ConsumptionData.dataForCancellingOrder.res8583);
	if ("00" == params.resCode) {
		window.RMS.clear("savedTransData");
		setTimeout(function() {
			window.posPrint.printTrans(ConsumptionData.dataForCancellingOrder.rrn);
			ConsumptionData.resetDataForCancellingOrder();
		}, 300);
		if (Pay.cancelCallBack) {
			Pay.cancelCallBack();
			Pay.cancelCallBack = null;
		};
	} else if ("98" == params.resCode) {
		Pay.reverseOrder(Pay.cancelEnd);
	} else {
		Pay.cancelEnd();
		setTimeout(function() {
			Scene.alert(params.resMessage);
		}, 300);
	}
	;

};

Pay.cancelEnd = function() {
	window.RMS.clear("savedTransData");
	Scene.goBack("OrderDetail");
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

