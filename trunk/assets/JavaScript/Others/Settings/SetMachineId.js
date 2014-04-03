;(function(){
 if (window.SetMachineId) { return }
  
  function onConfirm(data) {
  	var msg = JSON.parse(data)
  	var machineId = msg.machineId
  	var params = {
  		machineId : machineId
  	}
  	RMS.save("merchant",params);
  	setTimeout(function(){actionAfterSet(machineId)},300)
  }
  
  function actionAfterSet(machineId) {
  	  window.user.init({})
  	  window.user.machineId = machineId
      window.user.machineIdSetResultAction()
  }
  
 window.SetMachineId = {
 	onConfirm : onConfirm,
  }
  
})()