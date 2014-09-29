//app/Others/Settings/SignIn.js
define(['Moo'], function(Moo) {

	var currentIndex = 0;
	var keyIndex = -1;
	var signTag = {};
	var merchSettings = {};

	var SignIn = new Class({
		initialize : function() {

		},

		gotoSignIn : function() {
			var data = {
				typeOf8583 : "signin",
				paymentId : ConsumptionData.dataForPayment.paymentId,
				brhKeyIndex : ConsumptionData.dataForPayment.brhKeyIndex
			};
			window.data8583.get8583(data, actionAfterGetMsg);

		},

		actionAfterGetMsg : function(params) {
			var req = {
				"data" : params.data8583,
				"paymentId" : params.paymentId,
				"transType" : params.transType,
				"batchNo" : params.batchNo,
				"traceNo" : params.traceNo,
				"transTime" : params.transTime,
				"cardNo" : params.cardNo,
				"transAmount" : params.transAmount,
				"oriTxnId" : params.oriTxnId,
				"oriBatchNo" : params.oriBatchNo,
				"oriTraceNo" : params.oriTraceNo,
				"oriTransTime" : params.oriTransTime
			};
			Net.connect("msc/pay/signin", req, actionAfterSignIn);
		},

		actionAfterSignIn : function(params) {
			if ("0" == params.responseCode) {
				var convertData = {
					"data8583" : params.data,
					"brhKeyIndex" : ConsumptionData.dataForPayment.brhKeyIndex
				};
				ConsumptionData.dataForPayment.res8583 = params.data;
				ConsumptionData.dataForPayment.txnId = params.txnId;
				window.data8583.convert8583(convertData, afterConvertMsg);
			} else {
				Scene.alert(params.errorMsg, errOKProcess);
			}
		},

		errOKProcess : function() {
			if (ConsumptionData.dataForPayment.isExternalOrder) {
				Pay.restart();
			} else {
				Scene.goBack("Home");
			}
		},

		afterConvertMsg : function(params) {
			if ("00" == params.resCode) {
				var req = {
					"txnId" : ConsumptionData.dataForPayment.txnId,
					"resCode" : params.resCode,
					"resMsg" : params.resMessage,
					"refNo" : params.rrn,
					"authNo" : params.authNo,
					"issuerId" : params.issuerId,
					"dateExpr" : params.dateExpr,
					"stlmDate" : params.stlmDate
				};
				var brhKeyIndex = ConsumptionData.dataForPayment.brhKeyIndex;
				var _params = {
					"signature" : true
				};
				RMS.save(brhKeyIndex, _params);
				actionAfterSet();
				Net.asynConnect("msc/txn/update", req, afterBackupInfo);
			} else {
				Scene.alert(params.resMessage, errOKProcess);
			}
		},

		afterBackupInfo : function(data) {
			if ("0" == data.responseCode) {
				return;
			} else {
				Scene.alert(data.errorMsg);
			}
		},

		actionAfterSet : function() {
			window.user.afterSignInAction();
		},

		gotoSignOut : function() {
			currentIndex = 0;
			keyIndex = -1;
			signTag = {};
			merchSettings = {};
			if (window.merchSettings == null) {
				window.RMS.read("merchSettings", afterGetTransInfo);
			} else {
				afterGetTransInfo(window.merchSettings);
			}

			function afterGetTransInfo(data) {
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

		},
		parseMerchSettings : function() {
			if (currentIndex < merchSettings.length) {
				keyIndex = merchSettings[currentIndex].brhKeyIndex;
				Scene.alert("JSLOG,parseMerchSettings,signTag[keyIndex]=" + signTag[keyIndex]);
				if (signTag[keyIndex] == "" || signTag[keyIndex] == null) {
					signOut(keyIndex);
					signTag[keyIndex] = true;
					Scene.alert("JSLOG,parseMerchSettings,keyIndex=" + keyIndex);
				} else {
					currentIndex++;
					parseMerchSettings();
				}
			} else {
				Scene.alert("签退完成！", errOKProcess);
			}
		},

		signOut : function(keyIndex) {
			//通联MISpos方案，这里进行滤掉
			if (keyIndex == "90" || keyIndex == "91") {
				currentIndex++;
				parseMerchSettings();
				return;
			}
			ConsumptionData.dataForPayment.brhKeyIndex = keyIndex;
			var data = {
				typeOf8583 : "signout"
			};
			window.data8583.get8583(data, actionAfterGet);
		},
		actionAfterGet : function(params) {
			var req = {
				"data" : params.data8583,
				"paymentId" : params.paymentId,
				"transType" : params.transType,
				"batchNo" : params.batchNo,
				"traceNo" : params.traceNo,
				"transTime" : params.transTime,
				"cardNo" : params.cardNo,
				"transAmount" : params.transAmount,
				"oriTxnId" : params.oriTxnId,
				"oriBatchNo" : params.oriBatchNo,
				"oriTraceNo" : params.oriTraceNo,
				"oriTransTime" : params.oriTransTime
			};

			Net.connect("msc/pay/signout", req, actionAfterSignout);
		},

		actionAfterSignout : function(data) {
			var params = {
				data8583 : data.data
			};
			window.data8583.convert8583(params, actionAfterConvertSignoutRes);
		},

		actionAfterConvertSignoutRes : function(data) {
			if ("00" != data.resCode) {
				Scene.alert(data.resMessage, errOKProcess);
				return;
			} else {
				var _params = {
					"signature" : false
				};
				RMS.save(keyIndex, _params);
				currentIndex++;
				parseMerchSettings();
			}
		}
	});

	return SignIn;
});
