package cn.koolcloud.pos.controller.dialogs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.ipos.appstore.service.aidl.IMSCService;
import cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.Logger;

public class CheckingUpdateDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private final static int DEAL_WITH_CHECKING_APP_VERSION = 0;
	private TextView titleTextView;
	private TextView msgBodyTextView;
	private TextView msgBodyWaitingTextView;
	private TextView dialogTitleTextView;
	private ImageView dialogTitleImageView;
	private Button okButton;
	
	
	protected ParcelableApp localParcelableApp;
	protected IMSCService mIService;
	
	protected ServiceConnection connection = new ServiceConnection() {
		  
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get AIDL instance from remote service.  
            mIService = IMSCService.Stub.asInterface(service);
            Logger.i("Bind Checking App Version Service Successfull");
            new CheckUpdateThread().start();
        }
  
        public void onServiceDisconnected(ComponentName name) {
            mIService = null;
            Logger.i("Checking App Version Service Disconnected");
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
		setContentView(R.layout.dialog_soft_update_layout);
		initViews();
		Logger.d("CheckingUpdateDialog onCreate");
		//bind MSC service
		Intent service = new Intent(IMSCService.class.getName());
        bindService(service, connection, BIND_AUTO_CREATE);
	}

	private void initViews() {
		titleTextView = (TextView) findViewById(R.id.dialog_title_text);
		msgBodyWaitingTextView = (TextView) findViewById(R.id.dialog_common_waiting_text);
		titleTextView.setText(getResources().getString(R.string.str_check_update));
		dialogTitleTextView = (TextView) findViewById(R.id.dialogTitleTextView);
		dialogTitleImageView = (ImageView) findViewById(R.id.dialogTitleImageView);
		dialogTitleImageView.setImageResource(R.drawable.dialog_self_checking_update);
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		
		
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DEAL_WITH_CHECKING_APP_VERSION:
				msgBodyWaitingTextView.setVisibility(View.GONE);
				okButton.setVisibility(View.VISIBLE);
				StringBuffer strBuffer = new StringBuffer();
				if (null != localParcelableApp) {
//					strBuffer.append(getResources().getString(R.string.app_name) + "\n");
					strBuffer.append(getResources().getString(R.string.str_current_version_name) + Env.getVersionName(getApplicationContext()) + "\n");
					strBuffer.append(getResources().getString(R.string.str_new_version) + localParcelableApp.getVersion() + "\n");
					strBuffer.append(getResources().getString(R.string.about_info) + "\n");
					okButton.setText(getResources().getString(R.string.str_update));
					
				} else {
//					strBuffer.append(getResources().getString(R.string.app_name) + "\n");
					strBuffer.append(getResources().getString(R.string.str_current_version_name) + Env.getVersionName(getApplicationContext()) + "\n");
					strBuffer.append(getResources().getString(R.string.str_new_version) + Env.getVersionName(getApplicationContext()) + "\n");
					strBuffer.append(getResources().getString(R.string.about_info) + "\n");
					okButton.setText(getResources().getString(R.string.alert_btn_positive));
				}
				dialogTitleTextView.setText(getResources().getString(R.string.app_name));
				msgBodyTextView.setText(strBuffer.toString());
				break;

			default:
				break;
			}
		}		
	};
	
	private void startDeviceChecking() {
		Intent mIntent = new Intent(getApplicationContext(), DevicesCheckingDialog.class);
		startActivity(mIntent);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.ok:
			try {
				if (null != mIService && null != localParcelableApp) {
					mIService.openAppDetail(localParcelableApp);
				} else {
					//TODO: start devices checking
					startDeviceChecking();
					finish();
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * deal with not responding on clicking out side of dialog
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;  
	}
	
	/**
	 * <p>Description: checking update</p>
	 * <p>Copyright: Copyright (c) 2014</p>
	 * <p>Company: All In Pay</p>
	 * @author 		Teddy
	 * @date 		2014-6-3
	 * @version 	
	 */
	class CheckUpdateThread extends Thread {

		@Override
		public void run() {
			try {
				if (null != mIService) {
					
					localParcelableApp = mIService.checkUpdate(Env.getPackageName(getApplicationContext()), Env.getVersionCode(getApplicationContext()));
					if (localParcelableApp != null) {
						Logger.i("appId:" + localParcelableApp.getId()
								+ " appName:" + localParcelableApp.getName()
								+ " appVersion:" + localParcelableApp.getVersion());
						
					}
					Message msg = mHandler.obtainMessage();
					msg.obj = localParcelableApp;
					msg.what = DEAL_WITH_CHECKING_APP_VERSION;
					mHandler.sendMessage(msg);
				}
			} catch (RemoteException localRemoteException) {
				Logger.e(localRemoteException.getMessage());
			}
		}
		
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		super.onDestroy();
	}
	
}
