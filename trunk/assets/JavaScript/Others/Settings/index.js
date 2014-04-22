;
(function() {
	if (window.SettingsIndex) {
		return
	}

  var g_transBatchRes
  
	function gotoLogout() {
		var data = {
      typeOf8583: "transBatch"
    }
	  if(window.user.userStatus == null){
	  	Scene.alert("已签退");
	  	return;
	  }
    window.data8583.get8583(data, actionAfterGet)
	}
	
	function actionAfterGet(data) {
      var req = {      	
      }
      req.data = data.data8583
      Net.connect("merchant/transBatch", req, actionAfterTransBatch)
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
    	window.user.init({})
	    Scene.alert("签退成功")
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

	function gotoSetMerchId() {
		window.user.setMerchIdResult(function() {
			Scene.goBack()
		})
		Scene.showScene("SetMerchId")
	}

	function gotoSetMachineId() {
		window.user.setMachineIdResult(function() {
			Scene.goBack()
		})
		Scene.showScene("SetMachineId")
	}

	function gotoMerchantInfo() {
		window.util.exeActionWithLoginChecked(getMerchantInfo)
	}

	function getMerchantInfo() {
		var req = {
			merchId : window.user.merchId,
		}
		Net.connect("merchant/getInfo", req, actionAfterGetMerchantInfo)
	}

	function actionAfterGetMerchantInfo(data) {
		var params = {
			merchId : window.user.merchId,
			machineId : window.user.machineId,
			merchName : data.merchName,
			merchAccount : data.merchAccount,
		}
		Scene.showScene("MerchantInfo", "", params)
	}
	
	//Get merchant info after login
	function getMerchantInfoAfterLogin() {
		window.util.exeActionWithLoginChecked(getMerchantInfoOnLogin);
	}
	
	//get merchant info from interface
	function getMerchantInfoOnLogin() {
		var req = {
			merchId : window.user.merchId,
		};
		Net.asynConnect("merchant/getInfo", req, actionAfterLoginGetMerchantInfo);
	}
	
	//save merchant info
	function actionAfterLoginGetMerchantInfo(data) {
		var params = {
			merchId : window.user.merchId,
			machineId : window.user.machineId,
			merchName : data.merchName,
			merchAccount : data.merchAccount,
		};
		Scene.alert("JSLOG MERCH NAME:" + data.merchName);
		Scene.alert("JSLOG MERCH Acount:" + data.merchAccount);
		RMS.save("merchant", params);
	}

	function downloadMerchData() {
		window.util.showSceneWithLoginChecked("SettingsDownload");
	}

	function gotoSetTransId() {
		window.util.showSceneWithLoginChecked("SetTransId");
	}

	window.SettingsIndex = {
		"gotoLogin" : gotoLogin,
		"gotoLogout" : gotoLogout,
		"gotoMerchantInfo" : gotoMerchantInfo,
		"getMerchantInfoAfterLogin" : getMerchantInfoAfterLogin,
		"gotoSetMerchId" : gotoSetMerchId,
		"gotoSetMachineId" : gotoSetMachineId,
		"clearReverseData" : clearReverseData,
		"downloadMerchData" : downloadMerchData,
		"gotoSetTransId": gotoSetTransId,
	};

})();
