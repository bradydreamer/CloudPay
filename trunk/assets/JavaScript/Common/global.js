;
(function() {
  if (window.Global) {
    return
  }

  var messagingIframe;
  var msg;
  var callbackQueue = {};
  var uniqueId = 0;

  function _createQueueReadyIframe(doc) {
    Global.messagingIframe = doc.createElement('iframe')
    Global.messagingIframe.style.display = 'none'
    doc.documentElement.appendChild(Global.messagingIframe)
  }

  function calloc(func, params, callbackfunc) {
    var funcData = {
      handler: func,
      params: params,
    }

    if (callbackfunc) {
      var callbackId = 'js_cb_' + (uniqueId++)
      funcData.callbackId = callbackId

      callbackQueue[callbackId] = function(data) {
        if (callbackfunc) {
          callbackfunc(data)
        } else {
          // Scene.alert("cannot find objcCallBack func")
        }
      }
    }
    msg = funcData
    Global.callPlatform(funcData)
  }

  function callPlatform(funcData) {

  }

  function objcResponse(data) {
    var message = JSON.parse(data)
    if (message.responseId) {
      var handler = callbackQueue[message.responseId]
      if (handler) {
        var completed = handler(message.data)
        if (completed != false) {
          delete callbackQueue[message.responseId]
        }
      } else {
        // Scene.alert("cannot find callbackQueue func:" + message.responseId)
      }
    }
    return "ok"
  }

  function callJS(data) {
    var message = JSON.parse(data)
    var result
    try {
      result = eval(message.handler)(JSON.stringify(message.params))
    } catch (ex) {
      Scene.alert("callJSError:" + ex.name + "; " + ex.message)
    } finally {
      if (result == null) {
        result = ""
      }
    }
    JSResponser.sendJsResponse(result)
  }

  function fetchMessage() {
    return JSON.stringify(msg)
  }

  function clearGlobal() {
    window.getFormData = null;
    window.Temp = {};
  }

  function getSystemInfo(callbackfunc) {
    Global.callObjcHandler("getSystemInfo", "", callbackfunc)
  }

  function openUrl(url, callbackfunc) {
    Global.callObjcHandler("openUrl", {
      "url": url
    }, callbackfunc);
  }

  function clearCache(data) {
    var params = JSON.parse(data);
    var cacheList = params.list;
    for (var index in cacheList) {
      var cache = cacheList[index];
      window[cache] = null;
    };
  }

  function exit() {
    Global.callObjcHandler("exit", "");
  }

  function getMerchInfo(callbackfunc) {
    Global.callObjcHandler("getSystemInfo", "", callbackfunc)
  }

  window.Global = {
    "callObjcHandler": calloc,
    "callPlatform": callPlatform,
    "messagingIframe": messagingIframe,
    "objcResponse": objcResponse,
    "fetchMessage": fetchMessage,
    "clearGlobal": clearGlobal,
    "getSystemInfo": getSystemInfo,
    "callJS": callJS,
    "exit": exit,
    "openUrl": openUrl,
    "clearCache": clearCache,
  };

  var doc = document;
  _createQueueReadyIframe(doc);
})();

(function() {
  function getInfo(callbackfunc) {
    var params = {
      "action": "get",
    }
    Global.callObjcHandler("ServiceSecureInfo", params, callbackfunc)
  }

  function setInfo(value, callbackfunc) {
    var params = {
      "action": "set",
      "value": value,
    }
    Global.callObjcHandler("ServiceSecureInfo", params, callbackfunc)
  }
  
  function setMerchInfo(value, callbackfunc) {
    var params = {
      "action": "set",
      "value": value,
    };
    Global.callObjcHandler("ServiceMerchInfo", params, callbackfunc);
  }

  window.ServiceMerchInfo = {
    "getInfo": getInfo,
    "setInfo": setInfo,
    "setMerchInfo": setMerchInfo
  };
})();

(function() {
  if (window.Scene) {
    return
  }

  function showScene(sName, sTitle, sData) {
    var scene = {
      "name": sName,
      "title": sTitle,
    };
    if (sData) {
      scene.data = sData
    }
    Global.callObjcHandler("showScene", scene, null);
  }

  function alert(msg, callbackfunc, positiveButtonText, negativeButtonText) {
    var params = {
      "msg": msg,
    };
    if (null != positiveButtonText) {
      params.positiveButtonText = positiveButtonText;
    };
    if (null != negativeButtonText) {
      params.negativeButtonText = negativeButtonText;
    };
    Global.callObjcHandler("alert", params, callbackfunc);
  }

  function setProperty(sceneNm, propertyList) {
    var data = {
      "controller": sceneNm,
      "params": propertyList,
    }
    Global.callObjcHandler("setProperty", data, null);
  }

  function goBack(sName, sData) {
    var scene = {
      "name": sName,
    }
    if (sData) {
      scene.data = sData
    }
    Global.callObjcHandler("goBack", scene, null);
  }
  window.Scene = {
    "showScene": showScene,
    "alert": alert,
    "setProperty": setProperty,
    "goBack": goBack,
  };
})();

(function() {
  if (window.RMS) {
    return
  }

  function readLocal(key, callbackfunc) {
    var params = {
      "key": key,
    }
    Global.callObjcHandler("readLocal", params, callbackfunc);
  }
  
  function readLocalBatch(callbackfunc) {
    Global.callObjcHandler("readLocalBatch", null, callbackfunc);
  }

  function saveLocal(key, value) {
    var params = {
      "key": key,
      "value": value,
    }
    Global.callObjcHandler("saveLocal", params);
  }

  function clearLocal(key, callbackfunc) {
    var params = {
      "key": key,
    }
    Global.callObjcHandler("clearLocal", params, callbackfunc);
  }
  
  function rmBatchByPrimaryKey(data) {
	Global.callObjcHandler("rmBatchTask", data);
  }
  
  function saveMerchSettingToDataBase(callbackfunc) {
	Global.callObjcHandler("saveMerchSettingToDataBase", "", callbackfunc);
  }

  window.RMS = {
    "read": readLocal,
    "save": saveLocal,
    "clear": clearLocal,
    "rmBatchByPrimaryKey": rmBatchByPrimaryKey,
    "saveMerchSettingToDataBase": saveMerchSettingToDataBase,
    "readLocalBatch": readLocalBatch
  };
})();

(function() {
  if (window.AppInit) {
    return
  }

  function initResult(value) {
  	var param = {
		"value": value
	}
	if(value == window.FAIL){
		window.user.init({});
	}
	window.BackgroundInit = false;
	Global.callObjcHandler("initResult", param);
  }
  
  function summaryCallBack(params) {
  	Global.callObjcHandler("summaryCallBack", params);
  }
  function orderListCallBack(params) {
  	Global.callObjcHandler("orderListCallBack", params);
  }

  window.AppInit = {
    "initResult": initResult,
    "orderListCallBack": orderListCallBack,
    "summaryCallBack" : summaryCallBack
  };  
})();
window.SUCC = 0;
window.FAIL = 1;
window.BackgroundInit = false;
window.downloadParams = false;
(function() {
  if (window.COMM) {
    return
  }

  function checkSession() {
    Global.callObjcHandler("checkSessionTime", null, null);
  }
  
  function stopCheckSession(){
	Global.callObjcHandler("stopCheckSessionTime", null, null);
  }

  function deleteParamsFiles(){
  	window.downloadParams = false;
	Global.callObjcHandler("deleteParamsFiles", null, null);
  }
  window.COMM = {
    "checkSession": checkSession,
	"stopCheckSession": stopCheckSession,
	"deleteParamsFiles": deleteParamsFiles
  };
})();
