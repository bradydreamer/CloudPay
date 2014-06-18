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
		//Net.connect("user/pwd/change", req, actionAfterModify)
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
	  	Scene.alert("密码修改成功，请重新登录",resultAction);	  		  	
  	}	
  }
  
  function resultAction(){
  	var params = null
		params = {
			shouldRemoveCurCtrl: true
		}
		window.user.init({});
		window.user.setLoginResult(function(){
			Scene.goBack();
		});
		Scene.showScene("Login","",params);
  }
  
 window.ModifyPwd = {
 	"gotoModifyPwd" : gotoModifyPwd,
  }
  
})()