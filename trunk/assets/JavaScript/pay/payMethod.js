;
(function() {
	if (window.PayMethod) {
		return;
	};

	var transType_Consume = "1021";
	var transType_ConsumeCancel = "3021";
	var transType_Refund = "3051";
	var transType_PreAuth = "1011";
	var transType_preAuthComplete= "1031";
	var transType_preAuthSettlement = "1091";
	var transType_preAuthCancel = "3011";
	var transType_preAuthCompleteCancel = "3031";

	function onConfirmMethod(data) {
		var payType;
		var params = JSON.parse(data);
		if(params.error != null || params.error != undefined){
			Scene.alert(params.error);
			return;
		}
		var product = JSON.parse(params.tag);
		if (product == null) {
			Scene.alert(data);
			return;
		};

		if(ConsumptionData.dataForPayment.isExternalOrder != true){			
			ConsumptionData.resetConsumptionData();
		}else{
			var transAmount = ConsumptionData.dataForPayment.transAmount;
			var couponAmount = ConsumptionData.dataForPayment.couponAmount;
			//cache couponAmount mod by Teddy on 12th August --start
			ConsumptionData.resetConsumptionData();
			ConsumptionData.dataForPayment.isExternalOrder = true;
			ConsumptionData.dataForPayment.couponAmount = couponAmount;
			//cache couponAmount mod by Teddy on 12th August --end
			
			if(!ConsumptionData.isMultiPay){
				ConsumptionData.dataForPayment.transAmount = transAmount;
			}
		}
		
		if(product.typeId == "SALE"){
			payType = transType_Consume;
		}else if(product.typeId == "PREPAID"){
			payType = transType_PreAuth;
		}		
		
		if (product[payType] != null && product[payType].length != 0) {
			if (window.payTemplates == null) {
				window.RMS.read("templateList", function(data) {
					window.payTemplates = data;
					confirmMethod(product, window.payTemplates[product[payType]]);
				});

				
			} else {
				confirmMethod(product, window.payTemplates[product[payType]]);
			}
		}else{
			confirmMethod(product);
		}

	}

	function confirmMethod(product, payList) {
		if(payList != null && typeof(payList) == "string"){
			payList = JSON.parse(payList);
			payList = JSON.parse(payList);
		}
		
		RMS.read("merchant", afterGetMerchData);
		function afterGetMerchData(data){
			window.user.merchId = data.merchId;
			window.user.machineId = data.machineId;
			var merchId = window.user.merchId;
			var iposId = window.user.machineId;
			var open_brh = product.openBrh;
			var open_brh_name = product.openBrhName;
			var brhMchtId = product.brhMchtId;
			var brhTermId = product.brhTermId;
			var payment_id = product.paymentId;		
			var brhKeyIndex = product.brhKeyIndex;
			var payment_name = product.paymentName;
			var typeId = product.typeId;
			var printType = product.printType;
			var typeName = product.typeName;

			if (typeId == util.getTransType("DELIVERY_VOUCHER")) {
				var formData = {
					"open_brh": open_brh,
					"payment_id": payment_id,
				};
				formData.openBrhName = open_brh_name;
				formData.brhMchtId = brhMchtId;
				formData.brhTermId = brhTermId;
				formData.merchId = merchId;
				formData.iposId = iposId;
				formData.printType = printType;
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
				formData.openBrhName = open_brh_name;
				formData.brhMchtId = brhMchtId;
				formData.brhTermId = brhTermId;
				formData.merchId = merchId;
				formData.iposId = iposId;
				formData.printType = printType;
				//indicate operation type, now is balance. --start add by Teddy on 20th June 
				formData.operationType = "BALANCE";
				//indicate operation type, now is balance. --end add by Teddy on 20th June 
				
				//add brhKeyIndex on balance --start by Teddy on 26th June
				formData.brhKeyIndex = brhKeyIndex;
				ConsumptionData.dataForPayment.brhKeyIndex = brhKeyIndex;
				//add brhKeyIndex on balance --end by Teddy on 26th June
				window.util.showSceneWithLoginChecked("PayAccount", formData, product.typeName);
				return;
			} else if(typeId == util.getTransType("PREPAID")){
				ConsumptionData.dataForPayment.typeOf8583 = "preAuth";
				ConsumptionData.dataForPayment.merchId = merchId;
				ConsumptionData.dataForPayment.iposId = iposId;
				ConsumptionData.dataForPayment.openBrh = open_brh;
				ConsumptionData.dataForPayment.openBrhName = open_brh_name;
				ConsumptionData.dataForPayment.brhMchtId = brhMchtId;
				ConsumptionData.dataForPayment.brhTermId = brhTermId;
				ConsumptionData.dataForPayment.printType = printType;
				ConsumptionData.dataForPayment.paymentId = payment_id;
				ConsumptionData.dataForPayment.paymentName = payment_name;
				ConsumptionData.dataForPayment.flowList = payList;
				ConsumptionData.dataForPayment.brhKeyIndex = brhKeyIndex;
				ConsumptionData.dataForPayment.typeName = typeName;
				ConsumptionData.dataForPayment.step = 0;
				Pay.gotoAuthFlow();
				return;

			}else if (typeId != util.getTransType("SALE")) {
				Scene.alert(product);
				return;
			};
			ConsumptionData.dataForPayment.merchId = merchId;
			ConsumptionData.dataForPayment.iposId = iposId;
			ConsumptionData.dataForPayment.openBrh = open_brh;
			ConsumptionData.dataForPayment.openBrhName = open_brh_name;
			ConsumptionData.dataForPayment.brhMchtId = brhMchtId;
			ConsumptionData.dataForPayment.brhTermId = brhTermId;
			ConsumptionData.dataForPayment.printType = printType;
			ConsumptionData.dataForPayment.paymentId = payment_id;
			ConsumptionData.dataForPayment.paymentName = payment_name;
			ConsumptionData.dataForPayment.flowList = payList;
			ConsumptionData.dataForPayment.brhKeyIndex = brhKeyIndex;
			ConsumptionData.dataForPayment.typeName = typeName;
			ConsumptionData.dataForPayment.step = 0;
			Pay.gotoPayFlow();
		}
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