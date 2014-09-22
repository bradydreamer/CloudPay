//app/Platform/Android.js
define(['Moo'], function() {
	var Android = new Class({
		
		initialize : function() {
		},

		callPlatform : function(funcData) {
			//call java method in Android project
			JSResponser.flushMessage(JSON.stringify(funcData));
		}
	});
	
	return Android;
});
