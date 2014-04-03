;
(function() {
	if (window.PayMethod) {
		return;
	};

	function onConfirmMethod(data) {
		var params = JSON.parse(data);
		var product = JSON.parse(params.tag);
		if (product == null) {
			Scene.alert(data);
			return;
		};

		if(ConsumptionData.dataForPayment.isExternalOrder != true){			
			ConsumptionData.resetConsumptionData();
		}
		
		if (product.saleTemplate != null && product.saleTemplate.length != 0) {
			if (window.saleTemplates == null) {
				window.RMS.read("saleTemplates", function(data) {
					window.saleTemplates = data;
					confirmMethod(product, window.saleTemplates[product.saleTemplate]);
				});
			} else {
				confirmMethod(product, window.saleTemplates[product.saleTemplate]);
			}
		}else{
			confirmMethod(product);
		}

	}

	function confirmMethod(product, payList) {
		if(payList != null && typeof(payList) == "string"){
			payList = JSON.parse(payList);
		}
		// "openBrh": "0229000184",
		// 	"paymentId": "9150",
		// 	"paymentName": "通联刷卡",
		// 	"typeName": "消费",
		// 	"typeId": "SALE",
		// 	"imgName": "logo_card.png",

		var open_brh = product.openBrh;
		var payment_id = product.paymentId;
		
		// open_brh = "0000000003";
		// payment_id = "0000";
// 		
		var payment_name = product.paymentName;

		var typeId = product.typeId;

		if (typeId == util.getTransType("DELIVERY_VOUCHER")) {
			var formData = {
				"open_brh": open_brh,
				"payment_id": payment_id,
			};
			window.util.showSceneWithLoginChecked("InputDelVoucherNum", formData, product.typeName);
			return;
		} else if (typeId == util.getTransType("BALANCE")) {
			ConsumptionData.resetDataForBalance();
			ConsumptionData.dataForBalance.openBrh = open_brh;
			ConsumptionData.dataForBalance.paymentId = payment_id;

			var formData = {
				"btn_swipe": 1,
				"swipeCard": "PayMethod.gotoBalanceResult",
			};

			window.util.showSceneWithLoginChecked("PayAccount", formData, product.typeName);
			return;
		} else if (typeId != util.getTransType("SALE")) {
			Scene.alert(product);
			return;
		};

		ConsumptionData.dataForPayment.openBrh = open_brh;
		ConsumptionData.dataForPayment.paymentId = payment_id;
		ConsumptionData.dataForPayment.paymentName = payment_name;
		ConsumptionData.dataForPayment.flowList = payList;
		ConsumptionData.dataForPayment.step = 0;
		Pay.gotoPayFlow();
	};

	function gotoBalanceResult(data) {
		var params = JSON.parse(data);

		ConsumptionData.dataForBalance.track2 = params.track2;
		ConsumptionData.dataForBalance.track3 = params.track3;
		ConsumptionData.dataForBalance.cardID = params.cardID;

		params.actionPurpose = "Balance";
		Scene.showScene("PinPad", "", params);
	}

	window.PayMethod = {
		"onConfirmMethod": onConfirmMethod,
		"confirmMethod": confirmMethod,
		"gotoBalanceResult": gotoBalanceResult,
	};
})();

PayMethod.productList = {};