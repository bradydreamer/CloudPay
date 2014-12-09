;
(function() {
	if (window.PinPad) {
		return;
	}

	function CompleteInput(data) {
		var params = JSON.parse(data)
		var actionPurpose = params.actionPurpose
		var pwd = params.pwd
		if ("Balance" == actionPurpose) {
			if (params.isCancelled) {
				Scene.goBack("Home")
			} else {
				ConsumptionData.dataForBalance.balancePwd = params.pwd;				
				//window.data8583.get8583(ConsumptionData.dataForBalance, afterGetBalance8583);
				Pay.checkTransReverse("msc/balance",function(){
					window.data8583.get8583(ConsumptionData.dataForBalance, afterGetBalance8583);
				});
			}
		} else {
			if (params.isCancelled) {
				Pay.flowRestartFunction();
			} else {
				var currentStep = Pay.cacheData.step;
				if(currentStep >= Pay.cacheData.flowList.length){
					Scene.alert("120",function(){
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
	}

	function afterGetBalance8583(data) {
		var params = data
		params.shouldRemoveCurCtrl = true;
		Scene.showScene("BalanceResult", "", params)
	}

	window.PinPad = {
		"CompleteInput": CompleteInput,
	};
})();