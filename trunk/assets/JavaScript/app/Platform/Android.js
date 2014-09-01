//app/Platform/Android.js
define(['Moo'], function() {
	var Android = new Class({
		Extends : Global,
		initialize : function() {
		},

		callPlatform : function(funcData) {
			JSResponser.flushMessage(JSON.stringify(funcData));
		}
	});
	
	return Android;
});
