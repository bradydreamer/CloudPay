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

  function printOperatorTodayRecord(params) {
    var req;
    var data = JSON.parse(params);
    req = {
        pageNo : 1,
        pageSize : 999999,
        ioperator : data.ioperator,
//        type : "FOR_SETTLE",
        type : "FOR_STL_ALL"
    };

    Net.connect("msc/txn/page/query", req, handleResFromReqRecord);

    function handleResFromReqRecord(msg) {
        if(msg.responseCode == "0"){
            setTimeout(function() {
                msg.ioperator = data.ioperator;
                window.posPrint.printRecord(msg);
            }, 300);
        }else{
            Scene.alert(msg.errorMsg,function(){
                Scene.goBack("Home");
            });
        }
    }
  }

  window.ConsumptionRecord = {
    "getRecordDetail": getRecordDetail,
    "reqMore": reqMore,
    "goBack": goBack,
    "printOperatorTodayRecord": printOperatorTodayRecord,
  };

})();