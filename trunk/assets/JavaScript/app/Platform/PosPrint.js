//app/Platform/PosPrint.js
define(['Moo'], function() {
	var PosPrint = new Class({

		initialize : function() {

		},

		printTrans : function(txnId) {
			window.Database.getAndCheckTransData8583(txnId, gotoPrint);
		},

		gotoPrint : function(data) {
			if (null != data) {
				data.userName = window.user.userName;

				if ("" === ConsumptionData.dataForPayment.paymentId || undefined === ConsumptionData.dataForPayment.paymentId) {
					data.paymentId = window.OrderDetail.paymentId;
				} else {
					data.paymentId = ConsumptionData.dataForPayment.paymentId;
				}
				if ("" === ConsumptionData.dataForPayment.paymentName || undefined === ConsumptionData.dataForPayment.paymentName) {
					data.paymentName = window.OrderDetail.paymentName;
				} else {
					data.paymentName = ConsumptionData.dataForPayment.paymentName;
				}
			}
			if ("" === data.req8583 || null == data.req8583) {
				Scene.alert("交易已经过期");
			}
			Global.callObjcHandler("printTrans", data);
		}
	});

	return posPrint;
});
