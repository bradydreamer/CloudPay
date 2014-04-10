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
      // if (params.userName == "") {
      // Scene.alert("请输入账号。", null)
      // return
      // }
      // if (params.pwd == 0) {
      // Scene.alert("请输入密码。", null)
      // return
      // }
      gotoLogin(params)
    }

    function gotoLogin(params) {
      params.userName = "operator";
      var req = {
        merchId: window.user.merchId,
        iposId: window.user.machineId,
        operatorId: params.userName,
        pwd: params.pwd,
      }
      g_userName = params.userName
      //	req.operatorId = "operator"
      //	 req.pwd = "1111111!"
      //req.pwd = "111111aA"

      // req.merchId = "_TDS_" + req.merchId
      // req.iposId = "_TDS_" + req.iposId
      // req.operatorId = "_TDS_" + req.operatorId
      // req.pwd = "_TDS_" + req.pwd
      // req.headerCrypt = true
      g_loginReq = req
      var data = {
        typeOf8583: "login"
      }
      window.data8583.get8583(data, actionAfterConvert)
    }

    function actionAfterConvert(data) {
      var req = g_loginReq
      req.loginData = data.data8583
      Net.connect("merchant/login", req, actionAfterLogin)
    }

    function actionAfterLogin(data) {
      g_loginRes = data
      var params = {
        data8583: data.data
      }
      window.data8583.convert8583(params, actionAfterConvertLoginRes)
    }

    function actionAfterConvertLoginRes(data) {
      if ("00" != data.resCode) {
        Scene.alert(data.resMessage)
        return
      };
      if ("0" == g_loginRes.userStatus) {

        g_loginRes.transTime = data.transTime;
        delete g_loginRes.data;
        //ServiceMerchInfo.setInfo(g_loginRes);

        window.user.init(g_loginRes)
        window.user.userName = g_userName
        window.user.reloginAction(g_loginRes)
      } else {
        Scene.alert(g_loginRes.errorMsg)
      }
    }


	/*	
	目前有两种获取用户权限的方法。
	1.直接传入用户名、密码、商户号、终端号，以及登录类型（比如退货登录和撤销登录），前置直接返回应答，根据responseCode来判断是否有权限做
		相应的操作，如:0表示有权限，其它表示无权限。
	2.直接传入用户名、密码、商户号、终端号，前置直接返回应答，根据应答的gradeId来判断是否有权限做相应的操作。
	目前下面的代码是以第一种方式实现的。
	*/
	function refundConfirmLogin(params){
		/*
		1.这里要组req.登录和查询该用户的退货权限
		2.连接前置，并传callback->afterConfirmLogin
		3.callback根据response.gradeId,来判断是否是有权限的等级，如果返回的等级有权限，则继续进行退货流程，否则退回订单详情。
		*/
		var loginType = "refundLogin";
		confirmLogin(params,loginType);
		return;
	}

	function voidConfirmLogin(params){
		/*
		1.这里要组req.登录和查询该用户的退货权限
		2.连接前置，并传callback->afterConfirmLogin
		3.callback根据response.gradeId,来判断是否是有权限的等级，如果返回的等级有权限，则继续进行撤销流程，否则退回订单详情。
		*/
		var loginType = "voidLogin";
		confirmLogin(params,loginType);
		return;
	}
	
	function afterConfirmLogin(data){
		/*根据params.gradeId，查看该用户是否有权限，如果有权限，则继续进行相应的流程，如果没有权限，则退回订单详情*/
		
		/*
    */
    if(data.responseCode == "0"){
    	currentStep = Pay.cacheData.step;
    	currentTag = Pay.cacheData.flowList[currentStep].packTag;
    	Pay.cacheData.step = currentStep + 1;
    	Pay.gotoFlow();
    }else{
    	goback();
    }
	}
	
	function confirmLogin(params,loginType){
		 var req = {
        merchId: window.user.merchId,
        iposId: window.user.machineId,
        operatorId: params.userName,
        pwd: params.pwd,       
        loginType: loginType, 
      }
      if(loginType == "refundLogin"){
      	Net.connect("merchant/refundlogin", req, afterConfirmLogin);
      }else{
      	Net.connect("merchant/voidlogin", req, afterConfirmLogin);
      }
		
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