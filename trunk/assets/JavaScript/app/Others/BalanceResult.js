//app/Others/BalanceResult.js
define(['Moo'], function(Moo) {
	var BalanceResult = new Class({
		initialize : function() {

		},

		reqBalance : function(data) {
			var msg = JSON.parse(data);
			var req = {
				"data" : msg.data8583,
				"paymentId" : msg.paymentId,
				"transType" : msg.transType,
				"batchNo" : msg.batchNo,
				"traceNo" : msg.traceNo,
				"transTime" : msg.transTime,
				"cardNo" : msg.cardNo,
				"transAmount" : msg.transAmount,
				"oriTxnId" : msg.oriTxnId,
				"oriBatchNo" : msg.oriBatchNo,
				"oriTraceNo" : msg.oriTraceNo,
				"oriTransTime" : msg.oriTransTime,
			};
			Net.connect("msc/balance", req, actionAfterReqBalance, true);
		},

		actionAfterReqBalance : function(data) {
			if (data.responseCode == "0") {
				g_loginRes = data
				var params = {
					data8583 : data.data
				};
				window.data8583.convert8583(params, afterConvertBalance8583);
			} else {
				var DataErrorMsg = data.errorMsg;
				if (null == DataErrorMsg || "" == DataErrorMsg) {
					DataErrorMsg = "系统异常";
				}
				Scene.alert(DataErrorMsg, goBackToOthers);
			}
		},

		afterConvertBalance8583 : function(data) {
			if ("00" != data.resCode) {
				Scene.alert(data.resMessage, goBackToOthers);
			} else {
				window.data8583.getBalance(showBalance);
			}
		},

		showBalance : function(data) {
			var propertyList = [{
				name : "tv_title_balance",
				key : "hidden",
				value : false
			}, {
				name : "tv_balance",
				key : "text",
				value : window.util.formatAmountStr("" + data.balance)
			}];
			Scene.setProperty("BalanceResult", propertyList);
		},

		goBackToOthers : function() {
			Scene.goBack("Home");
		}
	});
	
	return BalanceResult;
});
