//app/Platform/Data8583.js
define(['Moo'], function(Moo) {

	var Data8583 = new Class({

		initialize : function() {

		},

		convert8583 : function(data, callback) {
			Global.callObjcHandler("convert8583", data, convert8583_callback);
			function convert8583_callback(data) {
				if (data.error == "ERROR") {
					Scene.alert("程序异常，请重新操作或重新启动！", function() {
						if (ConsumptionData.dataForPayment.isExternalOrder) {
							Pay.restart();
						} else {
							Scene.goBack("Home");
						}
					});
					return;
				}
				if (callback != null) {
					callback(data);
				}
			}

		},

		get8583 : function(data, callback) {
			Global.callObjcHandler("get8583", data, get8583_callback);
			function get8583_callback(data) {
				if (data.error == "ERROR") {
					Scene.alert("程序异常，请重新操作或重新启动！", function() {
						if (ConsumptionData.dataForPayment.isExternalOrder) {
							Pay.restart();
						} else {
							Scene.goBack("Home");
						}
					});
					return;
				}
				if (callback != null) {
					callback(data);
				}
			}

		},

		getBalance : function(callback) {
			Global.callObjcHandler("getBalance", null, callback);
		}
	});

	return Data8583;
});
