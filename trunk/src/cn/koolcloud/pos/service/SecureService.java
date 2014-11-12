package cn.koolcloud.pos.service;

import cn.koolcloud.pos.service.ISecureService;
import android.app.IntentService;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;

public class SecureService extends IntentService {
	protected final static String TAG = "SecureService";
	
	public final static String ACTION = "ex_action";
	
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
		ICallBack mCallBack;
		
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
		
		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {
			String packageName = "";
			String[] packages = SecureService.this.getPackageManager().getPackagesForUid(getCallingUid());
			if (packages != null && packages.length > 0) {
				packageName = packages[0];
				Log.i(TAG, "onTransact  service----------------------");
				Log.i(TAG, "onTransact  packageName=" + packageName);
			}
			return super.onTransact(code, data, reply, flags);
		}
		
		@Override
		public void getSummary(ICallBack iCallBack) throws RemoteException {
			// TODO Auto-generated method stub
			mCallBack = iCallBack;
			Log.w("getSummary", "--------------------------getSummary");
			Intent intent = new Intent();
			intent.setAction("cn.koolcloud.pos.controller.GetSummaryActivity");
			intent.putExtra(ACTION, "getSummary");
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}
		
		public void getSummaryCallBack(String summary) throws RemoteException {
			Log.i(TAG, "getSummaryCallBack-----------result----------" + summary);
			mCallBack.summaryDataCallBack(summary);
		}
	};
	
	@Override
	protected void onHandleIntent(Intent intent) {		
	} 

}
