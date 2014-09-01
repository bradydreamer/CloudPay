//app/pay/MultiPay.js
define(['Moo'], function(Moo) {
	var MultiPay = new Class({

		initialize : function() {

		},

		onClickRecord : function(data) {
			Scene.goBack("MultiPayRecord");
		},

		onClickClose : function() {
			Scene.goBack("MultiPayRecord");
		},

		onClickPay : function() {

			if (ConsumptionData.dataForPayment.isExternalOrder == true) {
				ConsumptionData.resetConsumptionData();
				ConsumptionData.dataForPayment.isExternalOrder = true;
			}
			Scene.showScene("MultiPayIndex");
		},

		onClickComplete : function() {
			ConsumptionData.isMultiPay = false;
			ConsumptionData.dataForMultiPay.completed = true;
			Pay.restart();
		},

		resumeMultiPayTag : function() {

			ConsumptionData.isMultiPay = true;
		}
	});

	return MultiPay;
});
