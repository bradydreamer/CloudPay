;(function(){
 if (window.SetMerchId) { return }
  
  function onConfirm(data) {
  	var msg = JSON.parse(data)
  	var merchId = msg.merchId
  	var params = {
  		merchId : merchId
  	}
  	RMS.save("merchant",params);
  	setTimeout(function(){actionAfterSet(merchId)},300)
  }
  
  function actionAfterSet(merchId) {
  	  window.user.init({})
      window.user.merchId = merchId
      window.user.merchIdSetResultAction()
  }
  
 window.SetMerchId = {
 	onConfirm : onConfirm,
  }
  
})()