;(function(){
 if (window.SetTransId) { return; }
  
  function onConfirm(data) {
  	var msg = JSON.parse(data);
  	var transId = parseInt(msg.transId, 10);
  	var params = {
  		"transId" : transId
  	};
  	RMS.save("merchant",params);
  	Scene.goBack();
  }
  
  
 window.SetTransId = {
 	onConfirm : onConfirm,
 };
  
})();