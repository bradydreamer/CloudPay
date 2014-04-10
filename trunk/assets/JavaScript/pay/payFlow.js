Pay.gotoPayFlow = function() {
	Pay.cacheData = ConsumptionData.dataForPayment;
	Pay.flowEndFunction = Pay.convertPayReq;
	Pay.flowRestartFunction = Pay.restart;

	Pay.gotoFlow();
};

Pay.gotoCancelFlow = function() {
	Pay.cacheData = ConsumptionData.dataForCancellingOrder;
	Pay.flowEndFunction = Pay.cancelOrderConvertReq;
	Pay.flowRestartFunction = function () {
		Scene.goBack("OrderDetail");
	};
	
	Pay.gotoFlow();
};


Pay.gotoMultiPayFlow = function() {
	Pay.cacheData = ConsumptionData.dataForMultiPay;
	Pay.flowEndFunction = function() {
		if(ConsumptionData.dataForMultiPay.totalAmount == null){
			ConsumptionData.dataForMultiPay.totalAmount = "0";
		}
		if(ConsumptionData.dataForMultiPay.paidAmount == null){
			ConsumptionData.dataForMultiPay.paidAmount = "0";
		}
		var formData = {
			"totalAmount": util.formatAmountStr(ConsumptionData.dataForMultiPay.totalAmount),
			"paidAmount": util.formatAmountStr(ConsumptionData.dataForMultiPay.paidAmount),
		};
		if (Pay.cacheData.preScene == "InputAmount") {
			formData.shouldRemoveCurCtrl = true;
			Pay.cacheData.preScene = null;
		}
		Scene.showScene("MultiPayRecord", "", formData);
	};

	Pay.gotoFlow();
}; 


Pay.cacheData = {};
Pay.flowEndFunction = null;
Pay.flowRestartFunction = null;

Pay.gotoFlow = function() {
	var cacheData = Pay.cacheData;
	var endFunction = Pay.flowEndFunction;
	var step = cacheData.step;
	var flowList = cacheData.flowList;
	
	if (flowList == null || step > flowList.length) {
		return;
	} else if (step == flowList.length) {
		if (endFunction) {
			endFunction();
		};
		return;
	};
	
	var sceneName = "";
	var formData = null;

	var flow = flowList[step];

	if (flow.packTag == null) {
		Scene.alert("未指定填充域，请更新配置文件。");
		return;
	};

	if (checkExistField(flow.packTag)) {
		if (flow.methods == null || flow.methods[0] != "99") {
			cacheData.step = step + 1;
			Pay.gotoFlow();
			return;
		}
	};

	if (flow.methods == null || flow.methods.length == 0) {
		Scene.alert("采集方式有误，请更新配置文件。");
		return;
	}

	var method = flow.methods[0];
	if (method == null || method == "99") {
		if (flow.packTag != null) {
			cacheData[flow.packTag] = flow.packValue;
		};
		cacheData.step = step + 1;
		Pay.gotoFlow();
		return;
	} else if (method == "00" || method == "01" || method == "02" || method == "03") {
		sceneName = "PayAccount";
		initPayAccountData();
	} else if(method == "04"){
		sceneName = "Login"	
		formData = {"Login":"LoginIndex.refundConfirmLogin"};
	} else if(method == "05"){
		sceneName = "Login"		
		formData = {"Login":"LoginIndex.voidConfirmLogin"};
	} else if (method == "10") {
		sceneName = "InputAmount";
		initAmountData();
	} else if (method == "30") {
		sceneName = "PinPad";
		initPinData();
	} else if (method == "40") {
		sceneName = "PinPad";
	} else if (method == "90") {
		Pay.convertSendCodeReq();
		return;
	};
	
	if(cacheData.preScene == "PayAccount" || cacheData.preScene == "PinPad"){
		formData.shouldRemoveCurCtrl = true;
		cacheData.preScene = null;
	}
	
	window.util.showSceneWithLoginChecked(sceneName, formData, flow.desc);
	cacheData.preScene = sceneName;

	function checkExistField(packTag) {
		if (cacheData[packTag] != null) {
			return true;
		} else if (compare("F35", "track2") || compare("F36", "track3") || compare("F40_6F12", "field4") || compare("F40_6F08", "field1") || compare("F40_6F11", "authCode") || compare("F40_6F20", "openBrh")
		 || compare("F60.6", "paymentId") || compare("F04", "transAmount")) {
			return true;
		}
		return false;

		function compare(tag, fieldName) {
			if (packTag == tag && cacheData[fieldName] != null) {
				cacheData[packTag] = cacheData[fieldName];
				return true;
			}
			return false;
		}
	}

	function initAmountData() {
		formData = {};
		
		if(ConsumptionData.isMultiPay == true && ConsumptionData.dataForMultiPay.totalAmount != null){		
			var totalAmount = 0 + ConsumptionData.dataForMultiPay.totalAmount;
			var paidAmount = 0 + ConsumptionData.dataForMultiPay.paidAmount;
			var balance = totalAmount - paidAmount;
			formData.maxAmount = "" + balance;
		}
	}
	
	function initPinData() {
		formData = {};

		if (cacheData["F02"] != null) {
			formData.cardID = cacheData["F02"];
		};
		if (cacheData.needAuthCode) {
			formData["needAuthCode"] = true;
		};
		var transAmount = cacheData.transAmount;
		if (transAmount != null && transAmount.length != 0) {
			formData["transAmount"] = util.formatAmountStr(transAmount);
		};
	}

	function initPayAccountData() {
		formData = {
			"swipeCard": "PayAccount.exeSwipeResponse",
			"nearfieldAccount": "PayAccount.exeRecvData",
			"inputAccount": "PayAccount.exeCardIdResponse",
		};

		var transAmount = cacheData.transAmount;
		if (transAmount != null && transAmount.length != 0) {
			formData.transAmount = util.formatAmountStr(transAmount);
		};

		//-1：不可用 0：可用 1：可用，高亮
		formData.btn_swipe = -1;
		formData.btn_input = -1;
		formData.btn_sound = -1;
		formData.btn_qrcode = -1;

		for (var i = 0; i < flow.methods.length; i++) {
			method = flow.methods[i];
			var btn_name;
			if (method == "00") {
				btn_name = "btn_swipe";
			} else if (method == "01") {
				btn_name = "btn_input";
			} else if (method == "02") {
				btn_name = "btn_sound";
			} else if (method == "03") {
				btn_name = "btn_qrcode";
			}
			if (btn_name != null) {
				formData[btn_name] = i == 0 ? 1 : 0;
			};
		};

		formData.btn_sound = -1;
	}
};