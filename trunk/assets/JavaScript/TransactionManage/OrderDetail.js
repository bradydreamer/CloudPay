;
(function() {
  if (window.OrderDetail) {
    return;
  }
  var transType_Consume = 1021;
  var transType_ConsumeCancel = 3021;
  var transType_Refund = 3051;
  var transType_PreAuth = 1011;
  var transType_preAuthComplete= 1031;
  var transType_preAuthSettlement = 1091;
  var transType_preAuthCancel = 3011;
  var transType_preAuthCompleteCancel = 3031;
  
  function onRefund(data) {
    actionTransData8583(data, Pay.refundOrder, updateListRefund);
  }

  function onCancel(data) {
	var params = JSON.parse(data);
	//check index no, if it is mispos then don't get 8583 go pay flow --start add by Teddy on 3th July
	if (params.payKeyIndex == "90") {
  		Scene.alert("JSLOG onCancel 90");
  		ConsumptionData.dataForPayment.payKeyIndex = params.payKeyIndex;
  		ConsumptionData.dataForPayment.transAmount = params.transAmount;
  		ConsumptionData.dataForPayment.batchNo = params.batchNo;
  		ConsumptionData.dataForPayment.traceNo = params.traceNo;
  		ConsumptionData.dataForPayment.ref = params.ref;
  		ConsumptionData.dataForPayment.paymentId = params.paymentId;
  		ConsumptionData.dataForPayment.txnId = params.txnId;
  		ConsumptionData.dataForPayment.typeId = "REVERSE";
  		var formData = ConsumptionData.dataForPayment;
  		formData.Login = "LoginIndex.voidConfirmLogin";
  		var sceneName = "LoginVerify";
  		Scene.showScene(sceneName, "", formData);
		return;  	
  	} 
	//check index no, if it is mispos then don't get 8583 go pay flow --end add by Teddy on 3th July
	if (params.transType == transType_Consume) {
		actionTransData8583(data, Pay.cancelOrder, updateList);
	}else if(params.transType == transType_PreAuth){
		actionTransData8583(data, Pay.authCancelOrder, updateListAuthAllCancel);
	}else if(params.transType == transType_preAuthComplete){
		actionTransData8583(data, Pay.authCompleteCancelOrder, updateListAuthAllCancel);
	}	
  }

  function onAuthComplete(data){
	  actionTransData8583(data,Pay.authCompleterOrder,updateListAuthCompleteSettlement);
  }
  
  function onAuthSettlement(data){
	  actionTransData8583(data,Pay.authSettlementOrder,updateListAuthCompleteSettlement);
  }  
  
  function actionTransData8583 (data, actionFunc, succFunc) {
    var params = JSON.parse(data);
    var rrn = params.ref;
    var transTime = params.transTime;
    transTime = transTime.replace(/-/g, "");
    transTime = transTime.replace(/:/g, "");
    transTime = transTime.replace(/ /g, "");

	var open_brh = params.openBrh;
	var payment_id = params.paymentId;
	var txnId = params.txnId;
	var transType = params.transType;
	var data8583;
	
	window.OrderDetail.paymentId = params.paymentId;
    window.OrderDetail.paymentName = params.paymentName;
    window.Database.getAndCheckTransData8583(txnId, actionAfterGetTransData8583);

    function actionAfterGetTransData8583(data) {
      var transData8583 = data.res8583;
      actionFunc({
        "transData8583": transData8583,
        "rrn": rrn,
        "transTime": transTime,
        "openBrh": open_brh,
        "paymentId": payment_id,
        "txnId": txnId,
        "transType": transType,
      }, succFunc);
    }
  }
  
  function updateList() {
    var propertyList = [{
      name: "orderStatus",
      key: "text",
      value: "已撤销",
    }];
    Scene.setProperty("OrderDetail", propertyList);
    setTimeout(function() {
      Scene.goBack("OrderDetail");
    }, 300);
  }

  function updateListRefund() {
    var propertyList = [{
      name: "orderStatus",
      key: "text",
      value: "已退货",
    }];
    Scene.setProperty("OrderDetail", propertyList);
    setTimeout(function() {
      Scene.goBack("OrderDetail");
    }, 300);
  }
  
  function updateListAuthCompleteSettlement() {
    var propertyList = [{
        name: "orderStatus",
        key: "text",
        value: "已完成",
      }];
      Scene.setProperty("OrderDetail", propertyList);
  }
  
  function updateListAuthAllCancel() {
    var propertyList = [{
      name: "orderStatus",
      key: "text",
      value: "已撤销",
    }];
    Scene.setProperty("OrderDetail", propertyList);
  }
  
  function onPrint(data) {
    var params = JSON.parse(data);
    var rrn = params.ref;
	var txnId = params.txnId;
    //global variable for search pay result
    window.OrderDetail.paymentId = params.paymentId;
    window.OrderDetail.paymentName = params.paymentName;
    window.posPrint.printTrans(txnId);
  }
  
  window.OrderDetail = {
    "onRefund": onRefund,
    "onCancel": onCancel,
    "onAuthComplete": onAuthComplete,
    "onAuthSettlement": onAuthSettlement,
    "onPrint": onPrint
  };

})();