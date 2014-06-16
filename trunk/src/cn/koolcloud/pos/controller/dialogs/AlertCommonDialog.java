package cn.koolcloud.pos.controller.dialogs;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;

public class AlertCommonDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private TextView msgBodyTextView;
	private Button okButton;
	private Button cancelButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alert_dialog_common_layout);
		initViews();
		
	}

	private void initViews() {
		StringBuffer strBuffer = new StringBuffer();
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		
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
	
	private void onAlertClicked(String identifier, boolean isPositiveClicked) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("isPositiveClicked", isPositiveClicked);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		callBack(identifier, msg);
	}
	
	public void callBack(String callBackHandler, Object data) {
		JavaScriptEngine jsEngine = ClientEngine.engineInstance().javaScriptEngine();
		jsEngine.responseCallback(callBackHandler, data);
	}
	
	@Override
	protected void onDestroy() {
//		unbindService(connection);
		super.onDestroy();
	}
}
