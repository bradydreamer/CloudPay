;
(function() {
  if (window.SuperTransfer) {
    return;
  };
  var currentStep;
  var currentTag;

  function onCompleteInput(data) {
    var params = JSON.parse(data);
    var transAmount = params.transAmount;
    var fromAccount = params.fromAccount;
    var toAccount = params.toAccount;
    var idCard = params.idCard;
    var transType = "1721";

    currentStep = Pay.cacheData.step;
	if(currentStep >= Pay.cacheData.flowList.length){
		Scene.alert("120",function(){
			Scene.goBack("Home");
		});
		return;
	}
    if (Pay.cacheData.flowList[currentStep] == null) {
      Scene.alert("currentStep = " + currentStep);
      return;
    }
    currentTag = Pay.cacheData.flowList[currentStep].packTag;

    //add additional 10.00 RMB fax
    Pay.cacheData.transAmount = parseInt(transAmount) + 1000;
//    Pay.cacheData.transAmount = parseInt(transAmount);
    Pay.cacheData.fromAccount = fromAccount;
    Pay.cacheData.toAccount = toAccount;
    Pay.cacheData.idCard = idCard;
    Pay.cacheData.track2 = params.track2;
    Pay.cacheData.track3 = params.track3;
    Pay.cacheData[currentTag] = fromAccount;
    Pay.cacheData.transType = transType;
    Pay.cacheData.step = currentStep + 1;
    Pay.gotoTransferFlow();
  };
  
  function clear() {
	ConsumptionData.isMultiPay = false;
    if (currentStep == null) {
      return;
    }
    Pay.cacheData.transAmount = "";
    Pay.cacheData[currentTag] = null;
    Pay.cacheData.step = null;
  }


  window.SuperTransfer = {
    "onCompleteInput": onCompleteInput,
    "clear" : clear
  };
})();