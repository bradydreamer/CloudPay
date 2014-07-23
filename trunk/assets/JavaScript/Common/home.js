;
(function() {
	if (window.Home) {
		return;
	};
	var systemInfo;

	var transInfoList = {};

	function updateTransInfo() {
		Home.needUpdateUI = false;
		if (window.merchSettings == null) {
			window.RMS.read("merchSettings", initTransInfo);
		} else {
			initTransInfo(window.merchSettings);
		}
	}

	function initTransInfo(data) {
		window.merchSettings = data;
		var settingString = data.settingString;
		if (settingString == null || settingString.length == 0) {
			window.util.showSceneWithLoginChecked("SettingsDownload");
			return;
		};
		var merchSettings = JSON.parse(settingString);
		if (merchSettings == null || merchSettings.length == 0) {
			return;
		};
		
		initHomeUI(merchSettings);
	}

	function initHomeUI(merchSettings) {
		transInfoList = {};
		var funcModuleName = [];
		for (var i = 0; i < merchSettings.length; i++) {
			var typeName = merchSettings[i].typeName;
			var typeId = merchSettings[i].typeId;
			if (transInfoList[typeId] == null) {
				transInfoList[typeId] = [];
				funcModuleName.push({
					"typeName": typeName,
					"typeId": typeId,
				});
			}
			delete merchSettings.typeName;
			transInfoList[typeId].push(merchSettings[i]);
		};
		var propertyList = [{
			"name": "layout_funcModule",
			"key": "data",
			"value": funcModuleName,
		}, {
			"name": "viewPager",
			"key": "data",
			"value": transInfoList[funcModuleName[0].typeId],
		}];
		Scene.setProperty("", propertyList);
	}
	
	function onSwitchFuncModule(data) {
		var params = JSON.parse(data);
		var typeId = params.typeId;
		var transAmount = null;
		if(typeId == "cash"){
			if(ConsumptionData.isMultiPay == true && ConsumptionData.dataForMultiPay.totalAmount != null){		
				var totalAmount = parseInt(ConsumptionData.dataForMultiPay.totalAmount);
				var paidAmount = parseInt(ConsumptionData.dataForMultiPay.paidAmount);
				var balance = totalAmount - paidAmount;
				if(balance < 0){
					balance = 0.00;
				}
				transAmount = "" + balance;
			}else{
				transAmount = ConsumptionData.dataForPayment.transAmount;
			}
		}
		var propertyList = [{
			"name": "viewPager",
			"key": "data",
			"value": transInfoList[typeId],
			"transAmount": transAmount,
		}];
		Scene.setProperty("", propertyList);
	}

	function setSystemInfo(info) {
		systemInfo = info;
		if (systemInfo != null) {
			reqVerifyVer();
		}
	}

	function reqVerifyVer() {
		if (systemInfo == null) {
			Global.getSystemInfo(setSystemInfo);
			return;
		}
		var req = {
			"version": systemInfo.version,
			"platform": systemInfo.platform,
			"fullmobiletype": systemInfo.model,
			"os": systemInfo.os,
			"resolution": systemInfo.resolution,
		};
		Net.asynConnect("base/verifyVersion", req, callback);

		function callback(params) {
			Home.needVerifyVersion = false;
			var newVersionUrl = params.url;
			var forceUpdate = params.update;
			if (forceUpdate != null) {
				if (forceUpdate == "true") {
					Scene.alert("发现新版本：v" + params.version + "。\n请更新后使用。", _update);
				} else {
					Scene.alert("发现新版本：v" + params.version + "。\n是否马上更新？", _update, "", "");
				}
			};

			function _update(params) {
				if (params.isPositiveClicked == 1) {
					if (forceUpdate == "true") {
						Global.openUrl(newVersionUrl, Global.exit);
					} else {
						Global.openUrl(newVersionUrl);
					};
				}
			}
		}
	}

	function onShow() {
		Global.clearGlobal();
		window.user.token = "";
		if (Home.needVerifyVersion == true) {
			//setTimeout(reqVerifyVer, 300);
		}
		if (Home.needUpdateUI == true) {
			setTimeout(updateTransInfo, 50);
		}
	}

	function onClickBtnTransManage() {
		window.util.showSceneWithLoginChecked("TransactionManageIndex");
	}
	

	function onClickMultiPay() {
		ConsumptionData.isMultiPay = true;
		window.util.exeActionWithLoginChecked(function() {
			ConsumptionData.resetMultiData();

			if (ConsumptionData.dataForPayment.isExternalOrder == true) {
				if(ConsumptionData.dataForPayment.transAmount != null){
					ConsumptionData.dataForMultiPay.totalAmount = ConsumptionData.dataForPayment.transAmount;
				}
				ConsumptionData.resetConsumptionData();
				ConsumptionData.dataForPayment.isExternalOrder = true;
			}else{
				ConsumptionData.resetConsumptionData();
			}
			
			Pay.gotoMultiPayFlow();
		});
	}


	window.Home = {
		"onShow": onShow,
		"updateTransInfo": updateTransInfo,
		"onSwitchFuncModule": onSwitchFuncModule,
		"onClickBtnTransManage": onClickBtnTransManage,
		"onClickMultiPay": onClickMultiPay,
	};
	Home.needVerifyVersion = true;
	Home.needUpdateUI = true;
})();