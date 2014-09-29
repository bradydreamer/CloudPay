//app/TransactionManage/DelVoucherRecord.js
define(['Moo'], function(Moo) {
	
	var DelVoucherRecord = new Class({
		initialize : function() {
			
		},
		
		onCancel : function(data) {
			var msg = JSON.parse(data);
			var g_index = msg.index;
	
			var req = {
				"seqId" : msg.seqId,
				"consumePwd" : msg.consumePwd,
				"consumeQty" : msg.consumeQty,
				"brhId" : msg.brhId
			};
			Net.connect("merchant/voucherConsumeCancel", req, updateDelVoucherRecord);
	
			function updateDelVoucherRecord(data) {
				var propertyList = [{
					"name" : "lv_record",
					"key" : "deleteALine",
					"value" : g_index
				}];
				Scene.setProperty("DelVoucherRecord", propertyList);
			}
	
		}
	});
	
	return DelVoucherRecord;
});
