package cn.koolcloud.pos.controller.dialogs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.mispos.MisposController;
import cn.koolcloud.pos.util.Env;

public class AboutDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private TextView titleTextView;
	private TextView msgBodyTextView;
	private TextView dialogTitleTextView;
	private Button okButton;
//	private Button cancelButton;
	/*private ParcelableApp localParcelableApp;
	
	private IMSCService mIService;
	  
    private ServiceConnection connection = new ServiceConnection() {
  
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get AIDL instance from remote service.  
            mIService = IMSCService.Stub.asInterface(service);  
            Logger.i("Bind Success:" + mIService);  
        }
  
        public void onServiceDisconnected(ComponentName name) {
            mIService = null;
            Logger.i("onServiceDisconnected");
        }
    };*/
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_about_layout);
		initViews();
		
		//bind MSC service
//		Intent service = new Intent(IMSCService.class.getName());
//        bindService(service, connection, BIND_AUTO_CREATE);
	}

	private void initViews() {
		StringBuffer strBuffer = new StringBuffer();
		
		titleTextView = (TextView) findViewById(R.id.dialog_title_text);
		titleTextView.setText(getResources().getString(R.string.str_about));
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(this);
		
//		cancelButton = (Button) findViewById(R.id.cancel);
//		cancelButton.setOnClickListener(this);
		
		dialogTitleTextView = (TextView) findViewById(R.id.dialogTitleTextView);
//		dialogTitleTextView.setOnClickListener(this);
		dialogTitleTextView.setText(getResources().getString(R.string.app_name));
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		strBuffer.append(getResources().getString(R.string.str_version_name) + Env.getVersionName(AboutDialog.this) + "\n");
		strBuffer.append(getResources().getString(R.string.about_info) + "\n");
		
		msgBodyTextView.setText(strBuffer.toString());
		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.ok:
			finish();
			break;
		case R.id.cancel:
			/*if (null != localParcelableApp) {
				try {
					if (null != mIService) {
						mIService.openAppDetail(localParcelableApp);
					}
					finish();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			} else {
				checkAppUpdate(true);
			}*/
			break;
		case R.id.dialogTitleTextView:
			Intent mIntent = new Intent(this, MisposController.class);
			startActivity(mIntent);
			finish();
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
	 * @Title: checkAppUpdate
	 * @Description: TODO check update from service
	 * @param paramBoolean true show toast
	 * @return: void
	 */
	/*public void checkAppUpdate(boolean paramBoolean) {
		try {
			if (null != mIService) {
				
				localParcelableApp = mIService.checkUpdate(Env.getPackageName(AboutDialog.this), Env.getVersionCode(AboutDialog.this));
				if (localParcelableApp != null) {
					Logger.i("appId:" + localParcelableApp.getId()
							+ " appName:" + localParcelableApp.getName()
							+ " appVersion:" + localParcelableApp.getVersion());
					StringBuffer strBuffer = new StringBuffer();
					
					strBuffer.append(getResources().getString(R.string.app_name) + "\n");
					strBuffer.append(getResources().getString(R.string.str_version_name) + Env.getVersionName(AboutDialog.this) + "\n");
					strBuffer.append(getResources().getString(R.string.str_new_version) + localParcelableApp.getVersion() + "\n");
					
					msgBodyTextView.setText(strBuffer.toString());
					
					okButton.setText(getResources().getString(R.string.str_update_later));
					cancelButton.setText(getResources().getString(R.string.str_update_now));
					titleTextView.setText(getResources().getString(R.string.str_update));
				} else {
					if (paramBoolean) {
						Toast.makeText(AboutDialog.this, getResources().getString(R.string.already_latest_version), Toast.LENGTH_SHORT).show();
					}
					Logger.i(AboutDialog.this.getResources().getString(R.string.already_latest_version));
					finish();
				}
			} else {//service not exist or bind service failure
				Toast.makeText(AboutDialog.this, getResources().getString(R.string.str_install_appstore_first), Toast.LENGTH_SHORT).show();
				finish();
			}
		} catch (RemoteException localRemoteException) {
			Logger.e(localRemoteException.getMessage());
		}
	}*/
	
	@Override
	protected void onDestroy() {
//		unbindService(connection);
		super.onDestroy();
	}
}
