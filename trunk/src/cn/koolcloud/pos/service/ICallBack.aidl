package cn.koolcloud.pos.service;

interface ICallBack{
	/**
	*callback of caller
	*handle by server
	**/
	void handleByServer(int result);
	void summaryDataCallBack(String summary);
	void orderListDataCallBack(String orderList);
}