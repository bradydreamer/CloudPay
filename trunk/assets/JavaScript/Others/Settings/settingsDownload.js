;
(function() {
	if (window.SettingsDownload) {
		return;
	}
	var _templateList_sale = {};
	var _templateList_void = {};
	var _templateList_refund = {};
	var _settingParams = [];
	var _currentStep, _currentType, _totalSteps;

	var TYPE_reqInfo = 1;
	var TYPE_download_sale = 2;
	var TYPE_download_void = 3;
	var TYPE_download_refund = 4;
	var TYPE_save = 5;
	var TYPE_end = 6;

	function start() {
		_currentType = TYPE_reqInfo;
		_currentStep = 1;
		_totalSteps = 100;
		run();
	}

	function run() {
		switch (_currentType) {
			case TYPE_reqInfo:
				reqInfo();
				break;
			case TYPE_download_sale:
				downloadTemplate(_templateList_sale);
				break;
			case TYPE_download_void:
				downloadTemplate(_templateList_void);
				break;
			case TYPE_download_refund:
				downloadTemplate(_templateList_refund);
				break;
			case TYPE_save:
				save();
				break;
			case TYPE_end:
				end();
				return;
			default:
				break;
		}

		// Scene.alert("_currentStep: " + _currentStep + "\n_currentType" + _currentType);

		setTimeout(function() {
			var propertyList = [{
				name : "loading",
				key : "process",
				value : Math.ceil(_currentStep * 100 / _totalSteps),
			}];
			Scene.setProperty("SettingsDownload", propertyList);
		}, 100);
	}

	function reqInfo() {
		var req = {
			merchId : window.user.merchId,
		};
		Net.asynConnect("merchant/prdtInfoByMerid", req, callBack_reqInfo);

		// var merchSettings = {
		//   "prdtList": merchSettings_temp,
		// };
		// callBack_reqInfo(merchSettings);
	}

	function callBack_reqInfo(params) {
		var prdtList = params.prdtList;
		if (prdtList == null || prdtList.length == 0) {
			Scene.alert("参数列表为空");
			return;
		};

		_templateList_sale = {};
		_templateList_void = {};
		_templateList_refund = {};
		_settingParams = [];

		for (var i = 0; i < prdtList.length; i++) {
			var saleTemplate = prdtList[i]["trans_template"];

			var voidTemplate = prdtList[i]["void_template"];

			var refundTemplate = prdtList[i]["refund_template"];

			_settingParams[i] = {
				"openBrh" : prdtList[i]["open_brh"],
				"paymentId" : prdtList[i]["payment_id"],
				"paymentName" : prdtList[i]["payment_name"],
				"typeName" : prdtList[i]["classes"],
				"typeId" : prdtList[i]["content"],
				"imgName" : prdtList[i]["payment_icon"],
			};

			if (saleTemplate != null) {
				_settingParams[i]["saleTemplate"] = saleTemplate;
				_templateList_sale[saleTemplate] = {
					"paymentId" : _settingParams[i].paymentId,
					"openBrh" : _settingParams[i].openBrh,
					"templateName" : saleTemplate,
				};
			};

			if (voidTemplate != null) {
				_settingParams[i]["voidTemplate"] = voidTemplate;
				_templateList_void[voidTemplate] = {
					"paymentId" : _settingParams[i].paymentId,
					"openBrh" : _settingParams[i].openBrh,
					"templateName" : voidTemplate,

				};
			};

			if (refundTemplate != null) {
				_settingParams[i]["refundTemplate"] = refundTemplate;
				_templateList_refund[refundTemplate] = {
					"paymentId" : _settingParams[i].paymentId,
					"openBrh" : _settingParams[i].openBrh,
					"templateName" : refundTemplate,

				};
			};
		};

		_totalSteps = 1;

		_totalSteps = _totalSteps + objLength(_templateList_sale);
		_totalSteps = _totalSteps + objLength(_templateList_void);
		_totalSteps = _totalSteps + objLength(_templateList_refund);
		_totalSteps = _totalSteps + 4 + 1;

		_currentStep++;
		_currentType++;

		run();
	}

	var _downloasStep = 0;

	function downloadTemplate(templateList) {
		var template;
		var count = 0;
		for (var t in templateList) {
			if (count == _downloasStep) {
				template = templateList[t];
				break;
			};
			count++;
		};
		if (template == null) {
			callBack_dowload();
		} else {
			var req = {
				//"paymentId" : template.paymentId,
				//"openBrh" : template.openBrh,
				"templateName" : template.templateName,
			};
			Net.asynConnect("merchant/payprocQuery", req, callBack_dowload);

			// var name = "js_" + req.templateName.replace(".js", "");
			// callBack_dowload(window[name]);
		}

		function callBack_dowload(params) {
			if (params != null) {
				templateList[params.templateName] = JSON.stringify(params.templateContent);
				_currentStep++;
			};

			_downloasStep++;

			if (_downloasStep >= objLength(templateList)) {
				_currentType++;
				_downloasStep = 0;
			};
			run();
		}

	}

	var _saveStep = 0;

	function save() {
		switch (_saveStep) {
			case 0:
				RMS.save("merchSettings", {
					"settingString" : JSON.stringify(_settingParams),
				});
				break;
			case 1:
				RMS.clear("saleTemplates", function() {
					RMS.save("saleTemplates", _templateList_sale);
				});
				break;
			case 2:
				RMS.clear("voidTemplates", function() {
					RMS.save("voidTemplates", _templateList_void);
				});
				break;
			case 3:
				RMS.clear("refundTemplates", function() {
					RMS.save("refundTemplates", _templateList_refund);
				});
				_saveStep = -1;
				_currentType++;
				break;
			default:
				break;
		}
		_saveStep++;
		_currentStep++;
		setTimeout(run, 300);
	}

	function end() {
		_templateList_sale = null;
		_templateList_void = null;
		_templateList_refund = null;
		_settingParams = null;
		_currentStep = 0;
		_totalSteps = 0;

		Home.needUpdateUI = true;
		window.merchSettings = null;
		window.saleTemplates = null;
		window.voidTemplates = null;
		window.refundTemplates = null;

		Scene.alert("参数下载成功", function() {
			Scene.goBack("Home");
		});
	}

	function objLength(o) {
		var count = 0;
		for (var i in o) {
			count++;
		}
		return count;
	};

	window.SettingsDownload = {
		"start" : start,
	};
})();

