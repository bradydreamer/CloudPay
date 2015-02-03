;(function(){
 if (window.SignIn) { return }
  
  var paramType;
  function gotoSignIn() {  	
  	var data = {
  	          typeOf8583: "signin",
			  paymentId: ConsumptionData.dataForPayment.paymentId,
			  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex
  	    }
  	//window.data8583.get8583(data, actionAfterGetMsg);
	Pay.checkTransReverse("msc/pay/signin",function(){
		window.data8583.get8583(data, actionAfterGetMsg);
	});

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
	   		"oriTransTime": params.oriTransTime
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
		Scene.alert(params.errorMsg,errOKProcess);
	}   
  }  

  function errOKProcess(){
	  if(ConsumptionData.dataForPayment.isExternalOrder){
			Pay.restart();
		}else{			
			Scene.goBack("Home");
		}
  }
  
  function afterConvertMsg(params){
	  if("00" == params.resCode){
		  var brhKeyIndex = ConsumptionData.dataForPayment.brhKeyIndex;
		  var _params = {
	 				"signature" : true
	 		  }
		  RMS.save(brhKeyIndex, _params);
		  if(params.paramDownloadFlag == true){			  
			  paramType = "CAPK";
			  posUpStatus(downloadCAPK);
		  }else{
			  actionAfterSet();
		  }
	  }else{
		  Scene.alert(params.resMessage,errOKProcess);
	  }	  
  }
  
  function afterBackupInfo(data){
	  if("0" == data.responseCode){
		  return;
	  }else{
		  Scene.alert(data.errorMsg);
	  }
  }  
  
  function actionAfterSet() {
      window.user.afterSignInAction();
  }
  
  var ICCapkParamsCount = 1
  
  function posUpStatus(callback){
  	  window.downloadParams = true;
	  var datalist = [{
		  title: "145",
		  content: "146"
	  }];
	  Scene.setProperty("SignIn",datalist);
	  
	  var data = {
	  	          typeOf8583: "posUpStatus",
				  paymentId: ConsumptionData.dataForPayment.paymentId,
				  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex,
				  paramType: paramType
	  	    }
	  Pay.checkTransReverse("msc/pay/signin",function(){
			window.data8583.get8583(data, actionAfterGet);
		});
	  
	  function actionAfterGet(params){
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
				"oriTransTime": params.oriTransTime
		  };
			  
		  Net.connect("msc/pay/signin",req,actionAfterUpStatus); 
	  }
	  
	  function actionAfterUpStatus(params){
		  if("0" == params.responseCode){			  
			  var convertData = {
					  "data8583": params.data,
					  "brhKeyIndex": ConsumptionData.dataForPayment.brhKeyIndex
			  };			
			  ConsumptionData.dataForPayment.res8583 = params.data;	
			  ConsumptionData.dataForPayment.txnId = params.txnId;
			  window.data8583.convert8583(convertData, afterConvert);
		  }else{
		  	  window.COMM.deleteParamsFiles();
			  Scene.alert(params.errorMsg,errOKProcess);
		  } 
	  
	  }
	  
	  function afterConvert(params){
		  if("00" == params.resCode){
			  if(params.paramsCapkCheckNeed == true){
				  posUpStatus(callback);
			  }else{
				  callback();		  
			  }
		  }else{
		  	  window.COMM.deleteParamsFiles();
			  Scene.alert(params.resMessage,errOKProcess);
		  }	
	  
	  }
	  
	  
  }
  
  function downloadICParams(){
	  var content = "" + ICCapkParamsCount;      //"正在下载第" + ICCapkParamsCount + "组参数！";
	  var datalist = [{
		  title: "145",
		  type: "0", //0:参数
		  content: content 
	  }];
	  Scene.setProperty("SignIn",datalist);
	  
	  var data = {
	  	          typeOf8583: "downloadParams",
				  paymentId: ConsumptionData.dataForPayment.paymentId,
				  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex,
				  paramType: paramType
	  	    }
	  Pay.checkTransReverse("msc/pay/signin",function(){
			window.data8583.get8583(data, actionAfterGet);
		});
	  
	  function actionAfterGet(params){
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
		 	   		"oriTransTime": params.oriTransTime
		 	   	};
		  
		  Net.connect("msc/pay/signin",req,actionAfterDownload); 
	  }
	  
	  function actionAfterDownload(params){
		  if("0" == params.responseCode){			  
			  var convertData = {
					  "data8583": params.data,
					  "brhKeyIndex": ConsumptionData.dataForPayment.brhKeyIndex
			  };			
			  ConsumptionData.dataForPayment.res8583 = params.data;	
			  ConsumptionData.dataForPayment.txnId = params.txnId;
			  window.data8583.convert8583(convertData, afterConvert);
		  }else{
		      window.COMM.deleteParamsFiles();
			  Scene.alert(params.errorMsg,errOKProcess);
		  }  	  
	  }	
	  
	  function afterConvert(params){
		  if("00" == params.resCode){
			  if(params.paramsCapkDownloadNeed == true){				 
				  ICCapkParamsCount ++;
				  //TOD0：下载参数
				  downloadICParams();
			  }else{
				  downloadCAPKParamEnd(function(){
					  ICCapkParamsCount = 1;
					  actionAfterSet();
				  });					  		  
			  }
		  }else{
		      window.COMM.deleteParamsFiles();
			  Scene.alert(params.resMessage,errOKProcess);
		  }	
	  
	  }  
  }
  
  function downloadCAPK(){
	  var content = "" + ICCapkParamsCount;     //"正在下载第" + ICCapkParamsCount + "组公钥！";
	  var datalist = [{
		  title: "145",
		  type: "1", //1:表示公钥
		  content: content 
	  }];
	  Scene.setProperty("SignIn",datalist);
	  var data = {
	  	          typeOf8583: "downloadParams",
				  paymentId: ConsumptionData.dataForPayment.paymentId,
				  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex,
				  paramType: paramType
	  	    }
	  Pay.checkTransReverse("msc/pay/signin",function(){
			window.data8583.get8583(data, actionAfterGet);
		});
	  
	  function actionAfterGet(params){
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
		 	   		"oriTransTime": params.oriTransTime
		 	   	};
		  
		  Net.connect("msc/pay/signin",req,actionAfterDownload);
	  
	  }
	  
	  function actionAfterDownload(params){
		  if("0" == params.responseCode){			  
			  var convertData = {
					  "data8583": params.data,
					  "brhKeyIndex": ConsumptionData.dataForPayment.brhKeyIndex
			  };			
			  ConsumptionData.dataForPayment.res8583 = params.data;	
			  ConsumptionData.dataForPayment.txnId = params.txnId;
			  window.data8583.convert8583(convertData, afterConvert);
		  }else{
		      window.COMM.deleteParamsFiles();
			  Scene.alert(params.errorMsg,errOKProcess);
		  }  
	  
	  }
	  
	  function afterConvert(params){
		  if("00" == params.resCode){
			  if(params.paramsCapkDownloadNeed == true){				 
				  ICCapkParamsCount ++;
				  //TOD0：下载参数
				  downloadCAPK();
			  }else{
				  downloadCAPKParamEnd(function(){
					  ICCapkParamsCount = 1;
					  paramType = "PARAM";
					  posUpStatus(downloadICParams);
				  });				  
			  }
		  }else{
		      window.COMM.deleteParamsFiles();
			  Scene.alert(params.resMessage,errOKProcess);
		  }		  
	  }  
  }  
  
  function downloadCAPKParamEnd(nextCallback){
		var content;
		if(paramType == "PARAM"){
			content = "163"
		}else{	  
			content = "164";
		}	  
		var datalist = [{
			  title: "145",
			  content: content 
		}];
		Scene.setProperty("SignIn",datalist);
	  
		var data = {
			          typeOf8583: "downloadEnd",
					  paymentId: ConsumptionData.dataForPayment.paymentId,
					  brhKeyIndex: ConsumptionData.dataForPayment.brhKeyIndex,
					  paramType: paramType
			    }
		Pay.checkTransReverse("msc/pay/signin",function(){
				window.data8583.get8583(data, actionAfterGet);
			});
		
		function actionAfterGet(params){
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
			 	   		"oriTransTime": params.oriTransTime
			 	   	};
			  
			  Net.connect("msc/pay/signin",req,actionAfterEnd);		
		}
		
		function actionAfterEnd(params){
			if("0" == params.responseCode){			  
				  var convertData = {
						  "data8583": params.data,
						  "brhKeyIndex": ConsumptionData.dataForPayment.brhKeyIndex
				  };			
				  ConsumptionData.dataForPayment.res8583 = params.data;	
				  ConsumptionData.dataForPayment.txnId = params.txnId;
				  window.data8583.convert8583(convertData, afterConvert);
			 }else{
			      window.COMM.deleteParamsFiles();
				  Scene.alert(params.errorMsg,errOKProcess);
			 }  
		}	
		
		function afterConvert(params){
			if("00" == params.resCode){
				window.downloadParams = false;
				nextCallback();
			}else{
				window.COMM.deleteParamsFiles();
				Scene.alert(params.resMessage,errOKProcess);
			}	
		}
		
  }
  
  
  
  var currentIndex = 0;
  var keyIndex = -1;
  var signTag = {};
  var merchSettings = {};
  
  function gotoSignOut(){	  
	currentIndex = 0;
  	keyIndex = -1;
  	signTag = {};
  	merchSettings = {};
  	Scene.alert("Toast:正在签退，请稍后重新操作！");
	if (window.merchSettings == null) {
		window.RMS.read("merchSettings", afterGetTransInfo);
	} else {
		afterGetTransInfo(window.merchSettings);
	}
	
	function afterGetTransInfo(data){
		window.merchSettings = data;
		var settingString = data.settingString;
		if (settingString == null || settingString.length == 0) {
			window.util.showSceneWithLoginChecked("SettingsDownload");
			return;
		};
		merchSettings = JSON.parse(settingString);
		if (merchSettings == null || merchSettings.length == 0) {
			return;
		};		
		parseMerchSettings();
	}	
  }
  function parseMerchSettings(){			
	if(currentIndex < merchSettings.length){
		keyIndex = merchSettings[currentIndex].brhKeyIndex;
		if(signTag[keyIndex] == "" || signTag[keyIndex] == null){
			signOut(keyIndex);
			signTag[keyIndex] = true;
			
		}else{
			currentIndex++;
			parseMerchSettings();
		}		
	}else{
//	    Scene.alert("签退完成！",errOKProcess);
		errOKProcess();
	}			
  }	
  
  function signOut(keyIndex){
	//通联MISpos方案，这里进行滤掉
	if(keyIndex == "90" || keyIndex == "91"){
		currentIndex++;
		parseMerchSettings();
		return;
	}	
	ConsumptionData.dataForPayment.brhKeyIndex = keyIndex;
	var data = {
  		typeOf8583: "signout",
	}
	//window.data8583.get8583(data, actionAfterGet)
	Pay.checkTransReverse("msc/pay/signout",function(){
		window.data8583.get8583(data, actionAfterGet);
	});
  }  
  function actionAfterGet(params) {
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
      }

      Net.connect("msc/pay/signout", req, actionAfterSignout);
  }
  
    function actionAfterSignout(data){
        if(data.responseCode == "0"){
            var params = {
                data8583: data.data
            }
            window.data8583.convert8583(params, actionAfterConvertSignoutRes);
        }else{
            Scene.alert(data.errorMsg,function(){
                Scene.goBack("Home");
            });
        }
    }
	
	function actionAfterConvertSignoutRes(data){
	    if ("00" != data.resCode) {
		    Scene.alert(data.resMessage,errOKProcess);
		    return;
	  	} else {
	  		var _params = {
	  		 				"signature" : false,
	  		 		  }
	  		RMS.save(keyIndex, _params);
	  		currentIndex++;
	  		parseMerchSettings();
		}	
	}
  
  window.SignIn = {
	"gotoSignIn": gotoSignIn,
 	"gotoSignOut": gotoSignOut,
  }
  
})()
