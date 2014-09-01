//app/Others/Settings/SetTransId.js
define(['Moo'], function() {

	var SetTransId = new Class({

		initialize : function() {

		},

		onConfirm : function(data) {
			var msg = JSON.parse(data);
			var transId = parseInt(msg.transId, 10);
			var params = {
				"transId" : transId
			};
			RMS.save("merchant", params);
			Scene.goBack();
		}
	});
});
