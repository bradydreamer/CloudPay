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

  var transCancelTag = false;
  
  function onRefund(data) {
    var params = JSON.parse(data);
    if(!params.timeValid){
        Scene.alert("请隔日以后进行退货", function() {
            if (ConsumptionData.dataForPayment.isExternalOrder) {
                Pay.errRestart();
            } else {
                Scene.goBack("OrderDetail");
            }
        });
        return;
    }
    actionTransData8583(data, Pay.refundOrder, updateListRefund);
  }

  function onCancel(data) {
  	transCancelTag = true;
	var params = JSON.parse(data);
	if(!params.timeValid){
        Scene.alert("159", function() {
            if (ConsumptionData.dataForPayment.isExternalOrder) {
                Pay.errRestart();
            } else {
                Scene.goBack("OrderDetail");
            }
        });
        return;
    }

	//check index no, if it is mispos then don't get 8583 go pay flow --start add by Teddy on 3th July
	//fix SMTPS-171 --start fixed by Teddy on 10th November
  	ConsumptionData.dataForPayment.payKeyIndex = params.payKeyIndex;
  	//fix SMTPS-171 --end fixed by Teddy on 10th November
	if (params.payKeyIndex == "90") {
  		ConsumptionData.dataForPayment.transAmount = util.yuan2fenStr(params.transAmount);
  		ConsumptionData.dataForPayment.batchNo = params.batchNo;
  		ConsumptionData.dataForPayment.traceNo = params.traceNo;
  		ConsumptionData.dataForPayment.ref = params.ref;
  		ConsumptionData.dataForPayment.paymentId = params.paymentId;
  		ConsumptionData.dataForPayment.txnId = params.txnId;
  		ConsumptionData.dataForPayment.typeId = "REVERSE";
  		var formData = ConsumptionData.dataForPayment;
  		formData.Login = "LoginIndex.voidConfirmLogin";
  		var sceneName = "LoginVerify";
  		Scene.showScene(sceneName, "166", formData);
		return;  	
  	}else if(params.payKeyIndex == "91") {
  		if (params.transType == transType_Consume) {
			params.transType = transType_ConsumeCancel;
  		}
  		ConsumptionData.dataForPayment.cashdata = params;
  		var formData = {
  		};  		
  		formData.Login = "LoginIndex.voidConfirmLogin";
  		var sceneName = "LoginVerify";
  		Scene.showScene(sceneName, "166", formData);
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
	  transCancelTag = false;
	  actionTransData8583(data,Pay.authCompleterOrder,updateListAuthCompleteSettlement);
  }
  
  function onAuthSettlement(data){
	  transCancelTag = false;
	  actionTransData8583(data,Pay.authSettlementOrder,updateListAuthCompleteSettlement);
  }
  
  function showCoupon(data) {
  	  var params = JSON.parse(data);
	  Scene.showScene("Coupon", "", params);
	  /*Scene.alert("是否进入酷券发券？", function (callBack) {
			if (callBack.isPositiveClicked == true) {
				Scene.showScene("Coupon", "", params);
			}
	  }, "确定", "取消");*/
	  
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
	var transType;
	if(params.payKeyIndex == null || params.payKeyIndex == undefined){
		Scene.alert("没有主密钥索引，请联系客服！");
		return;
	}
	if(transCancelTag){
		if (params.transType == transType_Consume) {
			transType = transType_ConsumeCancel;
		}else if(params.transType == transType_PreAuth){
			transType = transType_preAuthCancel;
		}else if(params.transType == transType_preAuthComplete){
			transType = transType_preAuthCompleteCancel;
		}
	}
	
	var transAmount = util.yuan2fenStr(params.transAmount);
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
        "transAmount": transAmount,
      }, succFunc);
    }
  }
  
  function updateList() {
    var propertyList = [{
      name: "orderStatus",
      key: "text",
      value: "113",
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
      value: "114",
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
  
  Pay.updataListCashCancel = function(){
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
  
  
  function onPrint(data) {
    var params = JSON.parse(data);
    var rrn = params.ref;
	var txnId = params.txnId;
    //global variable for search pay result
    window.OrderDetail.paymentId = params.paymentId;
    window.OrderDetail.paymentName = params.paymentName;
    window.posPrint.printTrans(txnId);
  }
  
  function writeBackSendCoupon (data) {
	var msg = JSON.parse(data);
	var action = "msc/txn/update";
	var req = {
  		txnId : msg.txnId,
  		stat: msg.stat
  	};
  	
  	Net.asynConnect(action, req, afterWriteBackSendCoupon, true);
  	function afterWriteBackSendCoupon(param) {
  		TransactionManageIndex.refreshResearch();
  	}
  }
  
  window.OrderDetail = {
    "onRefund": onRefund,
    "onCancel": onCancel,
    "onAuthComplete": onAuthComplete,
    "onAuthSettlement": onAuthSettlement,
    "onPrint": onPrint,
    "writeBackSendCoupon": writeBackSendCoupon,
    "showCoupon": showCoupon
  };

})();