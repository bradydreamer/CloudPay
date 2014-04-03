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

  window.LoginIndex = {
    onLogin: onLogin,
  }

})()