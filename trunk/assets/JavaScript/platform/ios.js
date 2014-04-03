;(function() {
	var CUSTOM_PROTOCOL_SCHEME = 'jscallocscheme'
	var QUEUE_HAS_MESSAGE = '__JS_QUEUE_MESSAGE__'
	Global.callPlatform = function(funcData){
		Global.messagingIframe.src = CUSTOM_PROTOCOL_SCHEME + '://' + QUEUE_HAS_MESSAGE
	}
})()