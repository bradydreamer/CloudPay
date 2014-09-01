//app/Others/Settings/ModifyPwd.js
define(['Moo'], function(Moo) {
	var ModifyPwd = new Class({
		initialize : function() {

		},

		gotoModifyPwd : function(data) {
			var msg = JSON.parse(data);
			var origPwd = msg.origalPwd;
			var newPwd = msg.newPwd;

			var req = {
				pwd : newPwd,
				oldPwd : origPwd,
			};
			Net.connect("msc/user/pwd/change", req, actionAfterModify);
			/*var data = {
			 responseCode:"0",
			 errorMsg:"dkdkdk",
			 }
			 actionAfterModify(data);*/
		},

		actionAfterModify : function(data) {
			if (data.responseCode != "0") {
				Scene.alert(data.errorMsg);
				return;
			} else {
				Scene.alert("密码修改成功，请重新登录！", resultAction);
			}
		},

		resultAction : function() {
			var params = null;
			params = {
				shouldRemoveCurCtrl : true
			};
			window.user.init({});
			window.user.setLoginResult(function() {
				Scene.goBack("Home");
			});

			RMS.read("merchant", getMerchInfo);
			function getMerchInfo(data) {

				var formData = {
					"merchId" : data.merchId,
					"operator" : data.operator,
				};
				Scene.showScene("Login", "", formData);
			}

		}
	});

	return ModifyPwd;
});
