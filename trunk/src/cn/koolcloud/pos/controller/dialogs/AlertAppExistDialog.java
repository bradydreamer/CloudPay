package cn.koolcloud.pos.controller.dialogs;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.ipos.appstore.service.aidl.IMSCService;
import cn.koolcloud.ipos.appstore.service.aidl.ParcelableApp;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.HostMessage;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.Logger;

public class AlertAppExistDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private TextView msgBodyTextView;
	private Button okButton;
	private Button cancelButton;
	
	private String msg;
	private String identifier;
	private String positiveText;
	private ParcelableApp parcelableApp = null;
	private String transAmount;
	private String packageName;

    private IMSCService iMSCService;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            iMSCService = null;
            Logger.i("Checking App Service Disconnected");
            initViews();
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            iMSCService = IMSCService.Stub.asInterface(service);
            try {
                parcelableApp = iMSCService.checkUpdate(packageName, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Logger.i("Checking App Service Connected");
            initViews();
        }
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alert_dialog_common_layout);
		msg = getIntent().getExtras().getString(ConstantUtils.MSG_KEY);
		identifier = getIntent().getExtras().getString(ConstantUtils.IDENTIFIER_KEY);
		positiveText = getIntent().getExtras().getString(ConstantUtils.POSITIVE_BTN_KEY);
		transAmount = getIntent().getExtras().getString("transAmount");
		packageName = getIntent().getExtras().getString("packageName");

        //bind app store service
        Intent intent = new Intent(IMSCService.class.getName());
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

	private void initViews() {
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);

        if (null != parcelableApp) {
            msgBodyTextView.setText(Env.getResourceString(this, R.string.str_coupon_app_exist_install));
        } else {
            msgBodyTextView.setText(Env.getResourceString(this, R.string.str_coupon_app_not_installed));
        }

        cancelButton.setText(Env.getResourceString(this, R.string.alert_btn_negative));
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			goBackHome();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.ok:
            if (null != parcelableApp) {

                try {
                    iMSCService.openAppDetail(parcelableApp);

                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            goBackHome();
			break;
		case R.id.cancel:
			goBackHome();
			break;
		default:
			break;
		}
		finish();
	}

	/**
	 * deal with not responding on clicking out side of dialog
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;  
	}
	
	public void callBack(String callBackHandler, Object data) {
		JavaScriptEngine jsEngine = ClientEngine.engineInstance().javaScriptEngine();
		
		jsEngine.responseCallback(callBackHandler, data);
	}
	
	private void goBackHome() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		if (js != null) {
			js.callJsHandler("window.util.backHome", null);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        if (conn != null) {
            unbindService(conn);
        }
        iMSCService = null;
	}
}
