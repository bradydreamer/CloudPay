package cn.koolcloud.pos.service;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.Log;
import cn.koolcloud.pos.service.ICallBack;
import cn.koolcloud.pos.service.SmartIposRunBackground.Stub;

public class RunBackgroundService extends Service {

	public static final String TAG = "RunBackgroundService";

	// private static final String START_ACTION =
	// "cn.koolcloud.pos.AppInitScreen";
	private static final String START_ACTION = "cn.koolcloud.pos.PayExScreen";
	public final static String ACTION_INIT = "appInit";
	public final static String ACTION = "ex_action";
	public final static String ACTION_PAY = "pay";
	public final static String PACKAGE_NAME = "packageName";
	String packageName = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onBind  service----------------------");
		return mBinder;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onCreate  service----------------------");
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onDestroy  service----------------------");
		super.onDestroy();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, "onUnbind  service----------------------");
		return super.onUnbind(intent);
	}

	Stub mBinder = new Stub() {
		ICallBack mCallBack;

		public void startServerDemo(ICallBack iCallBack) throws RemoteException {
			Log.i(TAG, "startServerDemo  service----------------------");
			mCallBack = iCallBack;
			Intent intent = new Intent();
			intent.setAction(START_ACTION);
			intent.putExtra(ACTION, ACTION_INIT);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {

			String[] packages = RunBackgroundService.this.getPackageManager()
					.getPackagesForUid(getCallingUid());
			if (packages != null && packages.length > 0) {
				packageName = packages[0];
				Log.i(TAG, "onTransact  service----------------------");
				Log.i(TAG, "onTransact  packageName=" + packageName);
			}
			return super.onTransact(code, data, reply, flags);
		}

		@Override
		public void invokCallBack(int result) throws RemoteException {
			// TODO Auto-generated method stub
			Log.i(TAG, "invokCallBack-----------result----------" + result);
			mCallBack.handleByServer(result);
		}

	};

}
