package cn.koolcloud.pos.controller.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cn.koolcloud.pos.R;

public class PushMessageController extends Activity implements View.OnClickListener {
	
	private TextView titleTextView;
	private TextView dialogDescriptionTextView;
	private TextView dialogTitleTextView;
	private ImageView dialogTitleImageView;
	private Button okButton;
	
	private String title = "";
	private String description = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.dialog_push_notification_layout);
		title = getIntent().getStringExtra("title");
		description = getIntent().getStringExtra("description");
		initViews();
	}

	private void initViews() {
		
		titleTextView = (TextView) findViewById(R.id.dialog_title_text);
		titleTextView.setText(getResources().getString(R.string.str_bulletin_msg));
		dialogTitleImageView = (ImageView) findViewById(R.id.dialogTitleImageView);
		dialogTitleImageView.setImageResource(R.drawable.dialog_notification_logo);
		
		dialogTitleTextView = (TextView) findViewById(R.id.dialogTitleTextView);
		dialogTitleTextView.setText(title);
		dialogDescriptionTextView = (TextView) findViewById(R.id.dialog_common_text);
		dialogDescriptionTextView.setText(description);
		
		okButton = (Button) findViewById(R.id.ok);
		okButton.setOnClickListener(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.ok:
			finish();
			
			break;

		default:
			break;
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return true;
	}
	
	/**
	 * deal with not responding on clicking out side of dialog
	 */
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return false;  
	}

}
