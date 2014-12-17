function External() {};

var transType_Consume = 1021;

External.startReverse = function(data) {
	window.ConsumptionData.resetConsumptionData();
	ConsumptionData.dataForPayment.isExternalOrder = true;
	window.util.exeActionWithLoginChecked(function() {
		External.startReverseAfterLogin(data);
	}, true);
};
External.startReverseAfterLogin = function(data) {	
	var params = JSON.parse(data);
	var req = {
			txnId : params.txnId
		};
	ConsumptionData.dataForPayment.orderNo = params.orderNo;
	
	Net.connect("msc/txn/detail/query", req, handleResFromReqRecord);
	
	function handleResFromReqRecord(resData) {
		var recordList = resData.recordList;
		if (recordList == null || recordList.length == 0) {
			Scene.alert("101");
			return;
		}

		var reverseData = recordList[0];
		var transTime = reverseData.transTime;
		reverseData.formatedTransDate = transTime.substring(0,4) + "," + transTime.substring(4,6) + "," + transTime.substring(6,8);
				
		// window.OrderDetail.onCancel(JSON.stringify(reverseData));
		handleRecordData(reverseData);
		// reverseData.confirm = "window.External.goBack";
		reverseData.confirm = "window.Pay.restart";
		reverseData.isExternalOrder = ConsumptionData.dataForPayment.isExternalOrder;
		Scene.showScene("OrderDetail", "", reverseData);
	}
	
	function handleRecordData(params) {

		var transTime = ""+params.transTime;	
		var tDate = transTime.substring(0, 8);
		var tTime = transTime.substring(8);

		tTime = tTime.substring(0, 2) + ":" + tTime.substring(2, 4) + ":" + tTime.substring(4);

		params.tDate = tDate;
		params.tTime = tTime;
		params.transTime = util.formatDateTime(transTime);
		params.oriTransTime = transTime;
		params.transAmount = util.formatAmountStr(params.transAmount);

		var cancelEnable = false;

		params.payTypeDesc = "" + params.paymentName;
		params.cancelEnable = params.cancelEnable;

		params.transTypeDesc = getTransTypeDesc(params.transType);
		params.orderStateDesc = getOrderStateDesc(params.orderState);

	}
	
	function getTransTypeDesc(transType) {
		// 交易类型
		// 1021 	消费
		// 3021 	消费撤销
		// 3051 	退货
		// 1011 	预授权
		// 3011 	预授权撤销
		// 1031 	预授权完成联机
		// 3031 	预授权完成联机撤销
		// 1091 	预授权完成离线

		var transTypeDesc = "";
		if (transType == "1021") {
			transTypeDesc = "102";
		} else if (transType == "3021") {
			transTypeDesc = "103";
		} else if (transType == "3051") {
			transTypeDesc = "104";
		} else if (transType == "1011") {
			transTypeDesc = "105";
		} else if (transType == "3011") {
			transTypeDesc = "106";
		} else if (transType == "1031") {
			transTypeDesc = "107";
		} else if (transType == "3031") {
			transTypeDesc = "108";
		} else if (transType == "1091") {
			transTypeDesc = "109";
		}
		;
		return transTypeDesc;
	}

	function getOrderStateDesc(orderState) {
		// 		订单状态
		// 0	成功
		// 1	失败
		// 2	已冲正
		// 3	已撤销
		// 4	预授权已完成
		// 5 	未知
		// 6    撤销中
		// 9	超时

		var orderStateDesc = "";
		if (orderState == "0") {
			orderStateDesc = "110";
		} else if (orderState == "1") {
			orderStateDesc = "111";
		} else if (orderState == "2") {
			orderStateDesc = "112";
		} else if (orderState == "3") {
			orderStateDesc = "113";
		} else if (orderState == "4") {
			orderStateDesc = "115";
		} else if (orderState == "5") {
			orderStateDesc = "116";
		} else if (orderState == "6") {
		    orderStateDesc = "172";
		} else if (orderState == "9") {
			orderStateDesc = "117";
		}
		;
		return orderStateDesc;
	}
};

External.goBack = function(data) {
	var params = JSON.parse(data);
	Scene.goBack("first", params);
};
External.onLogin = function() {
	window.util.exeActionWithLoginChecked(function() {
		var formData = {
			"userStatus": window.user.userStatus,
		};
		Scene.goBack("first", formData);
	}, true);
};

External.onLogout = function(data) {
	window.user.init({});
	var formData = {
		"userStatus": window.user.userStatus,
	};
	return formData;
};

