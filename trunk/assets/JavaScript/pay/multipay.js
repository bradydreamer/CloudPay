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
    ConsumptionData.resetConsumptionData();
    Scene.showScene("MultiPayIndex");
  }
  
  function onClickComplete () {
    ConsumptionData.isMultiPay = null;
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