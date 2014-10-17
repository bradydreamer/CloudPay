;
(function() {
	if (window.OthersIndex) {
		return;
	}

	function gotoBalance() {
		var nextForm = {
			"btn_swipe": 1,
			"swipeCard": "OthersIndex.gotoBalanceResult",
		};
		Scene.showScene("PayAccount", "", nextForm);
	}

	function gotoBalanceResult(data) {
		var params = JSON.parse(data);
		params.actionPurpose = "Balance";
		Scene.showScene("PinPad", "", params);
	}

	window.OthersIndex = {
		"gotoBalance": gotoBalance,
		"gotoBalanceResult": gotoBalanceResult,
	};

})();