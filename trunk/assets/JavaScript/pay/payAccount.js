;
(function() {
  if (window.PayAccount) {
    return;
  };
  var currentStep;
  var currentTag;

  function getInputRegexResult(field, regex) {
    if (regex == "fill_0") {
      var defaultCardId = "0000000000000000000";
      var cardPrefix = defaultCardId.substring(0, defaultCardId.length - field.length);
      field = cardPrefix + field;
    } else if (regex == "fill_ec") {
      field = "88888666" + field;
    };
    return field;
  }

  function exePurchase(params) {
    Pay.cacheData.needAuthCode = null;

    currentStep = Pay.cacheData.step;
	if(currentStep >= Pay.cacheData.flowList.length){
		Scene.alert("非正常操作，请重新操作！",function(){
			Scene.goBack("Home");
		});
		return;
	}
    currentTag = Pay.cacheData.flowList[currentStep].packTag;

    var inputRegex = Pay.cacheData.flowList[currentStep].inputRegex;
    var cardID = getInputRegexResult(params.field0, inputRegex);
    Pay.cacheData[currentTag] = cardID;
    Pay.cacheData.step = currentStep + 1;
    Pay.gotoFlow();
  }
  
  function exePrepaidCardPurchase(params){
	Pay.cacheData.needAuthCode = null;

    currentStep = Pay.cacheData.step;
    currentTag = Pay.cacheData.flowList[currentStep].packTag;

    var inputRegex = Pay.cacheData.flowList[currentStep].inputRegex;
    var cardID = getInputRegexResult(params.field0, inputRegex);
	Scene.alert("JSLOG,exePurchase,cardID = " + cardID);
    Pay.cacheData[currentTag] = cardID;
    Pay.cacheData.step = currentStep + 1;
    //don't cut cardID while prepaid card -- start mod by Teddy 28th September
//	getPrepaidCardAmount(cardID.substring(cardID.length -10, cardID.length));
	getPrepaidCardAmount(cardID);
    //don't cut cardID while prepaid card -- end mod by Teddy 28th September
    //Pay.gotoFlow();
  };

  function getPrepaidCardAmount(cardID){
	var req = {
		"v": "1.0",
		"cardId": cardID,
		"merId": "0229000297" //Pay.cacheData.brhMchtId
	};
	Net.connect("allinpay/ggpt/saleact/cardcoupon/query/bycardid", req, afterGetPrepaidCardAmount,true);

	function afterGetPrepaidCardAmount(data){
		Scene.alert("JSLOG,afterGetPrepaidCardAmount,data=" + JSON.stringify(data));
		if(data.responseCode == "0"){
			if(data.ggpt_saleact_cardcoupon_query_bycardid_response != null &&
				data.ggpt_saleact_cardcoupon_query_bycardid_response != undefined){
				Pay.cacheData.brand_desc = data.ggpt_saleact_cardcoupon_query_bycardid_response.brand_desc;
				if(data.ggpt_saleact_cardcoupon_query_bycardid_response.ori_avail_at == undefined){
					data.ggpt_saleact_cardcoupon_query_bycardid_response.ori_avail_at = "0";
				}
				Pay.cacheData.ori_avail_at = data.ggpt_saleact_cardcoupon_query_bycardid_response.ori_avail_at;
				Pay.cacheData.card_state = data.ggpt_saleact_cardcoupon_query_bycardid_response.card_state;
				Pay.gotoFlow();
			}else{
				if(data.error_response != null && data.error_response != undefined){
					Scene.alert(data.error_response.msg);
				}
			}
		}else{
			Scene.alert("JSLOG,errorMsg=" + data.errorMsg);
		}
	};
  };

  function exeCardIdResponse(data) {
    var params = JSON.parse(data);
    Pay.cacheData.track2 = "";
    Pay.cacheData.track3 = "";
    exePurchase(params);
  }

  function exeRecvData(data) {
    var params = JSON.parse(data);
    exePurchase(params);
  }
  function exeRecvDataForPrepaidCard(data){
	var params = JSON.parse(data);
    exePrepaidCardPurchase(params);
  };

  function exeSwipeResponse(data) {
    var params = JSON.parse(data);
    params.field0 = params.cardID;
    Pay.cacheData.track2 = params.track2;
    Pay.cacheData.track3 = params.track3;
	Pay.cacheData.validTime = params.validTime;

    exePurchase(params);
  }

  function clear () {
		if (currentStep == null) {
			if(Pay.cacheData.step > 0){
				Pay.cacheData.step--;
			}
	    return;
	  }
    Pay.cacheData[currentTag] = null;
    Pay.cacheData.step = currentStep;
		if(Pay.cacheData.step > 0){
			Pay.cacheData.step--;
		}

    Pay.cacheData.track2 = null;
    Pay.cacheData.track3 = null;    
  }

  window.PayAccount = {
    "exeSwipeResponse": exeSwipeResponse,
    "exeCardIdResponse": exeCardIdResponse,
    "exeRecvData": exeRecvData,
    "exeRecvDataForPrepaidCard": exeRecvDataForPrepaidCard,
    "clear": clear,
  };

})();