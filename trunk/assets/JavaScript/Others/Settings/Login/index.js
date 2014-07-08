;
(function() {
  if (window.LoginIndex) {
    return
  }

  var g_loginReq
  var g_loginRes
  var g_userName
  

	function onLogin(msg) {
	  var params = JSON.parse(msg)
	
	  gotoLogin(params)
	}


	function gotoLogin(params) {
		var req = {
		  merchId: params.merchId,
		  operator: params.userName,
		  pwd: params.pwd,
		  iposSn: params.ssn,
		}

		g_loginReq = req
		Net.connect("msc/user/login", req, actionAfterVerifyLogin)  //For real envir.
	}
	

	function actionAfterVerifyLogin(data){
		g_loginRes = data;
		if("0" == g_loginRes.loginCount){
		  	/*
		  	此处跳转密码修改界面。密码界面修改完成后重新跳转至登录界面。
		  	*/
		  	var params = {
					shouldRemoveCurCtrl: true
				}
		  	Scene.showScene("ModifyPwd","",params);
			return;
		  }		
		if ("0" == g_loginRes.responseCode) {
			/*if(g_loginRes.gradeId == "1"){
				Scene.alert("非操作员权限，请重新输入操作员账户！",backLogin);
				return;
			}*/
			g_loginRes.userName = g_loginReq.operator;
			g_loginRes.userStatus = g_loginRes.responseCode;
			RMS.read("merchant",getParamsVersion);
			
		} else {
			Scene.alert(g_loginRes.errorMsg)
		}	

		function getParamsVersion(verParams){
			
			var params = {
				merchId : g_loginReq.merchId,
				machineId : g_loginRes.iposId,
				operator: g_loginRes.userName,
				payParamVersion: g_loginRes.payParamVersion,
			}
			RMS.save("merchant",params);
			
			window.user.init(g_loginRes);
			window.user.merchId = g_loginReq.merchId;
			window.user.machineId = g_loginRes.iposId;
			window.user.userName = g_loginRes.userName;
			window.user.gradeId = g_loginRes.gradeId;
			//SettingsIndex.getMerchantInfoAfterLogin();
			if(g_loginRes.payParamVersion != verParams.payParamVersion){
				var mParam = {
					shouldRemoveCurCtrl: false,
				};
				Scene.showScene("SettingsDownload", "", mParam);
			}else{
				window.user.reloginAction(g_loginRes);
			}
		}
	}
    
	function backLogin(){
		/*登录界面*/
		Scene.goBack("Login");

	}



	/*	
	目前有两种获取用户权限的方法。
	1.直接传入用户名、密码、商户号、终端号，以及登录类型（比如退货登录和撤销登录），前置直接返回应答，根据responseCode来判断是否有权限做
		相应的操作，如:0表示有权限，其它表示无权限。
	2.直接传入用户名、密码、商户号、终端号，前置直接返回应答，根据应答的gradeId来判断是否有权限做相应的操作。
	目前下面的代码是以第二种方式实现的。
	*/
	function refundConfirmLogin(msg){
		/*
		1.这里要组req.登录和查询该用户的退货权限
		2.连接前置，并传callback->afterConfirmLogin
		3.callback根据response.gradeId,来判断是否是有权限的等级，如果返回的等级有权限，则继续进行退货流程，否则退回订单详情。
		*/
		var params = JSON.parse(msg);
		confirmLogin(params);
		return;
	}

	function voidConfirmLogin(msg){
		/*
		1.这里要组req.登录和查询该用户的退货权限
		2.连接前置，并传callback->afterConfirmLogin
		3.callback根据response.gradeId,来判断是否是有权限的等级，如果返回的等级有权限，则继续进行撤销流程，否则退回订单详情。
		*/
		var params = JSON.parse(msg);
		confirmLogin(params);
		return;
	}
	
	function afterConfirmLogin(data){
		/*根据params.gradeId，查看该用户是否有权限，如果有权限，则继续进行相应的流程，如果没有权限，则退回订单详情*/
		
		/*
		gradeId为："1":高权限，"2":低
    */
    if(data.gradeId == "1"){
    	if (ConsumptionData.dataForPayment.payKeyIndex == "90") {
    		
	    	Scene.alert("权限确认成功",function(){
		    	var sceneName = "MisposController";
  				Scene.goBack("Home");
  				Scene.showScene(sceneName, "", ConsumptionData.dataForPayment);
	    	});
    	} else {
    		
	    	Scene.alert("权限确认成功",function(){
	    	currentStep = Pay.cacheData.step;
	    	currentTag = Pay.cacheData.flowList[currentStep].packTag;
	    	Pay.cacheData.step = currentStep + 1;
	    	Pay.gotoFlow();});
    	}
    }else{
    	Scene.alert("权限确认失败！",goback);
    	
    }
	}
	
	function confirmLogin(params){
		 var req = {
        merchId: window.user.merchId,
        iposId: window.user.machineId,
        operator: params.userName,
        pwd: params.pwd,
      }
	Net.connect("msc/user/verify", req, afterConfirmLogin);
		
	}
  
  function goback(){
  	/*退回订单详情*/
  	Scene.goBack("OrderDetail");
  	
  }
    
  window.LoginIndex = {
    onLogin: onLogin,
		refundConfirmLogin: refundConfirmLogin,
		voidConfirmLogin: voidConfirmLogin
  }

})()