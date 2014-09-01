//app/TransactionManage/ConsumptionRecordSearch.js
define(['Moo'], function() {
	var ConsumptionRecordSearch = new Class({

		initialize : function() {

		},

		onConfirm : function(data) {
			var msg = JSON.parse(data);
			var params = {
				startDate : msg.startDate + "000000",
				endDate : msg.endDate + "235959",
			};
			//save startDate and endDate to global variable
			window.TransactionManageIndex.refresh = undefined;
			window.TransactionManageIndex.params = params;
			//save end
			window.TransactionManageIndex.gotoConsumptionRecord(params);
		},

		showConsumptionRecord : function(data) {
			window.TransactionManageIndex.showConsumptionRecord(data);
		}
	});
	
	return ConsumptionRecordSearch;
});

