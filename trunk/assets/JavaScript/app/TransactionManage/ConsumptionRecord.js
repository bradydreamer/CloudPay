//app/TransactionManage/ConsumptionRecord.js
define(['Moo'], function(Moo) {
	var ConsumptionRecord = new Class({

		initialize : function() {

		},

		getRecordDetail : function(data) {
			var msg = JSON.parse(data);
			msg.confirm = "ConsumptionRecord.goBack";
			Scene.showScene("OrderDetail", "", msg);
		},

		reqMore : function() {
			var params = {
				"isReqMore" : true,
			};
			window.TransactionManageIndex.gotoConsumptionRecord(params);
		},

		goBack : function() {
			Scene.goBack("");
		}
	});
	
	return ConsumptionRecord;
});
