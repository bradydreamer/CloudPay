package cn.koolcloud.pos.service;
import cn.koolcloud.pos.service.MerchInfo;
import cn.koolcloud.pos.service.IAllinpayMerchCallBack;

interface IAllinpayMerchService {
   	MerchInfo getMerchInfo();
   	void setMerchInfo(in MerchInfo mi); 	
   	void setLoginStatus(String ls);  
   	void endCallPayEx();
   	
   	void registerCallback(IAllinpayMerchCallBack cb);     
    void unregisterCallback(IAllinpayMerchCallBack cb);  	
}