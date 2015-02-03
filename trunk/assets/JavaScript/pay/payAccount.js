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
		Scene.alert("120",function(){
			Scene.goBack("Home");
		});
		return;
	}
    currentTag = Pay.cacheData.flowList[currentStep].packTag;

    var inputRegex = Pay.cacheData.flowList[currentStep].inputRegex;
    var cardID = getInputRegexResult(params.field0, inputRegex);
    Pay.cacheData[currentTag] = cardID;
    Pay.cacheData.step = currentStep + 1;
    if(Pay.cacheData.isICCard == true){
        Pay.cacheData.isICCard = false;
        Pay.gotoFlow();
    }else{
        if(ConsumptionData.dataForPayment.isExternalOrder == true){
            if(ConsumptionData.dataForExternal.preferential == true){
                RMS.read("merchant", actionForCalculate);
            }else{
                Pay.gotoFlow();
            }
        }else{
            Pay.gotoFlow();
        }
    }
  }

  function actionForCalculate(data){
    ConsumptionData.dataForExternal.merchId = data.merchId;
    if(isICPreferential == true){
        ConsumptionData.dataForExternal.cardId = icCardId;
    }else{
        ConsumptionData.dataForExternal.cardId = Pay.cacheData["F02"];
    }
    var req = {
        "v": "1.0",
        "merId": data.merchId,
        "terminalNo": data.machineId,
        "actiId": ConsumptionData.dataForExternal.actiId,
        "originalAmount": util.formatAmountStr(Pay.cacheData["F04"]),
        "cardNo": ConsumptionData.dataForExternal.cardId
    }
    Net.connect("mscbank/discount/calculate", req, actionAfterCalculate, true);
  }

  function actionAfterCalculate(data){
    //var params = JSON.parse(data);
    var params = data;
    if(params.responseCode == "0"){
        Scene.alert("应付金额：" + util.formatAmountStr(Pay.cacheData["F04"]) + " 元\n实付金额：" + params.disResult.payAmount + " 元\n是否继续进行交易？",nextAction,"确定","136");
    }else{
        var type = params.errorMsg.substring(7,8);
        if(params.errorMsg.substring(0,6) == "MB1000"){
            //Scene.alert("181",errorNextAction,"确定","136");
            errorAction("181",type);
        }else if(params.errorMsg.substring(0,6) == "MB1001"){
            //Scene.alert("182",errorNextAction,"确定","136");
            errorAction("182",type);
        }else if(params.errorMsg.substring(0,6) == "MB1002"){
            //Scene.alert("183",errorNextAction,"确定","136");
            errorAction("183",type);
        }else if(params.errorMsg.substring(0,6) == "MB1003"){
            //Scene.alert("184",errorNextAction,"确定","136");
            errorAction("184",type);
        }else if(params.errorMsg.substring(0,6) == "MB1004"){
            //Scene.alert("185",errorNextAction,"确定","136");
            errorAction("185",type);
        }else if(params.errorMsg.substring(0,6) == "MB1005"){
            //Scene.alert("186",errorNextAction,"确定","136");
            errorAction("186",type);
        }else if(params.errorMsg.substring(0,6) == "MB9000"){
            /*Scene.alert("187",function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });*/
            errorAction("187",type);
        }else if(params.errorMsg.substring(0,6) == "MB9001"){
            /*Scene.alert("188",function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });*/
            errorAction("188",type);
        }else if(params.errorMsg.substring(0,6) == "MB9002"){
            /*Scene.alert("189",function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });*/
            errorAction("189",type);
        }else if(params.errorMsg.substring(0,6) == "MB9999"){
            /*Scene.alert("190",function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });*/
            errorAction("190",type);
        }else {
            Scene.alert(params.errorMsg,function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });
        }
    }

    function errorAction(msg,type){
        if(type == "0"){
            var msg = parseInt(msg) + 20;
            msg = String(msg);
            Scene.alert(msg,errorNextAction,"确定","136");
        }else if(type == "1"){
            if(isICPreferential == true){
                isICPreferential = false;
            }
            Scene.alert(msg,function(){
                if(ConsumptionData.dataForPayment.isExternalOrder){
                    Pay.restart();
                }else{
                    Scene.goBack("Home");
                }
            });
        }
    }

    function errorNextAction(nextParams){
        if(nextParams.isPositiveClicked == true){
            ConsumptionData.dataForExternal.transAmount = Pay.cacheData["F04"];
            ConsumptionData.dataForExternal.paidAmount = Pay.cacheData["F04"];
            ConsumptionData.dataForExternal.disResultId = "N0";
            if(isICPreferential == true){
                isICPreferential = false;
                var datalist = [{
                        "isICPreferential": true,
                        "changedAmount": util.formatAmountStr(ConsumptionData.dataForExternal.paidAmount)
                    }];
                Scene.setProperty("PayAccount",datalist);
            }else{
                Pay.gotoFlow();
            }
        }else{
            if(isICPreferential == true){
                isICPreferential = false;
            }
            if(ConsumptionData.dataForPayment.isExternalOrder){
                Pay.restart();
            }else{
                Scene.goBack("Home");
            }
        }
    }
    function nextAction(nextData){
        if(nextData.isPositiveClicked == true){
            ConsumptionData.dataForExternal.transAmount = Pay.cacheData["F04"];
            Pay.cacheData["F04"] = util.yuan2fenStr(params.disResult.payAmount);
            ConsumptionData.dataForPayment.transAmount = util.yuan2fenStr(params.disResult.payAmount);
            ConsumptionData.dataForExternal.disResultId = params.disResult.id;
            ConsumptionData.dataForExternal.paidAmount = params.disResult.payAmount;
            if(isICPreferential == true){
                isICPreferential = false;
                var datalist = [{
                        "isICPreferential": true,
                        "changedAmount": params.disResult.payAmount
                    }];
                Scene.setProperty("PayAccount",datalist);
            }else{
                Pay.gotoFlow();
            }
        }else{
            if(isICPreferential == true){
                isICPreferential = false;
            }
            if(ConsumptionData.dataForPayment.isExternalOrder){
                Pay.restart();
            }else{
                Scene.goBack("Home");
            }
        }
    }
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
				if (data.ggpt_saleact_cardcoupon_query_bycardid_response.rsp_code != "0000") {
					Scene.alert("154", function() {
						Scene.goBack("Home");
					});
				} else {
				
					Pay.gotoFlow();
				}
			}else{
				if(data.error_response != null && data.error_response != undefined){
					Scene.alert(data.error_response.msg);
				}
			}
		}else{
			Scene.alert(data.errorMsg,function(){
			    Scene.goBack("Home");
			});
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
    var cardId = params.cardID;
    params.field0 = params.cardID;
    Pay.cacheData.track2 = params.track2;
    Pay.cacheData.track3 = params.track3;
	Pay.cacheData.validTime = params.validTime;
	var serviesCode = (params.servicesCode).substring(0,1);
	if( (serviesCode == "2" || serviesCode == "6") && cardId.substring(0,6) != "666010"){
		Scene.alert("JSLOG,serviesCode  is IC !" + serviesCode);
		var datalist = [{"close": false }];
		Scene.setProperty("PayAccount",datalist);	
		return;
	}else{
		var datalist = [{"close": true}];
		Scene.setProperty("PayAccount",datalist);
	}
    exePurchase(params);
  }

  function exeICSwipeResponse(data){
	  var params = JSON.parse(data);
	  if(params.isCancelled){
			Pay.flowRestartFunction();	
			return;
	  }
	  params.field0 = params.cardID;
	  Pay.cacheData.track2 = params.track2;
	  Pay.cacheData.track3 = "";
	  Pay.cacheData.validTime = params.validTime;
	  Pay.cacheData.pwd = params.pwd;
	  Pay.cacheData.isICCard = true;
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

  function goBackHome(data){
  	var params = JSON.parse(data);
	Scene.alert(params.alert,function(){
		if(ConsumptionData.dataForPayment.isExternalOrder){
				Pay.restart();
		}else{
			Scene.goBack("Home");
		}
	});
	
  }

  function cancelDialog(){
	  Scene.alert("155");
  }

  function isPreferential(data){
      var params = JSON.parse(data);
      icCardId = params.ICCardID;
      isICPreferential = true;
      RMS.read("merchant", actionForCalculate);
  }

  function CompleteInput(data) {
  		var params = JSON.parse(data)
  		var actionPurpose = params.actionPurpose
  		var pwd = params.pwd
  		if ("Balance" == actionPurpose) {
  			if (params.isCancelled) {
  				Scene.goBack("Home")
  			} else {
  				ConsumptionData.dataForBalance.balancePwd = params.pwd;
  				//window.data8583.get8583(ConsumptionData.dataForBalance, afterGetBalance8583);
  				Pay.checkTransReverse("msc/balance",function(){
  					window.data8583.get8583(ConsumptionData.dataForBalance, afterGetBalance8583);
  				});
  			}
  		} else {
  			if (params.isCancelled) {
  				Pay.flowRestartFunction();
  			} else {
  				var currentStep = Pay.cacheData.step;
  				if(currentStep >= Pay.cacheData.flowList.length){
  					Scene.alert("120",function(){
  						Scene.goBack("Home");
  					});
  					return;
  				}
  				var currentTag = Pay.cacheData.flowList[currentStep].packTag;

  				Pay.cacheData[currentTag] = pwd;
  				Pay.cacheData.authCode = params.authCode;

  				Pay.cacheData.step = currentStep + 1;
  				Pay.gotoFlow();
  			}
  		}
  	}

  	function afterGetBalance8583(data) {
  		var params = data
  		params.shouldRemoveCurCtrl = true;
  		Scene.showScene("BalanceResult", "", params)
  	}

  var isICPreferential = false;
  var icCardId = "0";
  window.PayAccount = {
    "exeSwipeResponse": exeSwipeResponse,
	"exeICSwipeResponse": exeICSwipeResponse,
    "exeCardIdResponse": exeCardIdResponse,
    "exeRecvData": exeRecvData,
    "exeRecvDataForPrepaidCard": exeRecvDataForPrepaidCard,
    "clear": clear,
    "goBackHome": goBackHome,
    "cancelDialog": cancelDialog,
    "CompleteInput": CompleteInput,
    "isPreferential": isPreferential
  };

})();