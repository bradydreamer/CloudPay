function Util() {

}

Util.prototype.formatAmountStr = function(fen) {
	fen = "" + fen
	var length = fen.length
	if (length == 0) {
		return ""
	} else if (length == 1) {
		return "0.0" + fen
	} else if (length == 2) {
		return "0." + fen
	} else {
		return fen.substring(0, length - 2) + "." + fen.substring(length - 2, length)
	}
}

Util.prototype.formatDateTime = function(time) {
	var length = time.length
	var newStr
	if (length < 4) {
		newStr = time
	}
	if (length >= 4) {
		newStr = time.substring(0, 4)
	}
	if (length >= 6) {
		newStr += "-" + time.substring(4, 6)
	}
	if (length >= 8) {
		newStr += "-" + time.substring(6, 8)
	}
	if (length >= 10) {
		newStr += " " + time.substring(8, 10)
	}
	if (length >= 12) {
		newStr += ":" + time.substring(10, 12)
	}
	if (length >= 14) {
		newStr += ":" + time.substring(12, 14)
	}

	return newStr
}

Util.prototype.exeActionWithMerchIdChecked = function(destAction) {

	RMS.read("merchant", loadMerchIdToUser);

	function loadMerchIdToUser(data) {
		if (null != data) {
			window.user.merchId = data.merchId
			window.user.machineId = data.machineId
		};
		if (null == window.user.merchId || "" == window.user.merchId) {
			window.user.setMerchIdResult(function() {
				showSetMachineIdIfNeeded(destAction, true)
			})
			Scene.showScene("SetMerchId")
			return
		}
		if (null == window.user.machineId || "" == window.user.machineId) {
			showSetMachineIdIfNeeded(destAction)
			return
		}
		destAction()
	}

	function showSetMachineIdIfNeeded(destAction, mvSelf) {
		if (null == window.user.machineId || "" == window.user.machineId) {
			window.user.setMachineIdResult(function() {
				destAction(true)
			})
			var params = null
			if (mvSelf) {
				params = {
					shouldRemoveCurCtrl: true
				}
			}
			Scene.showScene("SetMachineId", "", params)
		} else {
			destAction(true)
		}
	}

}

Util.prototype.showSceneWithLoginChecked = function(sceneName, params, sceneTitle) {
	var data = params == null ? {} : params;
	sceneTitle = sceneTitle == null ? "" : sceneTitle;

	window.util.exeActionWithLoginChecked(function(shouldRmvLogin) {
		if (shouldRmvLogin) {
			data.shouldRemoveCurCtrl = shouldRmvLogin;
		};
		Scene.showScene(sceneName, sceneTitle, data);
	}, true)
}

Util.prototype.exeActionWithLoginChecked = function(actionWithLoginNeeded, needNotGoBack) {
	if ("0" != window.user.userStatus) {
		checkMerchId(exeDestAction)
	} else {
		actionWithLoginNeeded()
	}

	function checkMerchId() {
		window.util.exeActionWithMerchIdChecked(exeDestAction)
	}

	function exeDestAction(mvSelfWhenShowLogin) {
		ServiceMerchInfo.getInfo(checkUserInfo);

		function checkUserInfo(data) {
			if (null != data) {
				var transTime = data.transTime;
				if (transTime != null && util.isReverseToday(transTime)) {
					window.user.init(data);
				};
			};

			if ("0" != window.user.userStatus) {
				var callback = function() {
					if (!needNotGoBack) {
						Scene.goBack()
					}
					setTimeout(function() {
						actionWithLoginNeeded(true);
					}, 300)
				}
				window.user.setLoginResult(callback)
				var nextForm = null
				if (true == mvSelfWhenShowLogin) {
					nextForm = {
						shouldRemoveCurCtrl: true
					}
				}
				Scene.showScene("Login", "", nextForm);
			} else {
				actionWithLoginNeeded();
			}
		}
	}
}

Util.prototype.stringlen = function(str) {
	return str.replace(/[^\x00-\xff]/g, "aa").length;
}

Util.prototype.getTransType = function(transTypeKey) {
	if ("SALE" == transTypeKey) {
		return "SALE";
	} else if ("DELIVERY_VOUCHER" == transTypeKey) {
		return "DELIVERY_VOUCHER";
	} else if ("BALANCE" == transTypeKey) {
		return "BALANCE";
	}
}

Util.prototype.getPayTypeCode = function(payTypeKey) {
	if ("koolcloud_cash" == payTypeKey) {
		return "9100"
	}
	if ("koolcloud_account" == payTypeKey) {
		return "9110"
	} else if ("quickPay_debit" == payTypeKey) {
		return "9120"
	} else if ("quickPay_credit" == payTypeKey) {
		return "9121"
	} else if ("membership_card" == payTypeKey) {
		return "9130"
	} else if ("coupon" == payTypeKey) {
		return "9142"
	} else if ("swipeCard" == payTypeKey) {
		return "9150"
	} else if ("deliveryVocher" == payTypeKey) {
		return "9141"
	} else if ("koolcloud" == payTypeKey) {
		return "9000"
	}
}

Util.prototype.payTypeCode2Name = function(payTypeCode) {
	if ("9100" == payTypeCode) {
		return "现金"
	} else if ("9110" == payTypeCode) {
		return "通联虚拟账户"
	} else if ("9120" == payTypeCode) {
		return "快捷支付"
	} else if ("9121" == payTypeCode) {
		return "快捷支付"
	} else if ("9130" == payTypeCode) {
		return "会员卡"
	} else if ("9142" == payTypeCode) {
		return "优惠券"
	} else if ("9141" == payTypeCode) {
		return "提货券"
	} else if ("9150" == payTypeCode) {
		return "刷卡"
	} else if ("9900" == payTypeCode) {
		return "其他"
	} else {
		return ""
	}
}

Util.prototype.isReverseToday = function(transDate) {
	var curDate = new Date()
	if (curDate.getFullYear() != transDate.substring(0, 4) || curDate.getMonth() + 1 != transDate.substring(4, 6) || curDate.getDate() != transDate.substring(6, 8)) {
		return false
	} else {
		return true
	}
}

Util.prototype.showNoticeIsPaying = function() {
	var appName = ConsumptionData.externalExtraData.appName
	if (!appName) {
		appName = ""
	}
	Scene.alert(appName + " 还有未完成的订单，请完成该订单后再进行此操作")
}


Util.prototype.getProductInfo = function(data, open_brh, payment_id) {

	window.merchSettings = data;
	var settingString = data.settingString;
	if (settingString == null || settingString.length == 0) {
		window.util.showSceneWithLoginChecked("SettingsDownload");
		return;
	};
	var merchSettings = JSON.parse(settingString);
	if (merchSettings == null || merchSettings.length == 0) {
		return;
	};

	for (var i = 0; i < merchSettings.length; i++) {
		var typeName = merchSettings[i].openBrh;
		var typeId = merchSettings[i].paymentId;
		if (open_brh == merchSettings[i].openBrh && payment_id == merchSettings[i].paymentId) {
			return merchSettings[i];
		}
	};
	return null;
};

window.util = new Util()