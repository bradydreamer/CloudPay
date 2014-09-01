//app/Common/Global.js
define(['Moo'], function(Moo) {
	var messagingIframe;
	var msg;
	var callbackQueue = {};
	var uniqueId = 0;
	var doc = document;

	var Global = new Class({

		//constructor
		initialize : function() {
			_createQueueReadyIframe(doc);
		},

		_createQueueReadyIframe : function(doc) {
			Global.messagingIframe = doc.createElement('iframe');
			Global.messagingIframe.style.display = 'none';
			doc.documentElement.appendChild(Global.messagingIframe);
		},

		calloc : function(func, params, callbackfunc) {
			var funcData = {
				handler : func,
				params : params,
			};

			if (callbackfunc) {
				var callbackId = 'js_cb_' + (uniqueId++);
				funcData.callbackId = callbackId;

				callbackQueue[callbackId] = function(data) {
					if (callbackfunc) {
						callbackfunc(data);
					} else {
						// Scene.alert("cannot find objcCallBack func")
					}
				};
			}
			msg = funcData;
			Global.callPlatform(funcData);
		},

		callPlatform : function(funcData) {

		},

		objcResponse : function(data) {
			var message = JSON.parse(data);
			if (message.responseId) {
				var handler = callbackQueue[message.responseId];
				if (handler) {
					var completed = handler(message.data);
					if (completed != false) {
						delete callbackQueue[message.responseId];
					}
				} else {
					// Scene.alert("cannot find callbackQueue func:" + message.responseId)
				}
			}
			return "ok";
		},

		callJS : function(data) {

			var message = JSON.parse(data);
			var result;
			try {
				result = eval(message.handler)(JSON.stringify(message.params));
			} catch (ex) {
				Scene.alert("callJSError:" + ex.name + "; " + ex.message);
			} finally {
				if (result == null) {
					result = "";
				}
			}
			JSResponser.sendJsResponse(result);
		},

		fetchMessage : function() {

			return JSON.stringify(msg);
		},

		clearGlobal : function() {
			window.getFormData = null;
			window.Temp = {};
		},

		getSystemInfo : function(callbackfunc) {

			Global.callObjcHandler("getSystemInfo", "", callbackfunc);
		},

		openUrl : function(url, callbackfunc) {

			Global.callObjcHandler("openUrl", {
				"url" : url
			}, callbackfunc);
		},

		clearCache : function(data) {

			var params = JSON.parse(data);
			var cacheList = params.list;
			for (var index in cacheList) {
				var cache = cacheList[index];
				window[cache] = null;
			};
		},

		exit : function() {
			Global.callObjcHandler("exit", "");
		},

		getMerchInfo : function(callbackfunc) {
			Global.callObjcHandler("getSystemInfo", "", callbackfunc);
		}
	});
	return Global;
});
