;(function(){
 if (window.ConsumptionRecordSearch) { return }
  
  function onConfirm(data) {
	  	var msg = JSON.parse(data)
	  	var params = {
	  		startDate : msg.startDate + "000000",
	  		endDate : msg.endDate + "235959",
	  	}
  	    window.TransactionManageIndex.gotoConsumptionRecord(params)
  }
  
  function showConsumptionRecord(data) {
  	  window.TransactionManageIndex.showConsumptionRecord(data)
  }
  
 window.ConsumptionRecordSearch = {
 	onConfirm : onConfirm,
  }
  
})()