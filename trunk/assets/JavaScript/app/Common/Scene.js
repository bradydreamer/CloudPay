//app/Common/Scene.js
define(['Moo', 'Global'], function(Moo, Global) {
	var global;
	var Scene = new Class({

		initialize : function() {
			//global = new Global();
			Scene.alert("JSLOG Scene.initialize");
		}
	});

	showScene = function(sName, sTitle, sData) {
		var scene = {
			"name" : sName,
			"title" : sTitle,
		};
		if (sData) {
			scene.data = sData;
		}
		Global.callObjcHandler("showScene", scene, null);
	};

	alert = function(msg, callbackfunc, positiveButtonText, negativeButtonText) {

		var params = {
			"msg" : msg,
		};
		if (null != positiveButtonText) {
			params.positiveButtonText = positiveButtonText;
		};
		if (null != negativeButtonText) {
			params.negativeButtonText = negativeButtonText;
		};
		Global.callObjcHandler("alert", params, callbackfunc);
	};

	setProperty = function(sceneNm, propertyList) {

		var data = {
			"controller" : sceneNm,
			"params" : propertyList,
		};
		Global.callObjcHandler("setProperty", data, null);
	};

	goBack = function(sName, sData) {

		var scene = {
			"name" : sName
		};
		if (sData) {
			scene.data = sData;
		}
		Global.callObjcHandler("goBack", scene, null);
	};
	
	return Scene;
});
