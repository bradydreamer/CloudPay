//app/Common/UserInfo.js
define(['Moo', 'ServiceMerchInfo'], function(Moo, ServiceMerchInfo) {
	var merchId = "";
	var machineId = "";
	var userName = "";
	var userStatus = "";
	var gradeId = "";
	var token = null;
	var reloginAction = function() {
	};
	var merchIdSetResultAction = function() {
	};
	var machineIdSetResultAction = function() {
	};

	var UserInfo = new Class({

		initialize : function(data) {
			this.userName = data.userName;
			this.userStatus = data.userStatus;
			this.gradeId = data.gradeId;
			this.token = data.token;
			var serviceMerchInfo = new ServiceMerchInfo();
			serviceMerchInfo.setInfo(data);

		},

		setLoginResult : function(func) {
			this.reloginAction = function(data) {
				if (func) {
					func(data);
				}
			};
		},

		setMerchIdResult : function(func) {
			this.merchIdSetResultAction = function(data) {
				if (func) {
					func(data);
				}
			};
		},

		setMachineIdResult : function(func) {
			this.machineIdSetResultAction = function(data) {
				if (func) {
					func(data);
				}
			};
		},

		setSignInResult : function(func) {
			this.afterSignInAction = function(data) {
				if (func) {
					func(data);
				}
			};
		}
	});

	return UserInfo;
});
