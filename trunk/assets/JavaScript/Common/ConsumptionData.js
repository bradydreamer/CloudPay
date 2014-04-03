;
(function() {
	if (window.ConsumptionData) {
		return;
	};

	var dataForPayment;

	var dataForCancellingOrder;

	var dataForBalance;
	
	var dataForMultiPay;
	

	function resetConsumptionData() {
		ConsumptionData.dataForPayment = {
			"typeOf8583": "pay",
			"openBrh": "",
			"paymentId": "",
			"flowList": [],
			"step": 0,
		};
	}

	function resetDataForCancellingOrder() {
		ConsumptionData.dataForCancellingOrder = {
			"rrn": "",
			"openBrh": "",
			"paymentId": "",
			"transData8583": "",
			"req8583": "",
			"res8583": "",
			"step": 0,
		};
	}

	function resetDataForBalance() {
		ConsumptionData.dataForBalance = {
			"typeOf8583": "chaxunyue",
			"track2": "",
			"track3": "",
			"balancePwd": "",
			"cardID": "",
			"openBrh": "",
			"paymentId": "",
		};
	}
	
	function resetMultiData() {		
		ConsumptionData.dataForMultiPay = {
			"orderList": [],
			"flowList" : [{
				"methods" : ["10"],
				"desc" : "请输入交易总金额",
				"matchRegex" : "",
				"inputRegex" : "",
				"packTag" : "totalAmount",
			}],
			"step" : 0,
			"completed": false,
		}; 
	}

	window.ConsumptionData = {
		"dataForPayment": dataForPayment,
		"resetConsumptionData": resetConsumptionData,
		"dataForCancellingOrder": dataForCancellingOrder,
		"resetDataForCancellingOrder": resetDataForCancellingOrder,
		"dataForBalance": dataForBalance,
		"resetDataForBalance": resetDataForBalance,
		"dataForMultiPay": dataForMultiPay,
		"resetMultiData": resetMultiData,
	};

	resetConsumptionData();
	resetDataForCancellingOrder();
	resetDataForBalance();
	resetMultiData();
})();