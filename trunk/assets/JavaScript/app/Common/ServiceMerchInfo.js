//app/Common/ServiceMerchInfo.js
define(['Moo', 'Global'], function(Moo, Global) {

	var ServiceMerchInfo = new Class({

		initialize : function() {

		},

		getInfo : function(callbackfunc) {
			var params = {
				"action" : "get"
			};
			Global.callObjcHandler("ServiceSecureInfo", params, callbackfunc);
		},

		setInfo : function(value, callbackfunc) {
			var params = {
				"action" : "set",
				"value" : value
			};
			Global.callObjcHandler("ServiceSecureInfo", params, callbackfunc);
		},

		setMerchInfo : function(value, callbackfunc) {
			var params = {
				"action" : "set",
				"value" : value
			};
			Global.callObjcHandler("ServiceMerchInfo", params, callbackfunc);
		}
	});

	return ServiceMerchInfo;
});
