;
(function() {
  if (window.Net) {
    return;
  };
  
  var _sessionId = "";
  var _keyExchange = false;
  var _callbackQueue = {};
  var _customAction = "";
  
  function isActionEqual(specAction, action, params) {
    if ((null != action && -1 != action.search(specAction))
      || (null != params && -1 != JSON.stringify(params).search(specAction))) {
      return true
    } else {
      return false
    }
  }

  function checkTransReverse(action, params, callBack) {
    if (!isActionEqual("base/verifyVersion", action, params) 
        && !isActionEqual("msc/user/login", action, params)
        && !isActionEqual("msc/pay/reverse", action, params)
        && !isActionEqual("msc/pay/signin", action, params)
        && !isActionEqual("msc/pay/signout", action, params)
        && !isActionEqual("msc/pay/consume/cancel", action, params)
        && !isActionEqual("msc/pay/batch/settle", action, params)
        && !isActionEqual("msc/txn/update",action, params)
        && !isActionEqual("msc/payment/info/query", action, params)
	&& !isActionEqual("msc/payment/template/query", action, params)
        && !isActionEqual("msc/cust/info/query", action, params)) {
      Pay.reverseOrder(callBack);
    } else {
      if (callBack) {
        callBack();
      };
    }
  }

  function saveTransData(action, params) {
    if (action == "msc/pay/consume") {
      var value = {
        "type": "reverse",
        "subType": "zhifuchongzheng",
        "data": params.data,
        "trans8583": params.data,
        "transDate": "",
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    } else if (action == "msc/pay/consume/cancel") {
      var value = {
        "type": "reverse",
        "subType": "chexiaochongzheng",
        "data": params.data,
        "trans8583": params.data,
        "transDate": ConsumptionData.dataForCancellingOrder.transDate,
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    }else if (action == "msc/pay/prepaid") {
       var value = {
        "type": "reverse",
        "subType": "preauthreverse",
        "data": params.data,
        "trans8583": params.data,
        "transDate": ConsumptionData.dataForCancellingOrder.transDate,
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    }else if (action == "msc/pay/prepaid/over") {
      var value = {
        "type": "reverse",
        "subType": "preauthcompletereverse",
        "data": params.data,
        "trans8583": params.data,
        "transDate": ConsumptionData.dataForCancellingOrder.transDate,
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    }else if (action == "msc/pay/prepaid/cancel") {
      var value = {
        "type": "reverse",
        "subType": "preauthcancelreverse",
        "data": params.data,
        "trans8583": params.data,
        "transDate": ConsumptionData.dataForCancellingOrder.transDate,
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    }else if (action == "msc/pay/prepaid/over/cancel") {
      var value = {
        "type": "reverse",
        "subType": "preauthcompletecancelreverse",
        "data": params.data,
        "trans8583": params.data,
        "transDate": ConsumptionData.dataForCancellingOrder.transDate,
        "brhKeyIndex":ConsumptionData.dataForPayment.brhKeyIndex,
      };
      window.RMS.save("savedTransData", value);
    }
  }

  function asynConnect(action, params, callbackfunc, isCustom) {
    connect(action, params, callbackfunc, isCustom, true);
  }

  function connect(action, params, callbackfunc, isCustom, isAsyn) {
    checkTransReverse(action, params,
      function() {
        saveTransData(action, params);
        gotoConnect(action, params, callbackfunc, isCustom, isAsyn);
      });
  }

  function gotoConnect(action, params, callbackfunc, isCustom, isAsyn) {
  	if( action == "msc/pay/signin" ||
			action == "msc/balance" ||
  			action == "msc/pay/consume" ||
  			action == "msc/pay/consume/cancel" ||
  			action == "msc/pay/refund" ||
  			action == "msc/pay/reverse" ||
  			action == "msc/pay/signout" ||
  			action == "msc/pay/batch/settle" ||
  			action == "msc/pay/prepaid" ||
  			action == "msc/pay/prepaid/over" ||
  			action == "msc/pay/prepaid/over/offline" ||
  			action == "msc/pay/prepaid/cancel" ||
  			action == "msc/pay/prepaid/over/cancel"){
  		action = "txn/"+ConsumptionData.dataForPayment.brhKeyIndex;
  	}
    var reqData = _request(action, params, callbackfunc);
    _customAction = isCustom ? action : "";
    if (isAsyn) {
      Global.callObjcHandler("netAsynConnect", reqData, _callBackConnect)
    } else {
      Global.callObjcHandler("netConnect", reqData, _callBackConnect)
    }
  }

  function _callBackConnect(response) {
    if (response == null) {
      return;
    }

    //联网错误
    if (response.errCode) {
      if (response.keyExchange == "1") {
        _keyExchange = true;
      }
      if (_customAction == "") {
        Scene.alert(response.errorMsg);
      } else {
        var func = _callbackQueue[_customAction];
        if (func) {
          func(response);
        };
      };
      return
    }

    //数据错误
    if (response.header) {
      if (response.header.keyExchange) {
        _keyExchange = true;
      } else {
        _sessionId = response.header.session;
      }
    }

    //数据正常
    if (response.body instanceof Array) {
      for (var index in response.body) {
        var data = response.body[index];
        if (_checkServerErr(data)) {
          return
        };

        if (data.action == null || data.action.length == 0) {
          if (_customAction == "") {
            if (data.errorMsg != null) {
              Scene.alert(data.errorMsg,actionAfterError)
            } else {
              Scene.alert("通信故障",actionAfterError)
            }
          } else {
            var func = _callbackQueue[_customAction];
            if (func) {
              func(response);
            };
          };
          return;
        };
        var func = _callbackQueue[data.action];
        if (func) {
			delete _callbackQueue[data.action];
			_responseForAction(data, func);
        }
      }
    }
  }
  
  function actionAfterError(){
	  Scene.goBack("Home"); 
  }
  

  function _responseForAction(data, func) {
    if (data.action == _customAction) {
      return func(data)
    }
    if (data.responseCode == "0") {
      return func(data)
    } else {
      Scene.alert(data.errorMsg)
    }
  }

  function _request(action, params, callbackfunc) {
    var reqData = {
      "header": {},
      "body": [],
    };
	
	if(action == "msc/user/login")
	{
		_sessionId = "-1";
	}
	reqData.header.session = _sessionId;
    if (_sessionId == "" || _sessionId == "-1") {
      _keyExchange = true;
    }
    if (_keyExchange) {
      reqData.header.keyExchange = "1";
      _keyExchange = false;
    }

    if (params instanceof Array) {
      reqData.body = params;
      for (var i in params) {
        var reqObj = params[i];
        if (reqObj.headerCrypt) {
          delete reqObj.headerCrypt;
          reqData.header.crypt = "1";
        }
        _callbackQueue[reqObj.action] = callbackfunc[i];
      }
    } else {
      params.action = action;
      if (params.headerCrypt) {
        delete params.headerCrypt;
        reqData.header.crypt = "1";
      }
      reqData.body = [params];
      _callbackQueue[action] = callbackfunc;
    }

    return reqData;
  }

  function _checkServerErr(data) {
    //服务器重启
    if (data.responseCode == "90"
		||data.responseCode == "91"
		||data.responseCode == "92") {
      
      _sessionId = "-1";
      _keyExchange = true;
      Scene.alert(data.errorMsg,function(){
      	window.user.init({});
		Scene.goBack("Home");
		setTimeout(window.util.exeActionWithLoginChecked,500);      	
      });
			

      return true;
    }
    return false;
  }

  window.Net = {
    "asynConnect": asynConnect,
    "connect": connect
  }
})();