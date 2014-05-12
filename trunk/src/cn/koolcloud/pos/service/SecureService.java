package cn.koolcloud.pos.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class SecureService extends IntentService {
	protected final static String TAG = "SecureService";
	
	private SecureInfo secureInfo;
	private String userInfo;
	
	public SecureService() {
		super(TAG);
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind: ");
		return secureStub;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		Log.d(TAG, "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}
	
	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	};
	
	private void initSecureInfo(){
		secureInfo = new SecureInfo("00000000", "1", null, "");
	}
	
	ISecureService.Stub secureStub = new ISecureService.Stub() {

		@Override
		public SecureInfo getSecureInfo() throws RemoteException {
			if(secureInfo == null){
				initSecureInfo();
			}
			return secureInfo;
		}

		@Override
		public void setSecureInfo(SecureInfo si) throws RemoteException {
			secureInfo = si;
		}

		@Override
		public String getUserInfo() throws RemoteException {
			return userInfo;
		}

		@Override
		public void setUserInfo(String ui) throws RemoteException {
			userInfo = ui;
		}
	};
	
	@Override
	protected void onHandleIntent(Intent intent) {		
	} 

}
