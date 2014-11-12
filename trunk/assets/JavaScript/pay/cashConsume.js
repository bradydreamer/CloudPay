;
(function() {
  if (window.CashConsume) {
    return;
  };
  var currentStep;
  var currentTag;


  function clear () {
		if(Pay.cacheData.step > 0){
			Pay.cacheData.step--;
		}
  }
  
  function cashExePay(data){
	  var params = JSON.parse(data);
	  currentStep = Pay.cacheData.step;
	  if(currentStep >= Pay.cacheData.flowList.length){
		Scene.alert("非正常操作，请重新操作！",function(){
			Scene.goBack("Home");
		});
		return;
	  }
//	  Pay.cacheData.cashPay = true;
//	  Pay.cacheData.result = "success";
//	  Pay.cacheData.paidAmount = params.cashPaidAmount;
//	  Pay.cacheData.changeAmount = params.changeAmount;
//	  Pay.cacheData.transTime = params.transTime;
//	  Pay.cacheData.step = currentStep + 1;
//	  Pay.gotoFlow();
	  Pay.cashSuccRestart(data);
  }  

  window.CashConsume = {
    "clear": clear,
    "cashExePay": cashExePay
  };

})();