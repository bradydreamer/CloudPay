//app/Others/Settings/PaymentMechanismInfo.js
define(['Moo'], function(Moo) {
	var transInfoList = {};

	var PaymentMechanismInfo = new Class({
		initialize : function() {

		},

		updateTransInfo : function() {
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

			initPaymentMechanismInfoUI(merchSettings);
		},

		initPaymentMechanismInfoUI : function(merchSettings) {
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
			var propertyList = [{
				"name" : "viewPager",
				"key" : "data",
				"value" : transInfoList[typeId],
			}];
			Scene.setProperty("", propertyList);
		},

		researchMethod : function(data) {
			var params = JSON.parse(data);
			var product = JSON.parse(params.tag);
			ConsumptionData.dataForPayment.paymentId = product.paymentId;
			ConsumptionData.dataForPayment.brhKeyIndex = product.brhKeyIndex;
			var formData = {
				"paymentId" : product.paymentId,
			};
			window.util.showSceneWithLoginChecked("SingleRecordSearch", formData, null);
		}
	});

	return PaymentMechanismInfo;
});
