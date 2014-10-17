;
(function() {
	if (window.DeliveryVocherConsume) {
		return
	}
	var consumeData = {
		"consumePwd": "",
		"num": "",
		"openBrh": "",
		"paymentId": "",
	};

	function resetConsumeData() {
		DeliveryVocherConsume.consumeData = {
			"consumePwd": "",
			"num": "",
			"openBrh": "",
			"paymentId": "",
		};
	}

	function onConfirmNum(data) {
		var msg = JSON.parse(data);
		DeliveryVocherConsume.consumeData.num = msg.num;
		DeliveryVocherConsume.consumeData.openBrh = msg.open_brh;
		DeliveryVocherConsume.consumeData.paymentId = msg.payment_id;

		var nextForm = {
			"inputAccount": "DeliveryVocherConsume.onConfirmPwd",
			"nearfieldAccount": "DeliveryVocherConsume.onConfirmPwd",
		};

		nextForm.btn_swipe = -1;
		nextForm.btn_input = 1;
		nextForm.btn_sound = -1;
		nextForm.btn_qrcode = 0;

		Scene.showScene("DelVoucherId", "请输入提货券号", nextForm);
	}

	function onConfirmPwd(data) {
		var msg = JSON.parse(data);

		// var msg = {
		// 	"field0": "7997581757",
		// };

		DeliveryVocherConsume.consumeData.consumePwd = msg.field0;
		var req = {
			"consumePwd": DeliveryVocherConsume.consumeData.consumePwd,
			"brhId": DeliveryVocherConsume.consumeData.openBrh,
			"payment_id": DeliveryVocherConsume.consumeData.paymentId,
		};
		Net.connect("merchant/voucherQuery", req, callBackQuery);

		function callBackQuery(params) {
			/*
			 response = {
				"body": [{
					"detail": "",
					"price": "999.00      ",
					"consumeEndtime": "20131231000000",
					"promotionName": "【成都欢乐谷年卡】",
					"action": "merchant\/voucherQuery",
					"consumeQty": "300",
					"brief": "测试商品购买无效，不支持退款",
					"consumeStarttime": "20130730000000"
				}],
				"header": {
					"session": "2013121214285071520131225141324202"
				}
			}
			 */
			var startTime = params.consumeStarttime;
			if (null != startTime && startTime.length > 8) {
				startTime = util.formatDateTime(startTime.substring(0, 8));
			};
			var endTime = params.consumeEndtime;
			if (null != endTime && endTime.length > 8) {
				endTime = util.formatDateTime(endTime.substring(0, 8));
			};
			var dateRange = "未知";
			if (null != startTime) {
				dateRange = startTime + "至" + endTime;
			}

			var formData = {
				"voucherId": DeliveryVocherConsume.consumeData.consumePwd,
				"dateRange": dateRange,
				"brief": params.brief,
				"productName": params.promotionName,
			};
			Scene.showScene("DelVoucherInfo", "", formData);
		}
	}

	function onConfirmConsume() {
		var req = {
			"consumeQty": DeliveryVocherConsume.consumeData.num,
			"consumePwd": DeliveryVocherConsume.consumeData.consumePwd,
			"brhId": DeliveryVocherConsume.consumeData.openBrh,
			"payment_id": DeliveryVocherConsume.consumeData.paymentId,
		};
		Net.connect("merchant/voucherConsume", req, callBackConsume);

		function callBackConsume() {
			DeliveryVocherConsume.resetConsumeData();
			Scene.goBack("Home");
		}
	}

	window.DeliveryVocherConsume = {
		"consumeData": consumeData,
		"resetConsumeData": resetConsumeData,
		"onConfirmNum": onConfirmNum,
		"onConfirmPwd": onConfirmPwd,
		"onConfirmConsume": onConfirmConsume,
	}

})();