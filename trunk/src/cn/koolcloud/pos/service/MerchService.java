package cn.koolcloud.pos.service;

import java.util.List;
import java.util.Map;

import android.app.IntentService;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.util.UtilForDataStorage;

public class MerchService extends IntentService {
	protected final String TAG = "MerchService";

	public final static String ACTION = "ex_action";
	public final static int RESULT_CODE = 100010;
	public final static String ACTION_PAY = "pay";
	public final static String ACTION_LOGIN = "login";
	public final static String ACTION_LOGOUT = "logout";
	public final static String ACTION_MERCH_INFO = "MERCH_INFO";

	private Intent payExIntent;

	private MerchInfo merchInfo;
	private String loginStatus;

	private RemoteCallbackList<IMerchCallBack> myCallbacks;

	public MerchService() {
		super("MerchService");
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate");
		super.onCreate();
	}

	@Override
	public IBinder onBind(Intent intent) {
		Log.d(TAG, "onBind");
		return merchStub;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand");
		return START_STICKY;
	}

	public void onDestroy() {
		Log.d(TAG, "onDestroy");
		super.onDestroy();
	};

	private Intent getExIntent() {
		if (payExIntent == null) {
			payExIntent = new Intent();
			payExIntent.setAction("cn.koolcloud.pos.PayExScreen");
		}
		return payExIntent;
	}

	IMerchService.Stub merchStub = new IMerchService.Stub() {
		private boolean isSettingLogin = false;
		private boolean isSettingMerchInfo = false;

		public int logIn() {
			Log.d(TAG, "getLoginStatus");
			if (isSettingLogin) {
				return -1;
			}

			if (loginStatus != "0") {
				Intent intent = getExIntent();
				intent.putExtra(ACTION, ACTION_LOGIN);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				isSettingLogin = true;
				return -1;
			}
			return 0;
		}

		@Override
		public void setLoginStatus(String ls) throws RemoteException {
			loginStatus = ls;
			isSettingLogin = false;
		}

		@Override
		public MerchInfo getMerchInfo() throws RemoteException {
			/*
			 * if (loginStatus != "0") { logIn(); return null; }
			 * 
			 * if (isSettingMerchInfo) { return null; }
			 */

			if (merchInfo == null) {
				Map<String, ?> map = UtilForDataStorage
						.readPropertyBySharedPreferences(
								MyApplication.getContext(), "merchant");
				String mId = String.valueOf(map.get("merchId"));
				String tID = String.valueOf(map.get("machineId"));
				String merchName = String.valueOf(map.get("merchName"));

				if (TextUtils.isEmpty(merchName) || TextUtils.isEmpty(mId)
						|| TextUtils.isEmpty(tID)) {
					Intent intent = getExIntent();
					intent.putExtra(ACTION, ACTION_MERCH_INFO);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
					isSettingMerchInfo = true;
				} else {
					merchInfo = new MerchInfo(merchName, mId, tID);
				}
				return null;
			}
			return merchInfo;
		}

		@Override
		public void setMerchInfo(MerchInfo mi) throws RemoteException {
			merchInfo = mi;
			isSettingMerchInfo = false;
		}

		@Override
		public void endCallPayEx() throws RemoteException {
			isSettingMerchInfo = false;
			isSettingLogin = false;

			if (myCallbacks != null) {

				int n = myCallbacks.beginBroadcast();
				try {
					for (int i = 0; i < n; i++) {
						if (null != myCallbacks.getBroadcastItem(i)) {
							myCallbacks.getBroadcastItem(i).setMerchInfo(
									merchInfo);
						}
					}
				} catch (RemoteException e) {
					Log.e(TAG, "", e);
				}
				myCallbacks.finishBroadcast();
			}
		}

		@Override
		public void registerCallback(IMerchCallBack cb) throws RemoteException {
			if (cb != null) {
				myCallbacks.register(cb);
			}
		}

		@Override
		public void unregisterCallback(IMerchCallBack cb)
				throws RemoteException {
			if (cb != null) {
				myCallbacks.unregister(cb);
			}
		}

		@Override
		public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
				throws RemoteException {
			// TODO Auto-generated method stub
			try {
				return super.onTransact(code, data, reply, flags);
			} catch (RuntimeException e) {
				Log.w("MerchService", "Unexpected remote exception", e);
				throw e;
			}
		}

		@Override
		public List<PaymentInfo> getPaymentInfos() throws RemoteException {
			CacheDB cacheDB = CacheDB.getInstance(MyApplication.getContext());
			
			List<PaymentInfo> paymentInfoList = cacheDB.selectAllPaymentInfo();
			return paymentInfoList;
		}

	};

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub

	}

}