External.onPay = function(data) {
	window.ConsumptionData.resetConsumptionData();

	var params = {};
	var transAmount, openBrh, paymentId, packageName, orderNo, orderDesc;
	if (data != null) {
		params = JSON.parse(data);
		transAmount = params.transAmount;
		openBrh = params.openBrh;
		paymentId = params.paymentId;
		packageName = params.packageName;
		orderNo = params.orderNo;
		orderDesc = params.orderDesc;
	};
	ConsumptionData.dataForPayment.isExternalOrder = true;
	ConsumptionData.dataForPayment.transAmount = transAmount;
	//save more info to consumption data
	ConsumptionData.dataForPayment.packageName = packageName;
	ConsumptionData.dataForPayment.orderNo = orderNo;
	ConsumptionData.dataForPayment.orderDesc = orderDesc;
	params.isExternalOrder = true;

	if (params.openBrh == null || params.openBrh == "") {
		Scene.showScene("Home", "", params);
	} else {
		updateTransInfo();
	};

	function updateTransInfo() {
		window.RMS.read("merchSettings", initTransInfo);
	}

	function initTransInfo(data) {
		var settingString = data.settingString;
		if (settingString == null || settingString.length == 0) {
			window.util.showSceneWithLoginChecked("SettingsIndex");
			return;
		};
		var merchSettings = JSON.parse(settingString);
		if (merchSettings == null || merchSettings.length == 0) {
			return;
		};

		transInfo = null;
		for (var i = 0; i < merchSettings.length; i++) {
			var merchSetting = merchSettings[i];
			if (merchSetting.openBrh == openBrh && merchSetting.paymentId == paymentId) {
				transInfo = merchSetting;
				break;
			};
		};
		if (transInfo != null) {
			var payKeyIndex = transInfo.brhKeyIndex;
		
			if (payKeyIndex === "90") {
				
				var params = {
					typeId : transInfo.typeId,
					payKeyIndex : transInfo.brhKeyIndex,
					paymentId : transInfo.paymentId,
					misc : transInfo.misc
				};
				window.util.showMisposWithLoginChecked(JSON.stringify(params));
			} else {
				
				window.RMS.read("templateList", function(data) {
					window.payTemplates = data;
					window.PayMethod.confirmMethod(transInfo, window.payTemplates[transInfo[transType_Consume]]);
				});
				// window.PayMethod.confirmMethod(transInfo);
			}
			
		} else {
			Scene.showScene("Home", "", params);
		};
	}

};

External.getBalance = function(data) {
	window.ConsumptionData.resetConsumptionData();

	var params = {};
	var openBrh, paymentId;
	if (data != null) {
		params = JSON.parse(data);
		openBrh = params.openBrh;
		paymentId = params.paymentId;
	};
	ConsumptionData.dataForPayment.isExternalOrder = true;
	params.isExternalOrder = true;

	if (params.openBrh == null || params.openBrh == "") {
		Scene.showScene("Home", "", params);
	} else {
		updateTransInfo();
	};

	function updateTransInfo() {
		window.RMS.read("merchSettings", initTransInfo);
	}

	function initTransInfo(data) {
		var settingString = data.settingString;
		if (settingString == null || settingString.length == 0) {
			window.util.showSceneWithLoginChecked("SettingsIndex");
			return;
		};
		var merchSettings = JSON.parse(settingString);
		if (merchSettings == null || merchSettings.length == 0) {
			return;
		};

		transInfo = null;
		for (var i = 0; i < merchSettings.length; i++) {
			var merchSetting = merchSettings[i];
			if (merchSetting.openBrh == openBrh && merchSetting.paymentId == paymentId) {
				transInfo = merchSetting;
				break;
			};
		};
		if (transInfo != null) {
			var payKeyIndex = transInfo.brhKeyIndex;
		
			if (payKeyIndex === "90") {
				
				var params = {
					typeId : transInfo.typeId,
					payKeyIndex : transInfo.brhKeyIndex,
					paymentId : transInfo.paymentId,
					misc : transInfo.misc
				};
				window.util.showMisposWithLoginChecked(JSON.stringify(params));
			} else {
				
				window.RMS.read("templateList", function(data) {
					window.payTemplates = data;
					window.PayMethod.confirmMethod(transInfo, window.payTemplates[transInfo[transType_Consume]]);
				});
			}
			
		} else {
			Scene.showScene("Home", "", params);
		};
	}

};