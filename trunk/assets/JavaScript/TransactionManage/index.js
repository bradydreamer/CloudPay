;
(function() {
	if (window.TransactionManageIndex) {
		return;
	}
	var req_loadMore = {};

	function handleResFromReqRecord(msg) {
		var recordDisplayedList = msg.recordList;
		for (var i = 0, j = recordDisplayedList.length; i < j; i++) {
			var recordData = recordDisplayedList[i];
			handleRecordData(recordData);
		};

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
			recordList : recordDisplayedList
		};
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

	function handleRecordData(params) {
		var transTime = params.transTime;
		var tDate = transTime.substring(0, 8);
		var tTime = transTime.substring(8);
		tTime = tTime.substring(0, 2) + ":" + tTime.substring(2, 4) + ":" + tTime.substring(4);
		params.tDate = tDate;
		params.tTime = tTime;

		params.transTime = util.formatDateTime(params.transTime);
		params.transAmount = util.formatAmountStr(params.transAmount);

		var cancelEnable = false;

		params.payTypeDesc = "" + params.paymentName;
		params.cancelEnable = params.cancelEnable;

		params.transTypeDesc = getTransTypeDesc(params.transType);
		params.orderStateDesc = getOrderStateDesc(params.orderState);
	}

	function getTransTypeDesc(transType) {
		// 交易类型
		// 6060	消费
		// 6062	消费撤销
		// 6078	退货
		// 6034	预授权
		// 6036	预授权撤销
		// 6042	预授权完成联机
		// 6045	预授权完成联机撤销
		// 6043	预授权完成离线

		var transTypeDesc = "";
		if (transType == "6060") {
			transTypeDesc = "消费";
		} else if (transType == "6062") {
			transTypeDesc = "消费撤销";
		} else if (transType == "6078") {
			transTypeDesc = "退货";
		} else if (transType == "6034") {
			transTypeDesc = "预授权";
		} else if (transType == "6036") {
			transTypeDesc = "预授权撤销";
		} else if (transType == "6042") {
			transTypeDesc = "预授权完成联机";
		} else if (transType == "6045") {
			transTypeDesc = "预授权完成联机撤销";
		} else if (transType == "6043") {
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
			orderStateDesc = "预授权已完成";
		} else if (orderState == "9") {
			orderStateDesc = "超时";
		}
		;
		return orderStateDesc;
	}

	function gotoSingleRecord(data) {
		var params = JSON.parse(data);
		var rrn = params.id;
		var req = {
			"ref" : rrn,
		};

		Net.connect("merchant/orderSearch", req, handleResFromReqRecord);
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

		if (req.startDate != null) {
			Net.connect("merchant/iposHistoryRecordList", req, handleResFromReqRecord);
		} else {
			Net.connect("merchant/iposCurrentRecordList", req, handleResFromReqRecord);
		}

	}
	
	function onConsumptionRecord() {
		window.util.exeActionWithLoginChecked(gotoConsumptionRecord);
	}

	function onSingleRecordSearch() {
		window.util.showSceneWithLoginChecked("SingleRecordSearch");
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


	window.TransactionManageIndex = {
		"onConsumptionRecord" : onConsumptionRecord,
		"onConsumptionRecordSearch" : onConsumptionRecordSearch,
		"onDelVoucherRecordSearch" : onDelVoucherRecordSearch,
		"onSingleRecordSearch" : onSingleRecordSearch,
		"gotoConsumptionRecord" : gotoConsumptionRecord,
		"gotoSingleRecord" : gotoSingleRecord,
		"gotoIndex" : gotoIndex,
	}; 


})(); 