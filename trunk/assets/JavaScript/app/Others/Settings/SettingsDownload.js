//app/Others/Settings/SettingsDownload.js
define(['Moo'], function() {
	var _templateList = {};
	var _paymentInfo = {};
	var _settingParams = [];
	var _prdtListLength = 0;
	var _currentStep, _currentType, _totalSteps;

	var TYPE_reqInfo = 1;
	var TYPE_download_templates = 2;
	var TYPE_download_merchInfo = 3;
	var TYPE_save = 4;
	var TYPE_end = 5;

	var _downloasStep = 0;
	var _saveStep = 0;

	var SettingsDownload = new Class({
		initialize : function() {

		},

		start : function() {
			_currentType = TYPE_reqInfo;
			_currentStep = 1;
			_totalSteps = 100;
			run();
		},

		run : function() {
			switch (_currentType) {
				case TYPE_reqInfo:
					reqInfo();
					break;
				case TYPE_download_templates:
					downloadTemplates(_templateList);
					break;
				case TYPE_download_merchInfo:
					downloadMerchantInfo();
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

			setTimeout(function() {
				var propertyList = [{
					name : "loading",
					key : "process",
					value : Math.ceil(_currentStep * 100 / _totalSteps)
				}];
				Scene.setProperty("SettingsDownload", propertyList);
			}, 100);
		},

		reqInfo : function() {
			var req = {
				merchId : window.user.merchId
			};
			//Net.asynConnect("merchant/prdtInfoByMerid", req, callBack_reqInfo);msc/payment/info/query
			//Net.asynConnect("merchant/paymentQuery", req, callBack_reqInfo);
			Net.asynConnect("msc/payment/info/query", req, callBack_reqInfo);
		},

		callBack_reqInfo : function(params) {
			var prdtList = params.prdtList;

			if (prdtList == null || prdtList.length == 0) {
				Scene.alert("参数列表为空");
				return;
			};

			_templateList = {};
			_paymentInfo = {};
			_settingParams = [];
			_prdtListLength = prdtList.length;
			for (var i = 0; i < prdtList.length; i++) {
				_settingParams[i] = {
					"mch_no" : prdtList[i]["mcht_no"],
					"paymentId" : prdtList[i]["payment_id"],
					"paymentName" : prdtList[i]["payment_name"],
					"openBrh" : prdtList[i]["open_brh"],
					"openBrhName" : prdtList[i]["open_brh_name"],
					"brhKeyIndex" : prdtList[i]["brh_key_index"],
					"brhMsgType" : prdtList[i]["brh_msg_type"],
					"brhMchtId" : prdtList[i]["brh_mcht_cd"],
					"brhTermId" : prdtList[i]["brh_term_id"],
					"brhMchtMcc" : prdtList[i]["brh_mcht_mcc"],
					"prdtNo" : prdtList[i]["prdt_no"],
					"prdtTitle" : prdtList[i]["prdt_title"],
					"prdtDesc" : prdtList[i]["prdt_desc"],
					"prdtType" : prdtList[i]["prdt_type"],
					"imgName" : prdtList[i]["payment_icon"],
					"typeName" : prdtList[i]["classes"],
					"typeId" : prdtList[i]["content"],
					"printType" : prdtList[i]["print_type"],
					"misc" : prdtList[i]["misc"]
				};

				var templates = prdtList[i].templates;
				for (var j = 0; j < templates.length; j++) {
					var transType = templates[j]["transType"];
					_settingParams[i][transType] = templates[j]["templateName"];
					_templateList[templates[j]["templateName"]] = {
						"paymentId" : _settingParams[i].paymentId,
						"openBrh" : _settingParams[i].openBrh,
						"templateName" : templates[j]["templateName"]
					};
				}

				_paymentInfo[prdtList[i]["payment_id"]] = JSON.stringify({
					"paymentId" : prdtList[i]["payment_id"],
					"paymentName" : prdtList[i]["payment_name"],
					"openBrh" : prdtList[i]["open_brh"],
					"openBrhName" : prdtList[i]["open_brh_name"],
					"brhMchtId" : prdtList[i]["brh_mcht_cd"],
					"brhTermId" : prdtList[i]["brh_term_id"],
					"prdtNo" : prdtList[i]["prdt_no"],
					"printType" : prdtList[i]["print_type"],
					"misc" : prdtList[i]["misc"]
				});

				var indexParams = {
					"signature" : false,
					"batchId" : 0,
					"transId" : 0
				};
				RMS.save(_settingParams[i].brhKeyIndex, indexParams);
			};

			_totalSteps = 1;
			_totalSteps = _totalSteps + objLength(_templateList) + 1 + 4;

			_currentStep++;
			_currentType++;

			run();
		},

		downloadTemplates : function(templateList) {
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
				callBack1_dowload();
			} else {
				var req = {
					"templateName" : template.templateName
				};
				Net.asynConnect("msc/payment/template/query", req, callBack1_dowload);
			}

			function callBack1_dowload(params) {
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

		},

		downloadMerchantInfo : function() {
			var req = {
				merchId : window.user.merchId
			};
			Net.asynConnect("msc/cust/info/query", req, afterGetMerchantInfo);

		},

		afterGetMerchantInfo : function(data) {
			if ("0" == data.responseCode) {
				var params = {
					merchId : data.mer_id,
					machineId : window.user.machineId,
					merchName : data.mer_name
				};
				RMS.save("merchant", params);
				ServiceMerchInfo.setMerchInfo(params);
				_currentStep++;
				_currentType++;
				run();
			} else {
				Scene.alert(data.errorMsg);
			}
		},

		save : function() {
			switch (_saveStep) {
				case 0:
					RMS.clear("merchSettings", function() {
						RMS.save("merchSettings", {
							"settingString" : JSON.stringify(_settingParams)
						});
					});

					RMS.clear("paymentInfo", function() {
						RMS.save("paymentInfo", _paymentInfo);
					});
					break;
				case 1:
					RMS.clear("templateList", function() {
						RMS.save("templateList", _templateList);
					});
					break;
				case 2:
					_saveStep = -1;
					_currentType++;
					break;
				default:
					break;
			}
			_saveStep++;
			_currentStep++;
			setTimeout(run, 300);
		},

		end : function() {
			_settingParams = [];
			_currentStep = 0;
			_totalSteps = 0;

			Home.needUpdateUI = true;
			window.merchSettings = null;

			Scene.alert("参数下载成功", function() {
				if (ConsumptionData.dataForPayment.isExternalOrder) {
					Pay.restart();
				} else {
					Scene.goBack("Home");
				}
			});
		},

		objLength : function(o) {
			var count = 0;
			for (var i in o) {
				count++;
			}
			return count;
		}
	});

	return SettingsDownload;
});