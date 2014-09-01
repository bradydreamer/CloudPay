//app/Others/OthersIndex.js
define(['Moo'], function(Moo) {
	var OthersIndex = new Class({
		initialize : function() {

		},

		gotoBalance : function() {
			var nextForm = {
				"btn_swipe" : 1,
				"swipeCard" : "OthersIndex.gotoBalanceResult",
			};
			Scene.showScene("PayAccount", "", nextForm);
		},

		gotoBalanceResult : function(data) {
			var params = JSON.parse(data);
			params.actionPurpose = "Balance";
			Scene.showScene("PinPad", "", params);
		}
	});

	return OthersIndex;
});
