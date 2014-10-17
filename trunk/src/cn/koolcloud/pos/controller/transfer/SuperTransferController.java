package cn.koolcloud.pos.controller.transfer;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.util.Log;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.EditText;
import android.widget.ImageView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForMoney;

public class SuperTransferController extends BaseController implements View.OnClickListener, OnTouchListener {
	private static final String TAG = "SuperTransferController";
	
	private EditText keyboardScreenEditText;
	private EditText fromAccountEditText;
	private EditText toAccountEditText;
	private EditText idCardEditText;
	private EditText amountEditText;
	private ImageView btnXImageView;
	
	private int currentEditText;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViews();
		
	}

	private void findViews() {
		keyboardScreenEditText = (EditText) findViewById(R.id.keyboardScreenEditText);
		//set font for EditText
		Typeface faceType = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
		keyboardScreenEditText.setTypeface(faceType);
		
		ActionModeCallback actionModeCallback = new ActionModeCallback();
		
		fromAccountEditText = (EditText) findViewById(R.id.fromAccountEditText);
		fromAccountEditText.setOnTouchListener(this);
		fromAccountEditText.setLongClickable(false);
		fromAccountEditText.setCustomSelectionActionModeCallback(actionModeCallback);
		currentEditText = fromAccountEditText.getId();
		
		toAccountEditText = (EditText) findViewById(R.id.toAccountEditText);
		toAccountEditText.setOnTouchListener(this);
		toAccountEditText.setLongClickable(false);
		toAccountEditText.setCustomSelectionActionModeCallback(actionModeCallback);
		
		idCardEditText = (EditText) findViewById(R.id.idCardEditText);
		idCardEditText.setOnTouchListener(this);
		idCardEditText.setLongClickable(false);
		idCardEditText.setCustomSelectionActionModeCallback(actionModeCallback);
		
		amountEditText = (EditText) findViewById(R.id.amountEditText);
		amountEditText.setOnTouchListener(this);
		amountEditText.setLongClickable(false);
		amountEditText.setCustomSelectionActionModeCallback(actionModeCallback);
		
		btnXImageView = (ImageView) findViewById(R.id.btnXImageView);
		
	}
	
	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_super_transfer_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_super_transfer_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return null;
	}
	
	protected void showInputNumber() {
		StringBuilder textBuilder = getNumberInputString();
		if (currentEditText == R.id.amountEditText) {
			
			if (null == textBuilder || 0 == textBuilder.length()) {
				keyboardScreenEditText.setText(null);
				amountEditText.setText(null);
			} else {
				String amount = textBuilder.toString();
				String text = UtilForMoney.fen2yuan(amount);
				keyboardScreenEditText.setText(text);
				amountEditText.setText(text);
			}
			int textLength = amountEditText.getText().length();
			amountEditText.setSelection(textLength, textLength);
		} else {
			keyboardScreenEditText.setText(textBuilder);
			int textLength = textBuilder.length();
			switch (currentEditText) {
			case R.id.fromAccountEditText:
				fromAccountEditText.setText(textBuilder.toString());
				fromAccountEditText.setSelection(textLength, textLength);
				break;
			case R.id.toAccountEditText:
				toAccountEditText.setText(textBuilder.toString());
				toAccountEditText.setSelection(textLength, textLength);
				
				break;
			case R.id.idCardEditText:
				idCardEditText.setText(textBuilder.toString());
				idCardEditText.setSelection(textLength, textLength);
				
				break;
			default:
				break;
			}
		}
		keyboardScreenEditText.setSelection(keyboardScreenEditText.getText().length(), keyboardScreenEditText.getText().length());
	}
	
	@Override
	protected void addInputNumber(String text) {
		if (currentEditText == R.id.amountEditText) {
			if (null != text && numberInputString.toString().length() < 12) {
				if (numberInputString.toString().equals("0")) {
					numberInputString.replace(0, 1, text);
				} else if (numberInputString.toString().equals("00")) {
					numberInputString.replace(0, 2, text);
				} else {
					numberInputString.append(text);
				}
			}
		} else {
			numberInputString.append(text);
		}
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		
	}

	@Override
	protected boolean getRemoveJSTag() {
		return false;
	}

	@Override
	public void onClick(View view) {
		Log.i(TAG, "view.getId(): " + view.getId());
		switch (view.getId()) {
		case R.id.fromAccountEditText:
			
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		EditText mEditText = (EditText) view;
		//set EditText don't show soft input keyboard
		int inType = mEditText.getInputType(); 				// backup the input type  
		mEditText.setInputType(InputType.TYPE_NULL); 		// disable soft input      
		mEditText.onTouchEvent(event); 						// call native handler      
		mEditText.setInputType(inType); 					// restore input type     
		mEditText.setSelection(mEditText.getText().length());
		
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			if (view.getId() != currentEditText) {
				currentEditText = view.getId();
//				clearInputNumber();
			}
			if (currentEditText == R.id.amountEditText) {
				clearInputNumber();
				btnXImageView.setClickable(false);
				keyboardScreenEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(13)});
			} else {
				btnXImageView.setClickable(true);
				keyboardScreenEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(999999999)});  
			}
			keyboardScreenEditText.setText(mEditText.getText());
			numberInputString = new StringBuilder(mEditText.getText().toString());
			keyboardScreenEditText.setSelection(mEditText.getText().length(), mEditText.getText().length());
			Log.i(TAG, "onTouch:" + view.getId());
		}
		
		return true;
	}
	
	//class for hidden select all, copy panel
	class ActionModeCallback implements Callback {

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			
		}
		
	}
}
