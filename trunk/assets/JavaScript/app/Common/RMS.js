//app/Common/RMS.js
define(['Moo', 'Global'], function(Moo, Global) {
	var RMS = new Class({

		initialize : function() {

		},

		readLocal : function(key, callbackfunc) {
			var params = {
				"key" : key
			};
			Global.callObjcHandler("readLocal", params, callbackfunc);
		},

		readLocalBatch : function(callbackfunc) {

			Global.callObjcHandler("readLocalBatch", null, callbackfunc);
		},

		saveLocal : function(key, value) {
			var params = {
				"key" : key,
				"value" : value
			};
			Global.callObjcHandler("saveLocal", params);
		},

		clearLocal : function(key, callbackfunc) {

			var params = {
				"key" : key
			};
			Global.callObjcHandler("clearLocal", params, callbackfunc);
		},

		rmBatchByTxnId : function(data) {

			Global.callObjcHandler("rmBatchTask", data);
		}
	});

	return RMS;

});
