//app/Others/Settings/SetMerchId.js
define(['Moo'], function(Moo) {
	var SetMerchId = new Class({
		initialize : function() {

		},

		onConfirm : function(data) {
			var msg = JSON.parse(data);
			var merchId = msg.merchId;
			var params = {
				merchId : merchId
			};
			RMS.save("merchant", params);
			setTimeout(function() {
				actionAfterSet(merchId);
			}, 300);
		},

		actionAfterSet : function(merchId) {
			window.user.init({});
			window.user.merchId = merchId;
			window.user.merchIdSetResultAction();
		}
	});
});
