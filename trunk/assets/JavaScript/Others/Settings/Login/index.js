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
      window.user.init({});
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
		g_loginReq.remenber_tag = params.remenber_tag;
		g_loginReq.pwd_len = params.pwd_len;
		Net.connect("msc/user/login", req, actionAfterVerifyLogin)  //For real envir.
	}
	

	function actionAfterVerifyLogin(data){
		g_loginRes = data;
//		if("0" == g_loginRes.loginCount){
//		  	/*
//		  	此处跳转密码修改界面。密码界面修改完成后重新跳转至登录界面。
//		  	*/
//			Scene.alert("首次登录需要修改密码!", function(){
//				var params = {
//								shouldRemoveCurCtrl: true
//							}
//				Scene.showScene("ModifyPwd","",params);
//			});	  	
//			return;
//		  }		
		if ("0" == g_loginRes.responseCode) {
			/*if(g_loginRes.gradeId == "1"){
				Scene.alert("非操作员权限，请重新输入操作员账户！",backLogin);
				return;
			}*/
			g_loginRes.userName = g_loginReq.operator;
			g_loginRes.userStatus = g_loginRes.responseCode;
			RMS.read("merchant",getParamsVersion);
			window.COMM.checkSession();
			//start batch task to write back --start add by Teddy on September 5th.
			ConsumptionData.startProcessBatchTasks();
			//start batch task to write back --end add by Teddy on September 5th.
		} else {
			Scene.alert(g_loginRes.errorMsg)
		}	

		function getParamsVersion(verParams){
			if(g_loginReq.remenber_tag){
				var params = {
					merchId : g_loginReq.merchId,
					machineId : g_loginRes.iposId,
					operator: g_loginRes.userName,
					pwd: g_loginReq.pwd,
					pwd_len: g_loginReq.pwd_len,
					payParamVersion: g_loginRes.payParamVersion,
					remenber_tag: g_loginReq.remenber_tag
				}
			}else{
				var params = {
					merchId : g_loginReq.merchId,
					machineId : g_loginRes.iposId,
					operator: g_loginRes.userName,
					pwd: "0",
					pwd_len: -1,
					payParamVersion: g_loginRes.payParamVersion,
					remenber_tag: "0"
				}
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
	    		
		    	Scene.alert("128",function(){
			    	var sceneName = "MisposController";
			    	//rm goBack statement otherwise can't call back to the 3rd part app --start mod by Teddy on 11th November
//	  				Scene.goBack("Home");
			    	//rm goBack statement otherwise can't call back to the 3rd part app --end mod by Teddy on 11th November
	  				Scene.showScene(sceneName, "", ConsumptionData.dataForPayment);
		    	});
	    	} else if(ConsumptionData.dataForPayment.payKeyIndex == "91"){
	    		Scene.alert("128",function(){
	    			Pay.cashCancelOrder(ConsumptionData.dataForPayment.cashdata,Pay.updataListCashCancel);
	    		});
	    		
	    	}else {
		    	Scene.alert("128",function(){
		    	currentStep = Pay.cacheData.step;
				if(currentStep > Pay.cacheData.flowList.length){
					Scene.alert("120",function(){
						Scene.goBack("Home");
					});
					return;
				}
		    	currentTag = Pay.cacheData.flowList[currentStep].packTag;
		    	Pay.cacheData.step = currentStep + 1;
		    	Pay.gotoFlow();});
	    	}
	    }else{
	    	Scene.alert("129",goback);
	    	
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