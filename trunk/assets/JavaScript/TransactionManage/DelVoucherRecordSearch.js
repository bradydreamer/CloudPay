;
(function() {
	if (window.DelVoucherRecordSearch) {
		return
	};

	var req_loadMore = {};

	function onConfirm(data) {
		var msg = JSON.parse(data)
		var req
		if (msg.isReqMore) {
			req = req_loadMore
		} else {
			req = {
				pageNo: 1,
				pageSize: 9,
				consumeStarttime: msg.startDate + "000000",
				consumeEndtime: msg.endDate + "235959",
			}
		}

		req_loadMore = {
			pageNo: req.pageNo + 1,
			pageSize: req.pageSize,
			consumeStarttime: req.consumeStarttime,
			consumeEndtime: req.consumeEndtime,
		}

		Net.connect("merchant/getTuanConsumeList", req, showDelVoucherRecord)
		// showDelVoucherRecord({})
	}

	function showDelVoucherRecord(msg) {
		var recordDisplayedList = msg.list
		if (null == recordDisplayedList) {
			recordDisplayedList = []
		};
		if (recordDisplayedList.length > 0) {
			for (var i = 0, j = recordDisplayedList.length; i < j; i++) {
				var recordData = recordDisplayedList[i]
				var consumeTime = recordData.consumeTime
				var delDate = consumeTime.substring(0, 8)
				recordData.delDate = delDate
				delete recordData.consumeTime
				// recordData.brhId = "0229000184";
				var cancelEnable = 0;
				if (recordData.txnCode == "6871") {
					recordData.txn = "162";
				} else if (recordData.txnCode == "6872") {
					recordData.txn = "102";
					cancelEnable = cancelEnable + 1;
				} else if (recordData.txnCode == "6873") {
					recordData.txn = "撤销";
				};

				if (recordData.txnStatus == "0") {
					recordData.txn = recordData.txn + "成功";
					cancelEnable = cancelEnable + 1;
				} else {
					recordData.txn = recordData.txn + "失败";
				};

				recordData.cancelEnable = cancelEnable == 2;
			};

			var totalPages
			if (msg.totalSize % msg.pageSize > 0) {
				totalPages = msg.totalSize / msg.pageSize + 1
			} else {
				totalPages = msg.totalSize / msg.pageSize
			}
			var hasMore = (parseInt(msg.pageNo) < parseInt("" + totalPages))
			var params = {
				hasMore: hasMore,
				recordList: recordDisplayedList
			}
			if (1 == msg.pageNo) {
				Scene.showScene("DelVoucherRecord", "", params)
			} else {
				var propertyList = [{
					name: "lv_record",
					key: "addList",
					value: params
				}]
				Scene.setProperty("DelVoucherRecord", propertyList)
			};
		} else {
			Scene.showScene("DelVoucherRecord", "", {
				hasMore: false,
				recordList: []
			})
		}
	}

	window.DelVoucherRecordSearch = {
		"onConfirm": onConfirm,
	}

})();