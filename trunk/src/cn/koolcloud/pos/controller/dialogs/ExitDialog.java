package cn.koolcloud.pos.controller.dialogs;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.pos.controller.BaseHomeController;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.wd.R;

public class ExitDialog extends BaseHomeController implements View.OnClickListener {

	public static final int UPDATE_CLIENT_DIALOG_REQUEST = 1;
	private TextView titleTextView;
	private TextView msgBodyTextView;
//	private TextView dialogTitleTextView;
	private Button okButton;
	private Button cancelButton;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.dialog_exit_layout);
		initViews();
		
	}

	private void initViews() {
		StringBuffer strBuffer = new StringBuffer();
		
		titleTextView = (TextView) findViewById(R.id.dialog_title_text);
		titleTextView.setText(getResources().getString(R.string.str_exit));
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setVisibility(View.VISIBLE);
		okButton.setOnClickListener(this);
		
		cancelButton = (Button) findViewById(R.id.cancel);
		cancelButton.setOnClickListener(this);
		
		msgBodyTextView = (TextView) findViewById(R.id.dialog_common_text);
		strBuffer.append(getResources().getString(R.string.str_exit_message) + "\n");
		
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
			exit();
			break;
		case R.id.cancel:
			
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
	
	@Override
	protected void onDestroy() {
//		unbindService(connection);
		super.onDestroy();
	}

	@Override
	protected void setControllerContentView() {
		
	}

	@Override
	protected String getTitlebarTitle() {
		return null;
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return null;
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		
	}

	@Override
	protected boolean getRemoveJSTag() {
		return false;
	}
}
