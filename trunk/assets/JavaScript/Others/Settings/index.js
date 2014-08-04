;
(function() {
	if (window.SettingsIndex) {
		return
	}

	var g_transBatchRes
	var logoutTag = false;
	var currentIndex = 0;
	var merchSettings = {};
	var transTag = {};
	var keyIndex = -1;
	var MISTAG = true;
  
	function gotoLogout(data) {
		var params = JSON.parse(data);
		window.SettingsIndex.existMispos = params.existMispos;
		logoutTag = true;
		if(window.user.userStatus == null){
			Scene.alert("已退出！");
			return;
		}		
		allTransBatch(errorOkprocess);		
	}
	
	function actionLogout(){
		var req = {      	
		};
		Net.connect("msc/user/logout", req, actionAfterLogout);
		function actionAfterLogout(data){
			
			if(data.responseCode == "0"){
				window.user.init({});
				if(!MISTAG){
					logoutTag = false;
					Scene.alert("退出成功！");
				}else{
					var formData = {
						typeId : "LOGOUT"					
					};
					if (window.SettingsIndex.existMispos) {
						
						Scene.showScene("MisposController", "", formData);
					} else {
						batchCallBack();
					}
				}
			}else{
				logoutTag = false;
				Scene.alert(data.errorMsg);				
			}
		}
	}
	
	var afterTransBatchCallback = function(){
		};
		
	function errorOkprocess(){
		Scene.goBack("Home");
	}
	
	
	function allTransBatch(callBack){	
		afterTransBatchCallback = callBack;
		merchSettings = {};
		transTag = {};
		currentIndex = 0;
		keyIndex = -1;
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
			Scene.alert("JSLOG,parseMerchSettings,transTag[keyIndex]=" + transTag[keyIndex]);
			if(transTag[keyIndex] == "" || transTag[keyIndex] == null){
				transBatch(keyIndex);
				transTag[keyIndex] = true;
				Scene.alert("JSLOG,parseMerchSettings,keyIndex=" + keyIndex);
			}else{
				currentIndex++;
				parseMerchSettings();
			}
			
		}else{
			if(logoutTag){
				actionLogout();
		    }else{
		    	if(!MISTAG){
			    	Scene.alert("批结算完成！",afterTransBatchCallback);
		    	}else{
			    	var formData = {
						typeId : "LOGOUT"					
					};
					if (window.SettingsIndex.existMispos) {
						
						Scene.showScene("MisposController", "", formData);
					} else {
						batchCallBack();
					}
		    	}
		    }
		}			
	}	
	
	function batchCallBack() {
		if (logoutTag) {
			logoutTag = false;
			Scene.alert("退出成功！");
		} else {
			Scene.alert("批结算完成！",afterTransBatchCallback);
		}
	}

	function transBatch(keyIndex){
		//通联MISpos方案，这里进行滤掉
		if(keyIndex == "90" || keyIndex == "91"){
			currentIndex++;
			parseMerchSettings();
			return;
		}
		
		ConsumptionData.dataForPayment.brhKeyIndex = keyIndex;
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

      Net.connect("msc/pay/batch/settle", req, actionAfterTransBatch)
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
		    Scene.alert(data.resMessage,TransBatch.gotoHome);
		    return
	  	} else {		
	  		currentIndex++;
	  		parseMerchSettings();
		}	
	}
	
	function clearReverseData() {
		window.util.exeActionWithLoginChecked(function() {
			window.RMS.clear("savedTransData", afterClearReverseData);
		});
	}

	function afterClearReverseData() {
		setTimeout(function() {
			Scene.alert("冲正数据已清除")
		}, 300)
	}

	function gotoLogin() {
		if("0" == window.user.userStatus){
			Scene.alert("已登录，是否重新登录？",reLoginAction,"确定","取消");
			return;
		}else{
			window.util.exeActionWithLoginChecked(function() {
			});
		}
		
		function reLoginAction(params){
			if(params.isPositiveClicked == true){
				window.user.init({});
				window.util.exeActionWithLoginChecked(function() {
				});
			}
		}		
	}

	function gotoSetMerchId() {
		window.util.exeActionWithLoginChecked(function() {
		
			if(window.user.gradeId != "1"){
				Scene.alert("没有权限，请使用主管账户登录！",errorOkprocess);
				return;
			}else{
				window.user.init({});
				var params = {
				       merchId : ""
				};
				RMS.save("merchant",params);
				Scene.alert("重置成功,请重新登录！",window.util.exeActionWithLoginChecked);
			}	
		});
	}
	var req_listMore = {};
	function gotoListUsers(reqDataInfo){
		var req;
		if(null != reqDataInfo && reqDataInfo.reqMoreInfo){
			req = req_listMore;
		}else{		
			req = {
				pageNo : 1,
				pageSize : 20
			};
		}
		req_listMore = req;
		Net.connect("msc/user/page/query", req, afterGetUsersInfo);

		function afterGetUsersInfo(params){
			var pageSize;
			var totalSize;
			var pageNo;
			var totalPages;
			var hasMore;
			if(params.responseCode == "0"){	
				pageSize = req_listMore.pageSize;
				totalSize = params.totalSize == null ? params.userList.length : params.totalSize;
				pageNo = req_listMore.pageNo;
				if (totalSize % pageSize > 0) {
					totalPages = totalSize / pageSize + 1;
				} else {
					totalPages = totalSize / pageSize;
				}

				req_listMore.pageNo = req_listMore.pageNo + 1;
				hasMore = (parseInt(pageNo) < parseInt("" + totalPages));
				var moreParams = {
				  			hasMore : hasMore,
				  			userList : params.userList
				  		};
				
				if (1 == pageNo) {
					Scene.showScene("UsersList", "", moreParams);
				} else {
					var propertyList = [{
						name : "lv_userInfo",
						key : "addList",
						value : moreParams
					}];
					Scene.setProperty("ListUserInfo", propertyList);
				};				
			}else{
				Scene.alert(params.errorMsg,errorOkprocess);
			}

		}
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

	function gotoTransBatch(data){
		var params = JSON.parse(data);
		window.SettingsIndex.existMispos = params.existMispos;
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
		"allTransBatch": allTransBatch,
		"batchCallBack": batchCallBack,
		"gotoSetMerchId": gotoSetMerchId,
		"gotoListUsers": gotoListUsers,
	};

})();
