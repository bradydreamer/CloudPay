;(function(){
 if (window.CreateUser) { return }
  
  function gotoCreate(data) {
  	var msg = JSON.parse(data)
  	var newOperator = msg.newOperator;
	var gradeId = msg.gradeId;
  	var pwd = msg.pwd;
  	
  	var req = {
  		merchId : window.user.merchId,
  		newOperator: newOperator,
  		aliasName: newOperator,
  		gradeId: gradeId,
  		pwd: pwd,
  	}
	Net.connect("msc/user/create", req, actionAfterCreate)
  }
  
  function actionAfterCreate(data) {
    if (data.responseCode != "0") {
	    Scene.alert(data.errorMsg)
	    return
  	} else {
	  	Scene.alert("创建用户成功",resultAction);	  		  	
  	}	
  }
  
  function resultAction(){
	  Scene.goBack("Home");
  }
  
 window.CreateUser = {
 	"gotoCreate" : gotoCreate,
  }
  
})()