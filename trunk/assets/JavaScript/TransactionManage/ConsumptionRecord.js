;
(function() {
  if (window.ConsumptionRecord) {
    return;
  }

  function getRecordDetail(data) {
    var msg = JSON.parse(data); 
    msg.confirm = "ConsumptionRecord.goBack";
    Scene.showScene("OrderDetail", "", msg);
  }

  function reqMore() {
    var params = {
      "isReqMore": true
    };
    window.TransactionManageIndex.gotoConsumptionRecord(params);
  }

  function goBack() {
    Scene.goBack("");
  }

  window.ConsumptionRecord = {
    "getRecordDetail": getRecordDetail,
    "reqMore": reqMore,
    "goBack": goBack,
  };

})();