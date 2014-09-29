;(function(){
 if (window.TransBatch) { return; }
  
  function gotoTransBatch() {
		SettingsIndex.allTransBatch(gotoHome);
  }
  
  function gotoHome(){
  	
  	Scene.goBack("Home");
  }
  
  
 window.TransBatch = {
 	"gotoTransBatch" : gotoTransBatch,
 	"gotoHome":gotoHome,
 };
  
})();