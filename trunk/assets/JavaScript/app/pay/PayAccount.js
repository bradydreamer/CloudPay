//app/pay/PayAccount.js
define(['Moo'], function(Moo) {
	var currentStep;
	var currentTag;
	var PayAccount = new Class({
		
		initialize : function() {
			
		},
		
		getInputRegexResult: function(field, regex) {
			if (regex == "fill_0") {
				var defaultCardId = "0000000000000000000";
				var cardPrefix = defaultCardId.substring(0, defaultCardId.length - field.length);
				field = cardPrefix + field;
			} else if (regex == "fill_ec") {
				field = "88888666" + field;
			}
			return field;
		},
	
		exePurchase : function(params) {
			Pay.cacheData.needAuthCode = null;
	
			currentStep = Pay.cacheData.step;
			if (currentStep >= Pay.cacheData.flowList.length) {
				Scene.alert("非正常操作，请重新操作！", function() {
					Scene.goBack("Home");
				});
				return;
			}
			currentTag = Pay.cacheData.flowList[currentStep].packTag;
	
			var inputRegex = Pay.cacheData.flowList[currentStep].inputRegex;
			var cardID = getInputRegexResult(params.field0, inputRegex);
			Pay.cacheData[currentTag] = cardID;
			Pay.cacheData.step = currentStep + 1;
			Pay.gotoFlow();
		},
	
		exeCardIdResponse : function(data) {
			var params = JSON.parse(data);
			Pay.cacheData.track2 = "";
			Pay.cacheData.track3 = "";
			exePurchase(params);
		},
	
		exeRecvData : function(data) {
			var params = JSON.parse(data);
			exePurchase(params);
		},
	
		exeSwipeResponse : function(data) {
			var params = JSON.parse(data);
			params.field0 = params.cardID;
			Pay.cacheData.track2 = params.track2;
			Pay.cacheData.track3 = params.track3;
			Pay.cacheData.validTime = params.validTime;
	
			exePurchase(params);
		},
	
		clear : function() {
			if (currentStep == null) {
				if (Pay.cacheData.step > 0) {
					Pay.cacheData.step--;
				}
				return;
			}
			Pay.cacheData[currentTag] = null;
			Pay.cacheData.step = currentStep;
			if (Pay.cacheData.step > 0) {
				Pay.cacheData.step--;
			}
	
			Pay.cacheData.track2 = null;
			Pay.cacheData.track3 = null;
		}
	});
});
