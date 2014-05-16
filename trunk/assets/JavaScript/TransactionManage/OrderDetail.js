;
(function() {
  if (window.OrderDetail) {
    return;
  }

  function onRefund(data) {
    actionTransData8583(data, Pay.refundOrder, updateListRefund);
  }

  function onCancel(data) {
    actionTransData8583(data, Pay.cancelOrder, updateList);
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
	
	window.OrderDetail.paymentId = params.paymentId;
    window.OrderDetail.paymentName = params.paymentName;
    window.Database.getTransData8583(rrn, actionAfterGetTransData8583);

    function actionAfterGetTransData8583(data) {
      var transData8583 = data.res8583;
      actionFunc({
        "transData8583": transData8583,
        "rrn": rrn,
        "transTime": transTime,
        "openBrh": open_brh,
        "paymentId": payment_id,
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
  
  function onPrint(data) {
    var params = JSON.parse(data);
    var rrn = params.ref;
    //global variable for search pay result
    window.OrderDetail.paymentId = params.paymentId;
    window.OrderDetail.paymentName = params.paymentName;
    window.posPrint.printTrans(rrn);
  }

  window.OrderDetail = {
    "onRefund": onRefund,
    "onCancel": onCancel,
    "onPrint": onPrint,
  };

})();