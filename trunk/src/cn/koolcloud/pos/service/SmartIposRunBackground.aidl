package cn.koolcloud.pos.service;
import cn.koolcloud.pos.service.ICallBack;
import android.content.Context;

interface SmartIposRunBackground
{
	void startServerDemo(ICallBack iCallBack);
	void invokCallBack(int result);
	
}