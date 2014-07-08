;
(function() {
	if (window.SettingsIndex) {
		return
	}

  var g_transBatchRes
  var logoutTag = false;
  
	function gotoLogout() {

		logoutTag = true;
	  if(window.user.userStatus == null){
	  	Scene.alert("已退出！");
	  	return;
	  }
    //transBatch()
		var req = {      	
		};
		Net.connect("msc/user/logout", req, actionAfterLogout);
		function actionAfterLogout(data){
			
			if(data.responseCode == "0"){
				window.user.init({});
				Scene.alert("退出成功！");
			}else{
				Scene.alert(data.errorMsg);				
			}
		}
	}

	function transBatch(){
		var data = {
      		typeOf8583: "transBatch"
    }
    window.data8583.get8583(data, actionAfterGet)
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

      Net.connect("txn/00", req, actionAfterTransBatch)
  }

	function actionAfterTransBatch(data){
		g_transBatchRes = data
		var params = {
        data8583: data.data
      }
      window.data8583.convert8583(params, actionAfterConvertTransBatchRes)		
	}
	
	function actionAfterConvertTransBatchRes(data){
    if ("00" != data.resCode) {
	    Scene.alert(data.resMessage)
	    return
  	} else {
  		if(logoutTag){
	    	window.user.init({})
			logoutTag = false;
		    Scene.alert("退出成功！");
	  	}else{
	  		Scene.alert("批结算成功！",TransBatch.gotoHome);
	  	}
  	}	
	}
	
	function clearReverseData() {
		window.util.exeActionWithLoginChecked(function() {
			window.RMS.clear("savedTransData", afterClearReverseData)
		})
	}

	function afterClearReverseData() {
		setTimeout(function() {
			Scene.alert("冲正数据已清除")
		}, 300)
	}

	function gotoLogin() {
		window.user.init({})
		window.util.exeActionWithLoginChecked(function() {
		})
	}

	function gotoCreateUser(){
		window.util.showSceneWithLoginChecked("CreateUser", null, null);	
	}
	
	function gotoModifyPwd(){
		window.util.showSceneWithLoginChecked("ModifyPwd", null, null);
	}

	function gotoMerchantInfo() {
		window.util.exeActionWithLoginChecked(getMerchantInfo)
	}

	function getMerchantInfo() {
		RMS.read("merchant", actionAfterGetMerchantInfo);
	}

	function actionAfterGetMerchantInfo(data) {
		var params = {
			merchId : window.user.merchId,
			machineId : window.user.machineId,
			merchName : data.merchName,
		}
		Scene.showScene("MerchantInfo", "", params)
	}
	
	function downloadMerchData() {
		window.util.showSceneWithLoginChecked("SettingsDownload");
	}

	function gotoSetTransId() {
		window.util.showSceneWithLoginChecked("SetTransId");
	}

	function gotoTransBatch(){
		window.util.showSceneWithLoginChecked("TransBatch");
	}

	window.SettingsIndex = {
		"gotoLogin" : gotoLogin,
		"gotoLogout" : gotoLogout,
		"gotoMerchantInfo" : gotoMerchantInfo,
		"clearReverseData" : clearReverseData,
		"downloadMerchData" : downloadMerchData,
		"gotoSetTransId": gotoSetTransId,
		"gotoTransBatch": gotoTransBatch,
		"gotoCreateUser": gotoCreateUser,
		"gotoModifyPwd": gotoModifyPwd,
		"transBatch":	transBatch,
	};

})();
