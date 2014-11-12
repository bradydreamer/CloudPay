package cn.koolcloud.pos.service;
import cn.koolcloud.pos.service.SecureInfo;
import cn.koolcloud.pos.service.ICallBack;

interface ISecureService {
   	SecureInfo getSecureInfo();
   	void setSecureInfo(in SecureInfo si);
   	String getUserInfo();
   	void setUserInfo(String ui);
   	
   	void getSummary(ICallBack iCallBack);
	void getSummaryCallBack(String summary);
}