;(function(){
	if (window.ConsumptionSummary) { return }

    function gotoGetConsumptionSummary(params){
  		var summaryData;
  		var req = {
                  startDate: params.startDate,
                  endDate: params.endDate,
                  ioperator: params.ioperator
  			};
  		Net.connect("msc/txn/statistic", req, afterGetSummary);

  		function afterGetSummary(params){
  			if("0" == params.responseCode){
  				summaryData = params;
  				RMS.read("merchant", afterGetMerchantInfo);
  			}else{
  			    if(params.errorMsg.substring(0,4) == "2005"){
                    summaryData = oriDate;
                    RMS.read("merchant", afterGetMerchantInfo);
  			    }else{
  				    Scene.alert(params.errorMsg);
  				}
  			}
  		}
  		function afterGetMerchantInfo(info){
  			summaryData.merchName = info.merchName;
  			summaryData.merchId = window.user.merchId;
  			summaryData.machineId = window.user.machineId;

  			var propertyList = [{
                    	"value": summaryData
                    }];
            Scene.setProperty("", propertyList);
  		}
  	}

  	function gotoRefreshConsumptionSummary(date){
    	    var msg = JSON.parse(date);
    	    var params = {
                startDate : msg.startDate,
                endDate : msg.endDate,
                ioperator : msg.operator
            }
    		window.util.exeActionWithLoginChecked(function(){
    		    gotoGetConsumptionSummary(params);
    		});
    	}

	
	window.ConsumptionSummary = {
        "gotoRefreshConsumptionSummary": gotoRefreshConsumptionSummary
	}
	var oriDate = {
	      "statistic":[{"totalAmount":"0","totalSize":"0","transType":"1021"}]
	}
})()
