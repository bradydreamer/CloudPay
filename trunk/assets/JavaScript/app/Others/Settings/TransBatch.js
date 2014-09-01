//app/Others/Settings/TransBatch.js
define(["Moo"], function() {
	var TransBatch = new Class({
		initialize : function() {

		},

		gotoTransBatch : function() {
			SettingsIndex.allTransBatch(gotoHome);
		},

		gotoHome : function() {

			Scene.goBack("Home");
		}
	});

	return TransBatch;
});
