package cn.koolcloud.pos.controller.dialogs;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;

public class AlertCommonDialog extends Activity implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private TextView msgBodyTextView;
	private Button okButton;
	private Button cancelButton;
	
	private String msg;
	private String identifier;
	private String positiveText;
	private String negativeText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.alert_dialog_common_layout);
		msg = getIntent().getExtras().getString(ConstantUtils.MSG_KEY);
		identifier = getIntent().getExtras().getString(ConstantUtils.IDENTIFIER_KEY);
		positiveText = getIntent().getExtras().getString(ConstantUtils.POSITIVE_BTN_KEY);
		negativeText = getIntent().getExtras().getString(ConstantUtils.NEGATIVE_BTN_KEY);
		initViews();
	}

	private void initViews() {
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		
		msgBodyTextView.setText(msg);
		
		
		if (TextUtils.isEmpty(negativeText)) {
			negativeText = getResources().getString(R.string.alert_btn_negative);
			cancelButton.setVisibility(View.GONE);
		} else {
			cancelButton.setVisibility(View.VISIBLE);
		}
		cancelButton.setText(negativeText);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			onAlertClicked(identifier, false);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View view) {
		
		switch (view.getId()) {
		case R.id.ok:
			onAlertClicked(identifier, true);
			
			break;
		case R.id.cancel:
			onAlertClicked(identifier, false);
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
