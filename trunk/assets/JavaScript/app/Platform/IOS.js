//app/Platform/IOS.js
define(['Moo'], function(Moo) {
	var CUSTOM_PROTOCOL_SCHEME = 'jsCallocScheme';
	var QUEUE_HAS_MESSAGE = '__JS_QUEUE_MESSAGE__';
	var IOS = new Class({
		Extends : Global,
		initialize : function() {

		},

		callPlatform : function(funcData) {
			Global.messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE;
		}
	});
});
