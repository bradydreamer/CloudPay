//app/Common/Home.js
define(['Moo', 'RMS'], function(Moo, RMS) {
	var systemInfo;
	var transInfoList = {};
	var needVerifyVersion = true;
	var needUpdateUI = true;

	var Home = new Class({

		initialize : function() {

		},
		updateTransInfo : function() {
			needUpdateUI = false;
			if (window.merchSettings == null) {
				window.RMS.read("merchSettings", initTransInfo);
			} else {
				initTransInfo(window.merchSettings);
			}
		},

		initTransInfo : function(data) {
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
		},

		initHomeUI : function(merchSettings) {
			transInfoList = {};
			var funcModuleName = [];
			for (var i = 0; i < merchSettings.length; i++) {
				var typeName = merchSettings[i].typeName;
				var typeId = merchSettings[i].typeId;
				if (transInfoList[typeId] == null) {
					transInfoList[typeId] = [];
					funcModuleName.push({
						"typeName" : typeName,
						"typeId" : typeId,
					});
				}
				delete merchSettings.typeName;
				transInfoList[typeId].push(merchSettings[i]);
			};
			var propertyList = [{
				"name" : "layout_funcModule",
				"key" : "data",
				"value" : funcModuleName,
			}, {
				"name" : "viewPager",
				"key" : "data",
				"value" : transInfoList[funcModuleName[0].typeId],
			}];
			Scene.setProperty("", propertyList);
		},

		onSwitchFuncModule : function(data) {
			var params = JSON.parse(data);
			var typeId = params.typeId;
			var transAmount = null;
			if (typeId == "cash") {
				if (ConsumptionData.isMultiPay == true && ConsumptionData.dataForMultiPay.totalAmount != null) {
					var totalAmount = parseInt(ConsumptionData.dataForMultiPay.totalAmount);
					var paidAmount = parseInt(ConsumptionData.dataForMultiPay.paidAmount);
					var balance = totalAmount - paidAmount;
					if (balance < 0) {
						balance = 0.00;
					}
					transAmount = "" + balance;
				} else {
					transAmount = ConsumptionData.dataForPayment.transAmount;
				}
			}
			var propertyList = [{
				"name" : "viewPager",
				"key" : "data",
				"value" : transInfoList[typeId],
				"transAmount" : transAmount,
			}];
			Scene.setProperty("", propertyList);
		},

		setSystemInfo : function(info) {
			systemInfo = info;
			if (systemInfo != null) {
				reqVerifyVer();
			}
		},

		reqVerifyVer : function() {
			if (systemInfo == null) {
				Global.getSystemInfo(setSystemInfo);
				return;
			}
			var req = {
				"version" : systemInfo.version,
				"platform" : systemInfo.platform,
				"fullmobiletype" : systemInfo.model,
				"os" : systemInfo.os,
				"resolution" : systemInfo.resolution,
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

		},

		onShow : function() {
			Global.clearGlobal();
			window.user.token = "";
			if (Home.needVerifyVersion == true) {
				//setTimeout(reqVerifyVer, 300);
			}
			if (Home.needUpdateUI == true) {
				setTimeout(updateTransInfo, 50);
			}
		},

		onClickBtnTransManage : function() {
			Util.showSceneWithLoginChecked("TransactionManageIndex");
		},
		onClickMultiPay : function() {
			ConsumptionData.isMultiPay = true;
			Util.exeActionWithLoginChecked(function() {
				ConsumptionData.resetMultiData();

				if (ConsumptionData.dataForPayment.isExternalOrder == true) {
					if (ConsumptionData.dataForPayment.transAmount != null) {
						ConsumptionData.dataForMultiPay.totalAmount = ConsumptionData.dataForPayment.transAmount;
					}
					ConsumptionData.resetConsumptionData();
					ConsumptionData.dataForPayment.isExternalOrder = true;
				} else {
					ConsumptionData.resetConsumptionData();
				}

				Pay.gotoMultiPayFlow();
			});
		}
	});
});
