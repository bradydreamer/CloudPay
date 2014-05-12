;
(function() {
  if (window.MultiPay) {
    return;
  };

  function onClickRecord(data) {
   Scene.goBack("MultiPayRecord");
  };

  function onClickClose() {
  	Scene.goBack("MultiPayRecord");
  }
  
  function onClickPay () {
  	
	if (ConsumptionData.dataForPayment.isExternalOrder == true){
    	ConsumptionData.resetConsumptionData();
		ConsumptionData.dataForPayment.isExternalOrder = true
	}
    Scene.showScene("MultiPayIndex");
  }
  
  function onClickComplete () {
    ConsumptionData.isMultiPay = false;
    ConsumptionData.dataForMultiPay.completed = true;
    Pay.restart();
  }

  window.MultiPay = {
    "onClickClose": onClickClose,
    "onClickRecord": onClickRecord,
    "onClickPay": onClickPay,
    "onClickComplete": onClickComplete,
  };
})();