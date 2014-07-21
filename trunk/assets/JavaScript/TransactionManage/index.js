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
		var pageNo = msg.pageNo == null ? 1 : msg.pageNo;

		var totalPages;
		if (totalSize % pageSize > 0) {
			totalPages = totalSize / pageSize + 1;
		} else {
			totalPages = totalSize / pageSize;
		}

		req_loadMore.pageNo = req_loadMore.pageNo + 1;

		var hasMore = (parseInt(pageNo) < parseInt("" + totalPages));
		var params = {
			hasMore : hasMore,
			recordList : recordDisplayedList,
			start_date : msg.start_date,
			end_date : msg.end_date
		};
		if (window.TransactionManageIndex.refresh !== undefined) {
			params.shouldRemoveCurCtrl = true;
			Scene.showScene("ConsumptionRecord", "消费记录", params);
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

		var transTypeDesc = "";
		if (transType == "1021") {
			transTypeDesc = "消费";
		} else if (transType == "3021") {
			transTypeDesc = "消费撤销";
		} else if (transType == "3051") {
			transTypeDesc = "退货";
		} else if (transType == "1011") {
			transTypeDesc = "预授权";
		} else if (transType == "3011") {
			transTypeDesc = "预授权撤销";
		} else if (transType == "1031") {
			transTypeDesc = "预授权完成联机";
		} else if (transType == "3031") {
			transTypeDesc = "预授权完成联机撤销";
		} else if (transType == "1091") {
			transTypeDesc = "预授权完成离线";
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
		// 9	超时

		var orderStateDesc = "";
		if (orderState == "0") {
			orderStateDesc = "成功";
		} else if (orderState == "1") {
			orderStateDesc = "失败";
		} else if (orderState == "2") {
			orderStateDesc = "已冲正";
		} else if (orderState == "3") {
			orderStateDesc = "已撤销";
		} else if (orderState == "4") {
			orderStateDesc = "已完成";
		} else if (orderState == "5") {
			orderStateDesc = "交易中断";
		} else if (orderState == "9") {
			orderStateDesc = "超时";
		}
		;
		return orderStateDesc;
	}

	function gotoSingleRecord(data) {
		var params = JSON.parse(data);
		var refNo = params.id;
		var paymentId = params.paymentId;
		var req = {
			"refNo" : refNo,
			"paymentId": paymentId,
		};
		singleResearchTag = true;
		ConsumptionData.dataForPayment.rrn = refNo;
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
	
	function onConsumptionRecord() {
		//request tag
		window.TransactionManageIndex.refresh = undefined;
		//delete global variable date object
		window.TransactionManageIndex.params = undefined;
		window.util.exeActionWithLoginChecked(gotoConsumptionRecord);
	}

	function onSingleRecordSearch() {
		window.util.showSceneWithLoginChecked("PaymentMechanism");
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

	function refreshResearch() {
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
		"gotoConsumptionRecord" : gotoConsumptionRecord,
		"gotoSingleRecord" : gotoSingleRecord,
		"gotoIndex" : gotoIndex,
		"refreshResearch" : refreshResearch
	}; 


})(); 
