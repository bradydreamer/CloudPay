;(function(){
 if (window.TransBatch) { return; }
  
  function gotoTransBatch() {
		SettingsIndex.transBatch();
  	//setTimeout(Scene.goBack,1000);
  }
  
  function gotoHome(){
  	
  	Scene.goBack();
  }
  
  
 window.TransBatch = {
 	"gotoTransBatch" : gotoTransBatch,
 	"gotoHome":gotoHome,
 };
  
})();