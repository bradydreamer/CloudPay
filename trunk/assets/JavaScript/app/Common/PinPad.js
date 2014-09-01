//app/Common/PinPad.js
define(['Moo'], function(Moo) {

	var PinPad = new Class({

		initialize : function() {

		},

		completeInput : function(data) {
			var params = JSON.parse(data);
			var actionPurpose = params.actionPurpose;
			var pwd = params.pwd;
			if ("Balance" == actionPurpose) {
				if (params.isCancelled) {
					Scene.goBack("Home");
				} else {
					ConsumptionData.dataForBalance.balancePwd = params.pwd;
					window.data8583.get8583(ConsumptionData.dataForBalance, afterGetBalance8583);
				}
			} else {
				if (params.isCancelled) {
					Pay.flowRestartFunction();
				} else {
					var currentStep = Pay.cacheData.step;
					if (currentStep >= Pay.cacheData.flowList.length) {
						Scene.alert("非正常操作，请重新操作！", function() {
							Scene.goBack("Home");
						});
						return;
					}
					var currentTag = Pay.cacheData.flowList[currentStep].packTag;

					Pay.cacheData[currentTag] = pwd;
					Pay.cacheData.authCode = params.authCode;

					Pay.cacheData.step = currentStep + 1;
					Pay.gotoFlow();
				}
			}
		},

		afterGetBalance8583 : function(data) {
			var params = data;
			params.shouldRemoveCurCtrl = true;
			Scene.showScene("BalanceResult", "", params);
		}
	});

	return PinPad;
});
