//app/Common/DataBase.js
define(['Moo'], function(Moo) {

	var DataBase = new Class({
		initialize : function() {

		},

		getAndCheckTransData8583 : function(txnId, callbackfunc) {
			var data8583;
			window.Database.getTransData8583(txnId, checkTransData8583);
			function checkTransData8583(data) {
				data8583 = data;
				if (data.res8583 == null || data.res8583 == "") {
					var req = {
						"txnId" : txnId
					};
					Net.connect("msc/txn/detail/query", req, afterGetDetail, true);
				} else {
					if (callbackfunc) {
						callbackfunc(data8583);
					}
				}
			}

			function afterGetDetail(resData) {
				if ("0" == resData.responseCode) {
					var recordDisplayedList = resData.recordList;
					if (recordDisplayedList[0].txnId == txnId) {
						recordDisplayedList[0].res8583 = recordDisplayedList[0].data;
						window.Database.insertTransData8583(txnId, null, recordDisplayedList[0].res8583);
						if (callbackfunc) {
							callbackfunc(recordDisplayedList[0]);
						} else {
							Scene.alert("ERROR:NO Callback function！");
						}
					}
				} else {
					if (callbackfunc) {
						callbackfunc(data8583);
					}
				}
			}

		},

		insertTransData8583 : function(txnId, req8583, res8583, callbackfunc) {
			var params = {
				"txnId" : txnId,
				"req8583" : req8583,
				"res8583" : res8583
			};
			Global.callObjcHandler("insertTransData8583", params, callbackfunc);
		},

		updateTransData8583 : function(txnId, req8583, res8583, callbackfunc) {
			var params = {
				"txnId" : txnId,
				"req8583" : req8583,
				"res8583" : res8583
			};
			Global.callObjcHandler("updateTransData8583", params, callbackfunc);
		},

		getTransData8583 : function(txnId, callbackfunc) {
			var params = {
				"txnId" : txnId
			};
			Global.callObjcHandler("getTransData8583", params, callbackfunc);
		}
	});

	return DataBase;
});
