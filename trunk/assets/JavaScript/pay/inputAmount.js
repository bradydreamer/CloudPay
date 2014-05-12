;
(function() {
  if (window.InputAmount) {
    return;
  };
  var currentStep;
  var currentTag;

  function onCompleteInput(data) {
    var params = JSON.parse(data);
    var transAmount = params.transAmount;

    currentStep = Pay.cacheData.step;
    if (Pay.cacheData.flowList[currentStep] == null) {
      Scene.alert("currentStep = " + currentStep);
      return;
    }
    currentTag = Pay.cacheData.flowList[currentStep].packTag;

    Pay.cacheData.transAmount = transAmount;
    Pay.cacheData[currentTag] = transAmount;
    Pay.cacheData.step = currentStep + 1;
    Pay.gotoFlow();
  };

  function clear() {
	ConsumptionData.isMultiPay = false;
    if (currentStep == null) {
      return;
    }
    Pay.cacheData.transAmount = "";
    Pay.cacheData[currentTag] = null;
    Pay.cacheData.step = currentStep;
  }

  window.InputAmount = {
    "onCompleteInput": onCompleteInput,
    "clear": clear,
  };
})();