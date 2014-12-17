;
(function() {
  if (window.ListUserInfo) {
    return;
  }

  function reqMoreInfo() {
    var params = {
      "reqMoreInfo": true,
    };
    window.SettingsIndex.gotoListUsers(params);
  }

  function goBack() {
    Scene.goBack("");
  }

  function deleteAlert(params){
	  Scene.alert("JSLOG,deleteAlert：" + JSON.stringify(params));
	  var data = JSON.parse(params);
	  var userInfoList = data.userList;
	  var position = data.position;
	  var moreParams = {
			  			userItem : userInfoList[position],
						position : position
			  		};	  
	  
	  Scene.alert("是否删除账户:"+userInfoList[position].operator+"?",afterVertifyAction,"确定","136");
	  
	  function afterVertifyAction(backParams){		  
		  if(backParams.isPositiveClicked == true){
			  if(window.user.gradeId != "1"){
				  Scene.alert("137",function(){
					  	updateUserInfo();
						Scene.goBack("UsersList");  
					  });
				  return;
			  }	
			  if(window.user.userName == userInfoList[position].operator){
				  Scene.alert("140",function(){
					  	updateUserInfo();
						Scene.goBack("UsersList"); 				
					  });
				  return;
			  }	 
			  var req = {
					  delOperator : userInfoList[position].operator
			  };
			  Net.connect("msc/user/delete", req, afterDeleteUser);		  
		  }else{
			  updateUserInfo();
		  }  
	  }
	  
	  function afterDeleteUser(params){
		  if(params.responseCode == "0"){
			  Scene.alert("141",function(){
				Scene.goBack("UsersList");  
			  });
		  }else{
			  Scene.alert("142",updateUserInfo);
		  }		  
	  }
	  
	  function updateUserInfo(){
		  var propertyList = [{
				name : "lv_userInfo",
				key : "recoverList",
				value : moreParams
			}];
			Scene.setProperty("ListUserInfo", propertyList);	  
	  }  
	  
  }

  window.ListUserInfo = {
    "reqMoreInfo": reqMoreInfo,
    "goBack": goBack,
    "deleteAlert": deleteAlert
  };

})();