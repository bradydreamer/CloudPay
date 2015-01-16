;(function(){
 if (window.ConsumptionRecordSearch) { return }
  
  function onConfirm(data) {
	  	var msg = JSON.parse(data)
	  	var params = {
	  	    fromTodayTag : false,
	  		startDate : msg.startDate,
	  		endDate : msg.endDate
	  	}
	  	//save startDate and endDate to global variable
	  	window.TransactionManageIndex.refresh = undefined;
	  	window.TransactionManageIndex.params = params;
	  	//save end
  	    window.TransactionManageIndex.gotoConsumptionRecord(params)
  }
  
  function showConsumptionRecord(data) {
  	  window.TransactionManageIndex.showConsumptionRecord(data)
  }
  
 window.ConsumptionRecordSearch = {
 	onConfirm : onConfirm,
  }
  
})()