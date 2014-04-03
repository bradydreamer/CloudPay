;(function() {
	Global.callPlatform = function(funcData){
		JSResponser.flushMessage(JSON.stringify(funcData))
	}
})()