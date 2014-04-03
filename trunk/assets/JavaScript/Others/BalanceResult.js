;
(function() {
  if (window.BalanceResult) {
    return
  }

  function reqBalance(data) {
    var msg = JSON.parse(data)
    var req = {
      data: msg.data8583
    }
    Net.connect("merchant/balance", req, actionAfterReqBalance, true)
  }

  function actionAfterReqBalance(data) {
    if (data.responseCode == "0") {
      g_loginRes = data
      var params = {
        data8583: data.data
      }
      window.data8583.convert8583(params, afterConvertBalance8583)
    } else {
      var DataErrorMsg = data.errorMsg
      if (null == DataErrorMsg || "" == DataErrorMsg) {
        DataErrorMsg = "系统异常"
      }
      Scene.alert(DataErrorMsg, goBackToOthers)
    }
  }

  function afterConvertBalance8583(data) {
    if ("00" != data.resCode) {
      Scene.alert(data.resMessage, goBackToOthers)
    } else {
      window.data8583.getBalance(showBalance)
    }
  }

  function showBalance(data) {
    var propertyList = [{
      name: "tv_title_balance",
      key: "hidden",
      value: false
    }, {
      name: "tv_balance",
      key: "text",
      value: window.util.formatAmountStr("" + data.balance)
    }]
    Scene.setProperty("BalanceResult", propertyList)
  }

  function goBackToOthers() {
    Scene.goBack("Home")
  }

  window.BalanceResult = {
    reqBalance: reqBalance,
  }

})()