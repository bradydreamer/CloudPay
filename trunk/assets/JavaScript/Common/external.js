function External() {};

External.onGetMerchId = function() {
	// window.util.exeActionWithMerchIdChecked(function() {
	window.util.exeActionWithLoginChecked(function() {
		var formData = {
			"merchId": window.user.merchId,
			"machineId": window.user.machineId,
		};
		Scene.goBack("first", formData);
	});
};

External.onLogin = function() {
	window.util.exeActionWithLoginChecked(function() {
		var formData = {
			"userStatus": window.user.userStatus,
		};
		Scene.goBack("first", formData);
	}, true);
};

External.onLogout = function(data) {
	window.user.init({});
	var formData = {
		"userStatus": window.user.userStatus,
	};
	return formData;
};

External.onPay = function(data) {
	window.ConsumptionData.resetConsumptionData();

	var params = {};
	var transAmount, openBrh, paymentId, packageName, orderNo, orderDesc;
	if (data != null) {
		params = JSON.parse(data);
		transAmount = params.transAmount;
		openBrh = params.openBrh;
		paymentId = params.paymentId;
		packageName = params.packageName;
		orderNo = params.orderNo;
		orderDesc = params.orderDesc;
	};
	ConsumptionData.dataForPayment.isExternalOrder = true;
	ConsumptionData.dataForPayment.transAmount = transAmount;
	//save more info to consumption data
	ConsumptionData.dataForPayment.packageName = packageName;
	ConsumptionData.dataForPayment.orderNo = orderNo;
	ConsumptionData.dataForPayment.orderDesc = orderDesc;

	if (params.openBrh == null || params.openBrh == "") {
		Scene.showScene("Home", "");
	} else {
		updateTransInfo();
	};

	function updateTransInfo() {
		window.RMS.read("merchSettings", initTransInfo);
	}

	function initTransInfo(data) {
		var settingString = data.settingString;
		if (settingString == null || settingString.length == 0) {
			window.util.showSceneWithLoginChecked("SettingsIndex");
			return;
		};
		var merchSettings = JSON.parse(settingString);
		if (merchSettings == null || merchSettings.length == 0) {
			return;
		};

		transInfo = null;
		for (var i = 0; i < merchSettings.length; i++) {
			var merchSetting = merchSettings[i];
			if (merchSetting.openBrh == openBrh && merchSetting.paymentId == paymentId) {
				transInfo = merchSetting;
				break;
			};
		};
		if (transInfo != null) {
			window.PayMethod.confirmMethod(transInfo);
		} else {
			Scene.showScene("Home", "");
		};
	}

};