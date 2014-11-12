package cn.koolcloud.pos;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import cn.koolcloud.pos.service.SmartIposRunBackground;
import cn.koolcloud.pos.wd.R;

public class AppInitScreen extends WelcomeScreen {
	private String action;

	public final static int SUCC = 0;
	public final static int FAIL = 1;
	public final static String ACTION = "ex_action";
	public final static int RESULT_CODE = 100010;

	public final static String ACTION_PAY = "pay";
	public final static String ACTION_LOGIN = "login";
	public final static String ACTION_LOGOUT = "logout";
	public final static String ACTION_REVERSE = "reverse";
	public final static String TAG = "AppInitScreen";
	public static final String STARTSERVICE = "cn.koolcloud.pos.service.RunBackgroundService";

	private SmartIposRunBackground sRunbackService;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Intent intent = getIntent();
		action = intent.getStringExtra(ACTION);
		String package_name = getCallingPackage();
		Log.i(TAG, "onCreate ----------- package_name:" + package_name);
		moveTaskToBack(true);
		Intent it = new Intent();
		it.setAction(AppInitScreen.STARTSERVICE);
		this.bindService(it, mServiceConnection, Service.BIND_AUTO_CREATE);
		super.onCreate(savedInstanceState);
		mainHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				initApp();
				startScene();
			}
		}, 1500);

	}

	protected void setContentView() {
		Log.i(TAG, "setContentView  ------------------");
		if (ACTION_PAY.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_REVERSE.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_LOGIN.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		} else if (ACTION_LOGOUT.equalsIgnoreCase(action)) {
			setContentView(R.layout.activity_loading_screen);
		}
	}

	ServiceConnection mServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			sRunbackService = SmartIposRunBackground.Stub.asInterface(service);

		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	};

	@Override
	protected void initApp() {
		Log.i(TAG, "initApp  ------------------");
		if (ClientEngine.engineInstance().getCurrentController() == null) {
			super.initApp();
		}
	}

	@Override
	protected void onStart() {
		moveTaskToBack(true);
		super.onStart();
	}

	@Override
	protected void startScene() {
		Log.i(TAG, "startScene  ------------------");
		AppInitManager aim = AppInitManager.getInstance();
		aim.Init(sRunbackService, this);
		aim.autoLogin();
	}

	@Override
	protected void onDestroy() {
		// this.unbindService(mServiceConnection);
		super.onDestroy();
		Log.i(TAG, "onDestroy()");
	}

}