var merchSettings_temp = [{
	"open_brh" : "0000000001",
	"payment_id" : "0000",
	"payment_name" : "通联收单",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_allinpay.png",
	"trans_template" : "1_swiper",
}, {
	"open_brh" : "0000000000",
	"payment_id" : "0000",
	"payment_name" : "银联收单",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_cup.png",
	"trans_template" : "1_swiper",
}, {
	"open_brh" : "0000000002",
	"payment_id" : "0000",
	"payment_name" : "广分收单",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_allinpay.png",
	"trans_template" : "1_swiper",
}, {
	"open_brh" : "0229000219",
	"payment_id" : "4567",
	"payment_name" : "情人节鲜花券",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_cp.png",
	"trans_template" : "5_coupon",
}, {
	"open_brh" : "0229000219",
	"payment_id" : "7705",
	"payment_name" : "元宵节团圆卡",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_ec.png",
	"trans_template" : "5_coupon",
}, {
	"open_brh" : "0229000219",
	"payment_id" : "7701",
	"payment_name" : "微会员卡",
	"content" : "消费",
	"classes" : "SALE",
	"payment_icon" : "logo_ec.png",
	"trans_template" : "4_ecard",
}, {
	"open_brh" : "0229000184",
	"payment_id" : "9150",
	"payment_name" : "提货券",
	"content" : "提货",
	"classes" : "DELIVERY_VOUCHER",
	"payment_icon" : "logo_delivery_voucher.png",
}, {
	"open_brh" : "0229000184",
	"payment_id" : "9150",
	"payment_name" : "马到成功礼盒",
	"content" : "提货",
	"classes" : "DELIVERY_VOUCHER",
	"payment_icon" : "logo_delivery_voucher.png",
}, {
	"open_brh" : "0229000184",
	"payment_id" : "9150",
	"payment_name" : "元宵礼盒",
	"content" : "提货",
	"classes" : "DELIVERY_VOUCHER",
	"payment_icon" : "logo_delivery_voucher.png",
}, {
	"open_brh" : "0229000184",
	"payment_id" : "9150",
	"payment_name" : "情人节限量",
	"content" : "提货",
	"classes" : "DELIVERY_VOUCHER",
	"payment_icon" : "logo_delivery_voucher.png",
}, {
	"open_brh" : "0000000000",
	"payment_id" : "0000",
	"payment_name" : "余额",
	"content" : "余额查询",
	"classes" : "BALANCE",
	"payment_icon" : "logo_card.png",
}, {
	"open_brh" : "0000000002",
	"payment_id" : "0000",
	"payment_name" : "广分余额",
	"content" : "余额查询",
	"classes" : "BALANCE",
	"payment_icon" : "logo_card.png",
}];

var js_1_swiper = [{
	"packTag" : "F40_6F20",
}, {
	"packTag" : "F60.6",
}, {
	"methods" : ["10"],
	"desc" : "请输入交易金额",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F04",
}, {
	"methods" : ["00"],
	"desc" : "请刷卡",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F02",
}, {
	"methods" : ["00"],
	"desc" : "请刷卡",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F35",
}, {
	"methods" : ["00"],
	"desc" : "请刷卡",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F36",
}, {
	"methods" : ["30"],
	"desc" : "请输入密码",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F52",
}];
var js_4_ecard = [{
	"packTag" : "F40_6F20",
}, {
	"packTag" : "F60.6",
}, {
	"methods" : ["10"],
	"desc" : "请输入交易金额",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F04",
}, {
	"methods" : ["01", "02", "03"],
	"desc" : "请输入会员账号",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F40_6F21",
}, {
	"methods" : ["99"],
	"packTag" : "F02",
	"packValue" : "9999999999999999",
}, {
	"methods" : ["30"],
	"desc" : "请输入会员密码",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F52",
}];
var js_5_coupon = [{
	"packTag" : "F40_6F20",
}, {
	"packTag" : "F60.6",
}, {
	"methods" : ["10"],
	"desc" : "请输入交易金额",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F04",
}, {
	"methods" : ["03", "01", "02"],
	"desc" : "请输入券号",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F40_6F21",
}, {
	"methods" : ["99"],
	"packTag" : "F02",
	"packValue" : "9999999999999999",
}, {
	"methods" : ["30"],
	"desc" : "请输入密码",
	"matchRegex" : "",
	"inputRegex" : "",
	"packTag" : "F52",
}];
