;(function(){
 if (window.ModifyPwd) { return }
  
  function gotoModifyPwd(data) {
  	var msg = JSON.parse(data)
  	var origPwd = msg.origalPwd;
  	var newPwd = msg.newPwd;
  	
  	var req = {
  		pwd : newPwd,
  		oldPwd: origPwd,
  	}
		Net.connect("msc/user/pwd/change", req, actionAfterModify)
		/*var data = {
			responseCode:"0",
			errorMsg:"dkdkdk",
		}
		actionAfterModify(data);*/
  }
  
  function actionAfterModify(data) {
    if (data.responseCode != "0") {
	    Scene.alert(data.errorMsg)
	    return
  	} else {
	  	Scene.alert("143",resultAction);
  	}	
  }
  
  function resultAction(){
		var params = null
		params = {
			shouldRemoveCurCtrl: true
		}
		window.user.init({});
		window.user.setLoginResult(function(){
			Scene.goBack("Home");
		});
		
		RMS.read("merchant", getMerchInfo);		
		function getMerchInfo(data){

			var formData = {
				"merchId": data.merchId,
				"operator": data.operator,
			}
			Scene.showScene("Login","",formData);
		}
  }
  
 window.ModifyPwd = {
 	"gotoModifyPwd" : gotoModifyPwd,
  }
  
})()