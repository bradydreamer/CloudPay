//app/Others/Settings/ListUserInfo.js
define(['Moo'], function() {
	var ListUserInfo = new Class({

		initialize : function() {

		},

		reqMoreInfo : function() {
			var params = {
				"reqMoreInfo" : true
			};
			window.SettingsIndex.gotoListUsers(params);
		},

		goBack : function() {
			Scene.goBack("");
		},

		deleteAlert : function(params) {
			Scene.alert("JSLOG,deleteAlert：" + JSON.stringify(params));
			var data = JSON.parse(params);
			var userInfoList = data.userList;
			var position = data.position;
			var moreParams = {
				userItem : userInfoList[position],
				position : position
			};
			Scene.alert("JSLOG,deleteAlert：userInfoList=" + JSON.stringify(userInfoList));

			Scene.alert("是否删除账户:" + userInfoList[position].operator + "?", afterVertifyAction, "确定", "取消");

			function afterVertifyAction(backParams) {
				if (backParams.isPositiveClicked == true) {
					if (window.user.gradeId != "1") {
						Scene.alert("此账户没有权限,无法执行此操作！", function() {
							updateUserInfo();
							Scene.goBack("UsersList");
						});
						return;
					}
					Scene.alert("JSLOG,afterVertifyAction,1");
					var req = {
						delOperator : userInfoList[position].operator
					};
					Net.connect("msc/user/delete", req, afterDeleteUser);
				} else {
					Scene.alert("JSLOG,afterVertifyAction,2");
					updateUserInfo();
				}
			}

			function afterDeleteUser(params) {
				if (params.responseCode == "0") {
					Scene.alert("删除成功！", function() {
						Scene.goBack("UsersList");
					});
				} else {
					Scene.alert("删除失败！", updateUserInfo);
				}
			}

			function updateUserInfo() {
				var propertyList = [{
					name : "lv_userInfo",
					key : "recoverList",
					value : moreParams
				}];
				Scene.setProperty("ListUserInfo", propertyList);
			}

		}
	});

	return ListUserInfo;
});
