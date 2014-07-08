;(function(){
 if (window.SignIn) { return }
  
  function gotoSignIn() {  	
  	var data = {
  	          typeOf8583: "signin",
			  paymentId: ConsumptionData.dataForPayment.paymentId,
			  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex
  	    }
  	window.data8583.get8583(data, actionAfterGetMsg);

  }
  
  function actionAfterGetMsg(params){
	  var req = {
	   		"data": params.data8583,
	   		"paymentId": params.paymentId,
	   		"transType": params.transType,
	   		"batchNo": params.batchNo,
	   		"traceNo": params.traceNo,
	   		"transTime": params.transTime,
	   		"cardNo": params.cardNo,
	   		"transAmount": params.transAmount,
	   		"oriTxnId": params.oriTxnId,
	   		"oriBatchNo": params.oriBatchNo,
	   		"oriTraceNo": params.oriTraceNo,
	   		"oriTransTime": params.oriTransTime,
	   	};
	  Net.connect("msc/pay/signin",req,actionAfterSignIn);  
  }
  
  function actionAfterSignIn(params){
	if("0" == params.responseCode){
		var convertData = {
				"data8583": params.data,
				"brhKeyIndex": ConsumptionData.dataForPayment.brhKeyIndex
		};			
	ConsumptionData.dataForPayment.res8583 = params.data;	
	ConsumptionData.dataForPayment.txnId = params.txnId;
	window.data8583.convert8583(convertData, afterConvertMsg);
	}else{
		Scene.alert(params.errorMsg,errProcess);
	}   
  }  

  function errProcess(){
	Scene.goBack("Home");
  }
  
  function afterConvertMsg(params){
	  var req = {
	        "txnId": ConsumptionData.dataForPayment.txnId,
			"resCode": params.resCode,
			"resMsg": params.resMessage,
			"refNo": params.rrn,
			"authNo": params.authNo,
			"issuerId": params.issuerId,
			"dateExpr": params.dateExpr,
			"stlmDate": params.stlmDate,
		};
	  var brhKeyIndex = ConsumptionData.dataForPayment.brhKeyIndex;
	  var _params = {
 				"signature" : true,
 		  }
	  RMS.save(brhKeyIndex, _params);
	  actionAfterSet();
	  Net.asynConnect("msc/txn/update",req,afterBackupInfo);
  }
  
  function afterBackupInfo(data){
	  if("0" == data.responseCode){
		  return;
	  }else{
		  Scene.alert(data.errorMsg);
	  }
  }  
  
  function actionAfterSet() {
      window.user.signInAction();
  }
  
 window.SignIn = {
 	"gotoSignIn" : gotoSignIn,
  }
  
})()
