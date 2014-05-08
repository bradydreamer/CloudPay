;
(function() {
  if (window.data8583) {
    return;
  }

  function convert8583(data, callback) {
    Global.callObjcHandler("convert8583", data, callback);
  }

  function get8583(data, callback) {
    Global.callObjcHandler("get8583", data, callback);
  }

  function getBalance(callback) {
    Global.callObjcHandler("getBalance", null, callback);
  }

  window.data8583 = {
    "convert8583": convert8583,
    "get8583": get8583,
    "getBalance": getBalance
  };
})();

(function() {
  if (window.posPrint) {
    return;
  }

  function printTrans(ref) {
    window.Database.getTransData8583(ref, gotoPrint);
  }

  function gotoPrint(data) {
    if (null != data) {
      	data.userName = window.user.userName;
      
      	if ("" === ConsumptionData.dataForPayment.paymentId) {
			data.paymentId = window.OrderDetail.paymentId;      	
      	} else {
			data.paymentId =  ConsumptionData.dataForPayment.paymentId;
      	}
      
      	if ("" === ConsumptionData.dataForPayment.paymentName) {
			data.paymentName = window.OrderDetail.paymentName;      	
      	} else {
			data.paymentName =  ConsumptionData.dataForPayment.paymentName;
      	}
    } else {
		Scene.alert("未找到对应纪录");
    }
    Global.callObjcHandler("printTrans", data);
  }

  window.posPrint = {
    "printTrans": printTrans,
  };
})();

(function() {
  if (window.Database) {
    return;
  }

  function insertTransData8583(ref, req8583, res8583, callbackfunc) {
    var params = {
      "ref": ref,
      "req8583": req8583,
      "res8583": res8583
    };
    Global.callObjcHandler("insertTransData8583", params, callbackfunc);
  }

  function updateTransData8583(ref, req8583, res8583, callbackfunc) {
    var params = {
      "ref": ref,
      "req8583": req8583,
      "res8583": res8583
    };
    Global.callObjcHandler("updateTransData8583", params, callbackfunc);
  }

  function getTransData8583(ref, callbackfunc) {
    var params = {
      "ref": ref,
    };
    Global.callObjcHandler("getTransData8583", params, callbackfunc);
  }

  window.Database = {
    "insertTransData8583": insertTransData8583,
    "updateTransData8583": updateTransData8583,
    "getTransData8583": getTransData8583,
  };
})();