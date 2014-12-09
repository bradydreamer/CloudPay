package cn.koolcloud.pos.controller.dialogs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.ipos.appstore.service.aidl.IMSCService;
import cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.util.Logger;

public class ServiceCheckingUpdateDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private final static int DEAL_WITH_CHECKING_APP_VERSION = 0;
	private TextView titleTextView;
	private TextView msgBodyTextView;
	private TextView msgBodyWaitingTextView;
	private TextView dialogTitleTextView;
	private ImageView dialogTitleImageView;
	private Button okButton;
	private Button cancelButton;
	
	
	protected ParcelableApp localParcelableApp;
	protected String appUpdateInfo;
	protected IMSCService mIService;
	
	protected ServiceConnection connection = new ServiceConnection() {
		  
        public void onServiceConnected(ComponentName name, IBinder service) {
            // get AIDL instance from remote service.  
            mIService = IMSCService.Stub.asInterface(service);
            Logger.i("Bind Checking App Version Service Successfull");
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
		localParcelableApp = getIntent().getExtras().getParcelable(ConstantUtils.SER_KEY);
		appUpdateInfo = getIntent().getStringExtra(ConstantUtils.UPDATE_INFO_KEY);
		
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
		
		/*cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setText(getResources().getString(R.string.alert_btn_negative));
		cancelButton.setVisibility(View.VISIBLE);
		cancelButton.setOnClickListener(this);*/
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		
		msgBodyWaitingTextView.setVisibility(View.GONE);
		okButton.setVisibility(View.VISIBLE);
		dialogTitleTextView.setText(getResources().getString(R.string.app_name));
		msgBodyTextView.setText(appUpdateInfo);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.ok:
			try {
				if (null != mIService && null != localParcelableApp) {
					mIService.openAppDetail(localParcelableApp);
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		case R.id.cancel:
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}

	@Override
	protected void onDestroy() {
		unbindService(connection);
		super.onDestroy();
	}
	
}
