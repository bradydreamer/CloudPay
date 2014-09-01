//app/Others/Settings/SetMachineId.js
define(['Moo'], function(Moo) {
	var SetMachineId = new Class({
		initialize : function() {

		},

		onConfirm : function(data) {
			var msg = JSON.parse(data);
			var machineId = msg.machineId;
			var params = {
				machineId : machineId
			};
			RMS.save("merchant", params);
			setTimeout(function() {
				actionAfterSet(machineId);
			}, 300);
		},

		actionAfterSet : function(machineId) {
			window.user.init({});
			window.user.machineId = machineId;
			window.user.machineIdSetResultAction();
		}
	});

	return SetMachineId;
});
