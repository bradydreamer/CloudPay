;
(function() {
  if (window.data8583) {
    return;
  }
  var ERROR_TYPE_0 = "0x00";
  var ERROR_TYPE_1 = "0x01";
  var ERROR_TYPE_2 = "0x02";
  function convert8583(data, callback) {
    Global.callObjcHandler("convert8583", data, convert8583_callback);
    function convert8583_callback(data){
		if(data.error == "ERROR"){
			Scene.alert("161",function(){
				if(ConsumptionData.dataForPayment.isExternalOrder){
					Pay.restart();
				}else{			
					Scene.goBack("Home");
				}
			});
			return;
		}
		if(callback != null){
			callback(data);
		}
  	}
  }

  function get8583(data, callback) {
    Global.callObjcHandler("get8583", data, get8583_callback);
	function get8583_callback(data){
		if(data.error == ERROR_TYPE_0){
			Scene.alert("161",function(){
				window.RMS.clear("savedTransData");
				if(ConsumptionData.dataForPayment.isExternalOrder){
					Pay.restart();
				}else{			
					Scene.goBack("Home");
				}
			});
			return;
		}else if(data.error == ERROR_TYPE_1){
		    Scene.alert("171",function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });
		    return;
		}else if(data.error != null && data.error != undefined){
			Scene.alert("161",function(){
				if(ConsumptionData.dataForPayment.isExternalOrder){
					Pay.restart();
				}else{			
					Scene.goBack("Home");
				}
			});
			return;
		}
		
		if(callback != null){
			callback(data);
		}
  	}
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

  function printTrans(txnId) {
    window.Database.getAndCheckTransData8583(txnId, gotoPrint);
  }

  function gotoPrint(data) {
    if (null != data) {
      	data.userName = window.user.userName;
        data.txnId = ConsumptionData.dataForPayment.txnId;
        data.from = ConsumptionData.dataForPayment.from;
      	if ("" === ConsumptionData.dataForPayment.paymentId ||
      		undefined ===ConsumptionData.dataForPayment.paymentId) {
			data.paymentId = window.OrderDetail.paymentId;
      	} else {
			data.paymentId =  ConsumptionData.dataForPayment.paymentId;
      	}
      	if ("" === ConsumptionData.dataForPayment.paymentName || 
      		undefined ===ConsumptionData.dataForPayment.paymentName) {
			data.paymentName = window.OrderDetail.paymentName;
      	} else {
			data.paymentName =  ConsumptionData.dataForPayment.paymentName;
      	}
    } 
    if ("" === data.res8583 || null == data.res8583) {
		Scene.alert("交易已经过期");
    }
    Global.callObjcHandler("printTrans", data);
  }

  function printRecord(msg) {
    Global.callObjcHandler("PrintRecord", msg)
  }

  window.posPrint = {
    "printTrans": printTrans,
    "printRecord": printRecord
  };
})();

(function() {
  if (window.Database) {
    return;
  }

  function getAndCheckTransData8583(txnId, callbackfunc){
	  var data8583;
	  window.Database.getTransData8583(txnId, checkTransData8583);
	  function checkTransData8583(data){
			data8583 = data;
			if (data.res8583 == null || data.res8583 == "") {
				var req = {
					"txnId":txnId,
				}			
				Net.connect("msc/txn/detail/query", req, afterGetDetail, true);
			}else{
				if(callbackfunc){
					callbackfunc(data8583);
				}
			}		
		}		
		function afterGetDetail(resData){
			if("0" == resData.responseCode){
				var recordDisplayedList = resData.recordList;
				if(recordDisplayedList[0].txnId == txnId){
					recordDisplayedList[0].res8583 = recordDisplayedList[0].data;
					window.Database.insertTransData8583(txnId, null, recordDisplayedList[0].res8583);
					if(callbackfunc){
						callbackfunc(recordDisplayedList[0]);
					}else{
						Scene.alert("ERROR:NO Callback function！");
					}				
				}	
			}else{
			    Scene.alert(resData.errorMsg,function(){
                    Scene.goBack("Home");
                });
				/*if(callbackfunc){
					callbackfunc(data8583);
				}*/
			}		
		}	
  }

  function insertTransData8583(txnId, req8583, res8583, callbackfunc) {
    var params = {
      "txnId": txnId,
      "req8583": req8583,
      "res8583": res8583
    };
    Global.callObjcHandler("insertTransData8583", params, callbackfunc);
  }

  function updateTransData8583(txnId, req8583, res8583, callbackfunc) {
    var params = {
      "txnId": txnId,
      "req8583": req8583,
      "res8583": res8583
    };
    Global.callObjcHandler("updateTransData8583", params, callbackfunc);
  }

  function getTransData8583(txnId, callbackfunc) {
    var params = {
      "txnId": txnId,
    };
    Global.callObjcHandler("getTransData8583", params, callbackfunc);
  }

  function saveBankData(bankData) {
    if (null != bankData) {
        Global.callObjcHandler("saveBankData", bankData);
    }
  }

  window.Database = {
    "insertTransData8583": insertTransData8583,
    "updateTransData8583": updateTransData8583,
    "getTransData8583": getTransData8583,
    "getAndCheckTransData8583": getAndCheckTransData8583,
    "saveBankData" : saveBankData
  };
})();