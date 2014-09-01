//app/pay/InputAmount.js
define(['Moo'], function(Moo) {
	var currentStep;
	var currentTag;
	var InputAmount = new Class({

		initialize : function() {

		},

		onCompleteInput : function(data) {
			var params = JSON.parse(data);
			var transAmount = params.transAmount;

			currentStep = Pay.cacheData.step;
			if (currentStep >= Pay.cacheData.flowList.length) {
				Scene.alert("非正常操作，请重新操作！", function() {
					Scene.goBack("Home");
				});
				return;
			}
			if (Pay.cacheData.flowList[currentStep] == null) {
				Scene.alert("currentStep = " + currentStep);
				return;
			}
			currentTag = Pay.cacheData.flowList[currentStep].packTag;

			Pay.cacheData.transAmount = transAmount;
			Pay.cacheData[currentTag] = transAmount;
			Pay.cacheData.step = currentStep + 1;
			Pay.gotoFlow();
		},

		clear : function() {
			ConsumptionData.isMultiPay = false;
			if (currentStep == null) {
				return;
			}
			Pay.cacheData.transAmount = "";
			Pay.cacheData[currentTag] = null;
			Pay.cacheData.step = null;
		}
	});

	return InputAmount;
});
