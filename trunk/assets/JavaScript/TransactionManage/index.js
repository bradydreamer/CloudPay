;
(function() {
	if (window.TransactionManageIndex) {
		return;
	}
	var req_loadMore = {};
	var singleResearchTag = false;

	function handleResFromReqRecord(msg) {
		var recordDisplayedList = msg.recordList;
		if(recordDisplayedList instanceof Array){
			for (var i = 0, j = recordDisplayedList.length; i < j; i++) {
				var recordData = recordDisplayedList[i];
				handleRecordData(recordData);
			};
		}else{
			handleRecordData(recordDisplayedList);
			recordDisplayedList.confirm = "TransactionManageIndex.gotoIndex";
			return;
		}
		var pageSize = msg.pageSize == null ? 20 : msg.pageSize;
		var totalSize = msg.totalSize == null ? recordDisplayedList.length : msg.totalSize;
		var pageNo = req_loadMore.pageNo;

		var totalPages;
		if (totalSize % pageSize > 0) {
			totalPages = totalSize / pageSize + 1;
		} else {
			totalPages = totalSize / pageSize;
		}

		req_loadMore.pageNo = req_loadMore.pageNo + 1;

		var hasMore = (parseInt(pageNo) < parseInt("" + totalPages));
		/*var params = {
			hasMore : hasMore,
			recordList : recordDisplayedList,
			start_date : msg.start_date,
			end_date : msg.end_date
		};*/
		var params = {
			hasMore : hasMore,
			recordList : recordDisplayedList
		};
		//pass start date and end date to order details page --start mod by Teddy on 25th July
		if ((null != window.TransactionManageIndex.params &&
			undefined != window.TransactionManageIndex.params) &&
			(null != window.TransactionManageIndex.params.startDate && 
				null != window.TransactionManageIndex.params.endDate)) {
			params.start_date = window.TransactionManageIndex.params.startDate;
			params.end_date = window.TransactionManageIndex.params.endDate;
		} else {
			params.start_date = msg.start_date;
			params.end_date = msg.end_date;
		}
		//pass start date and end date to order details page --end mod by Teddy on 25th July
		if (window.TransactionManageIndex.refresh !== undefined) {
			//refresh consumption data ListView with new data --start mod by Teddy on 29th September
			/*params.shouldRemoveCurCtrl = true;
			Scene.showScene("ConsumptionRecord", "消费记录", params);*/
			var propertyList = [{
					name : "lv_record",
					key : "addList",
					value : params
				}];
			Scene.setProperty("ConsumptionRecord", propertyList);
			//refresh consumption data ListView with new data --end mod by Teddy on 29th September
		} else {

			if (1 == pageNo) {
				/*if (recordDisplayedList.length == 1) {
					recordDisplayedList[0].confirm = "TransactionManageIndex.gotoIndex";
					Scene.showScene("OrderDetail", "", recordDisplayedList[0]);
				} else {*/
					Scene.showScene("ConsumptionRecord", "", params);
				//};
			} else {
				var propertyList = [{
					name : "lv_record",
					key : "addList",
					value : params
				}];
				Scene.setProperty("ConsumptionRecord", propertyList);
			};
		}
	}

	function handleRecordData(params) {

		var transTime = ""+params.transTime;	
		var tDate = transTime.substring(0, 8);
		var tTime = transTime.substring(8);

		tTime = tTime.substring(0, 2) + ":" + tTime.substring(2, 4) + ":" + tTime.substring(4);

		params.tDate = tDate;
		params.tTime = tTime;
		params.transTime = util.formatDateTime(transTime);
		params.oriTransTime = transTime;
		params.transAmount = util.formatAmountStr(params.transAmount);

		var cancelEnable = false;

		params.payTypeDesc = "" + params.paymentName;
		params.cancelEnable = params.cancelEnable;

		params.transTypeDesc = getTransTypeDesc(params.transType);
		params.orderStateDesc = getOrderStateDesc(params.orderState);

	}

	function getTransTypeDesc(transType) {
		// 交易类型
		// 1021 	消费
		// 3021 	消费撤销
		// 3051 	退货
		// 1011 	预授权
		// 3011 	预授权撤销
		// 1031 	预授权完成联机
		// 3031 	预授权完成联机撤销
		// 1091 	预授权完成离线
		// 1721     转账

		var transTypeDesc = "";
		if (transType == "1021") {
			transTypeDesc = "102";
		} else if (transType == "3021") {
			transTypeDesc = "103";
		} else if (transType == "3051") {
			transTypeDesc = "104";
		} else if (transType == "1011") {
			transTypeDesc = "105";
		} else if (transType == "3011") {
			transTypeDesc = "106";
		} else if (transType == "1031") {
			transTypeDesc = "107";
		} else if (transType == "3031") {
			transTypeDesc = "108";
		} else if (transType == "1091") {
			transTypeDesc = "109";
		} else if (transType == "1721") {
            transTypeDesc = "转账";
        }
		;
		return transTypeDesc;
	}

	function getOrderStateDesc(orderState) {
		// 		订单状态
		// 0	成功
		// 1	失败
		// 2	已冲正
		// 3	已撤销
		// 4	预授权已完成
		// 5 	未知
		// 6    撤销中
		// 9	超时

		var orderStateDesc = "";
		if (orderState == "0") {
			orderStateDesc = "110";
		} else if (orderState == "1") {
			orderStateDesc = "111";
		} else if (orderState == "2") {
			orderStateDesc = "112";
		} else if (orderState == "3") {
			orderStateDesc = "113";
		} else if (orderState == "4") {
			orderStateDesc = "115";
		} else if (orderState == "5") {
			orderStateDesc = "116";
		} else if (orderState == "6") {
		    orderStateDesc = "172";
		} else if (orderState == "9") {
			orderStateDesc = "117";
		}
		;
		return orderStateDesc;
	}

	function gotoSingleRecord(data) {
		var params = JSON.parse(data);
		var refNo = params.id;
		var paymentId = params.paymentId;
		var txnId = params.txnId;
		var req = {
			"pageNo" : 1,
			"pageSize" : 20,
			"refNo" : refNo,
			"paymentId": paymentId,
			"txnId":txnId
		};
		req_loadMore = req;
		singleResearchTag = true;
		ConsumptionData.dataForPayment.rrn = refNo;
		window.TransactionManageIndex.refresh = undefined;
   		window.TransactionManageIndex.params = undefined;
		Net.connect("msc/txn/detail/query", req, handleResFromReqRecord);
	}

	function gotoConsumptionRecord(searchData) {
		var req;
		if (null != searchData && searchData.isReqMore) {
			req = req_loadMore;
		} else {
			req = {
				pageNo : 1,
				pageSize : 20
			};
			if (null != searchData) {
				req.startDate = searchData.startDate;
				req.endDate = searchData.endDate;
			}
		}
		req_loadMore = req;

		/*if (req.startDate != null) {
			Net.connect("merchant/iposHistoryRecordList", req, handleResFromReqRecord);
		} else {
			Net.connect("merchant/iposCurrentRecordList", req, handleResFromReqRecord);
		}*/
		singleResearchTag = false;
		Net.connect("msc/txn/page/query", req, handleResFromReqRecord);

	}

	function gotoGetConsumptionSummary(params){
		var summaryData;
		var req = {
                startDate: params.startDate,
                endDate: params.endDate
			};		

		Net.connect("msc/txn/statistic", req, afterGetSummary);
		
		function afterGetSummary(params){
			if("0" == params.responseCode){

				summaryData = params;
				RMS.read("merchant", afterGetMerchantInfo);
			}else{
				Scene.alert(params.errorMsg);
			}
		}
		function afterGetMerchantInfo(info){
			summaryData.merchName = info.merchName;
			summaryData.merchId = window.user.merchId;
			summaryData.machineId = window.user.machineId;
			Scene.alert("JSLOG,afterGetSummary,summaryData=" + JSON.stringify(summaryData));
			Scene.showScene("ConsumptionSummary",null,summaryData);
		}		
	}
	
	function onConsumptionRecord(data) {
		//request tag
		window.TransactionManageIndex.refresh = undefined;
		//delete global variable date object
		window.TransactionManageIndex.params = undefined;

		var msg = JSON.parse(data)
        var params = {
            startDate : msg.startDate,
            endDate : msg.endDate
        }
		window.util.exeActionWithLoginChecked(function(){
		    gotoConsumptionRecord(params);
		});
	}

	function onSingleRecordSearch() {
		window.util.showSceneWithLoginChecked("PaymentMechanism");
	}

	function onSingleRecordSearchByTxnId(){
	    window.util.showSceneWithLoginChecked("SingleRecordSearchByTxnId");
	}

	function onConsumptionRecordSearch() {
		window.util.showSceneWithLoginChecked("ConsumptionRecordSearch");
	}

	function onDelVoucherRecordSearch() {
		window.util.showSceneWithLoginChecked("DelVoucherRecordSearch");
	}

	function gotoIndex() {
		Scene.goBack("TransactionManageIndex");
	}

	function gotoConsumptionSummary(date){
	    var msg = JSON.stringify(date);
	    var params = {
            startDate : msg.startDate,
            endDate : msg.endDate
        }
		window.util.exeActionWithLoginChecked(function(){
		    gotoGetConsumptionSummary(params);
		});
	}

	function refreshResearch() {
		//clear old data first and then refresh ListView --start mod by Teddy on 29th September
		var propertyList = [{
					name : "lv_record",
					key : "updateList",
					value : params
				}];
		Scene.setProperty("ConsumptionRecord", propertyList);
		//clear old data first and then refresh ListView --end mod by Teddy on 29th September
		
		window.TransactionManageIndex.refresh = true;
	  	if (window.TransactionManageIndex.params === undefined || window.TransactionManageIndex.params === "") {
	  		//delete global variable date object
			window.TransactionManageIndex.params = undefined;
			if(singleResearchTag == false){
				window.util.exeActionWithLoginChecked(gotoConsumptionRecord);
			}else{
				var req = {
					"id": ConsumptionData.dataForPayment.rrn,
					"paymentId": ConsumptionData.dataForPayment.paymentId,
				}
				var jsonStr = JSON.stringify(req);
				var params = jsonStr.replace(/"([^"]*)"/g,"\"$1\"");
				gotoSingleRecord(params);
			}
	  	} else {
	  		var param = window.TransactionManageIndex.params;
	  		gotoConsumptionRecord(param);
	  	}
	}

	window.TransactionManageIndex = {
		"onConsumptionRecord" : onConsumptionRecord,
		"onConsumptionRecordSearch" : onConsumptionRecordSearch,
		"onDelVoucherRecordSearch" : onDelVoucherRecordSearch,
		"onSingleRecordSearch" : onSingleRecordSearch,
		"onSingleRecordSearchByTxnId": onSingleRecordSearchByTxnId,
		"gotoConsumptionRecord" : gotoConsumptionRecord,
		"gotoConsumptionSummary" : gotoConsumptionSummary,
		"gotoSingleRecord" : gotoSingleRecord,
		"gotoIndex" : gotoIndex,
		"refreshResearch" : refreshResearch
	}; 


})(); 
