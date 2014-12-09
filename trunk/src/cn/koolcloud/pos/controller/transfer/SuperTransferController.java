package cn.koolcloud.pos.controller.transfer;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ActionMode;
import android.view.ActionMode.Callback;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CardSwiper;
import cn.koolcloud.pos.external.CardSwiper.CardSwiperListener;
import cn.koolcloud.pos.util.UtilForMoney;

public class SuperTransferController extends BaseController implements View.OnClickListener, OnTouchListener, CardSwiperListener {
	private static final String TAG = "SuperTransferController";
	
	private static final int HANDLE_TRACK_DATA = 0;
	private static final int ID_CARD_MIM_LENGTH = 12;
	
	private EditText keyboardScreenEditText;
	private EditText fromAccountEditText;
	private EditText toAccountEditText;
	private EditText idCardEditText;
	private EditText amountEditText;
	private ImageView btnXImageView;
	private Button btnConfirm;
	
	private int currentEditText;
	private int position = 0;
	private static final int MAX_COUNT = 4;
	private boolean removeJSTag = true;
	private CardSwiper mCardSwiper;
	private EditText currentView = null;

    private String track2 = "";
    private String track3 = "";
	
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
		currentView = fromAccountEditText;
		
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
		
		btnConfirm = (Button) findViewById(R.id.btnConfirm);
		btnConfirm.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		if (currentEditText == R.id.fromAccountEditText && mCardSwiper != null) {
			mCardSwiper.onStart();
		} else {
			onStartSwiper();
		}
		super.onResume();
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			
			switch (msg.what) {
			case HANDLE_TRACK_DATA:
				JSONObject jsObj = (JSONObject) msg.obj;
				if (currentEditText == R.id.fromAccountEditText) {
                    track2 = jsObj.optString("track2");
                    track3 = jsObj.optString("track3");
					fromAccountEditText.setText("");
					fromAccountEditText.setText(jsObj.optString("cardID"));
					if (!TextUtils.isEmpty(jsObj.optString("cardID"))) {
						int length = jsObj.optString("cardID").length();
						fromAccountEditText.setSelection(length, length);
					}
				}
				
				if (currentEditText == R.id.toAccountEditText) {
					toAccountEditText.setText("");
					toAccountEditText.setText(jsObj.optString("cardID"));
					if (!TextUtils.isEmpty(jsObj.optString("cardID"))) {
						int length = jsObj.optString("cardID").length();
						toAccountEditText.setSelection(length, length);
					}
				}
				
				initCalculateWithEditText(currentView);
				onStopSwiper();
				break;

			default:
				break;
			}
		}
		
	};

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
		return getString(R.string.controllerName_SuperTransfer);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_SuperTransfer);
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
	public void onClickBtnC(View view) {
		super.onClickBtnC(view);
		if (currentEditText == R.id.fromAccountEditText || currentEditText == R.id.toAccountEditText) {
			onStartSwiper();
		}
	}

	@Override
	public void onClickBtnOK(View view) {
		position++;
		position = position % MAX_COUNT;
		
		switch (position) {
		case 0:
			fromAccountEditText.requestFocus();
			currentEditText = R.id.fromAccountEditText;
			currentView = fromAccountEditText;
			onStartSwiper();
			break;
		case 1:
			toAccountEditText.requestFocus();
			currentEditText = R.id.toAccountEditText;
			currentView = toAccountEditText;
			onStopSwiper();
			onStartSwiper();
			break;
		case 2:
			idCardEditText.requestFocus();
			currentEditText = R.id.idCardEditText;
			currentView = idCardEditText;
			onStopSwiper();
			break;
		case 3:
			amountEditText.requestFocus();
			currentEditText = R.id.amountEditText;
			currentView = amountEditText;
			onStopSwiper();
			break;

		default:
			break;
		}
		
		initCalculateWithEditText(currentView);
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}
	
	@Override
	public void onBackPressed() {
		onCall("window.SuperTransfer.clear", null);
		onPause();
		super.onBackPressed();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.btnConfirm:
			JSONObject msg = new JSONObject();
			try {
				String amountStr = amountEditText.getText().toString().trim();
				if (amountStr.isEmpty() || 0 == Long.parseLong(UtilForMoney.yuan2fen(amountStr))) {
					return;
				}
				
				msg.put(getString(R.string.formData_key_transAmount), UtilForMoney.yuan2fen(amountStr));
				
				String fromAccountStr = fromAccountEditText.getText().toString().trim();
				if (TextUtils.isEmpty(fromAccountStr)) {
					return;
				}
				msg.put("fromAccount", fromAccountStr);
				
				String toAccountStr = toAccountEditText.getText().toString().trim();
				if (TextUtils.isEmpty(toAccountStr)) {
					return;
				}
				msg.put("toAccount", toAccountStr);
				
				String idCardStr = idCardEditText.getText().toString().trim();
				if (TextUtils.isEmpty(idCardStr) || idCardStr.length() < ID_CARD_MIM_LENGTH) {
					Toast.makeText(SuperTransferController.this, getResources().getString(R.string.msg_idcard_error), Toast.LENGTH_SHORT).show();
					return;
				}
				msg.put("idCard", idCardStr);
				msg.put("track2", track2);
				msg.put("track3", track3);

				onCall("window.SuperTransfer.onCompleteInput", msg);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
//			finish();
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
			}
			
			switch (view.getId()) {
			case R.id.fromAccountEditText:
				position = 0;
				if (mCardSwiper != null) {
					mCardSwiper.onStart();
				}
				currentView = fromAccountEditText;
				break;
			case R.id.toAccountEditText:
				position = 1;
				if (mCardSwiper != null) {
					mCardSwiper.onPause();
					mCardSwiper.onStart();
				}
				currentView = toAccountEditText;
				break;
			case R.id.idCardEditText:
				position = 2;
				onStopSwiper();
				currentView = idCardEditText;
				break;
			case R.id.amountEditText:
				position = 3;
				onStopSwiper();
				currentView = amountEditText;
				break;
			default:
				break;
			}
			
			
			initCalculateWithEditText(mEditText);
		}
		
		return true;
	}
	
	private void initCalculateWithEditText(EditText mEditText) {
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
	
	@Override
	protected void onPause() {
		if (mCardSwiper != null) {
			mCardSwiper.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mCardSwiper != null) {
			mCardSwiper.onDestroy();
			mCardSwiper = null;
		}
		super.onDestroy();
	}

	private void onStartSwiper() {
		if (mCardSwiper == null) {
			mCardSwiper = new CardSwiper();
			mCardSwiper.onCreate(this, this);
		}
		mCardSwiper.onStart();
	}

	private void onStopSwiper() {
		if (mCardSwiper != null) {
			mCardSwiper.onPause();
		}
	}

	@Override
	public void onRecvTrackData(Hashtable<String, String> trackData) {
		JSONObject jsObj = new JSONObject();
		try {
			Enumeration<String> keys = trackData.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				jsObj.putOpt(key, trackData.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
        UtilFor8583.getInstance().trans.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
		Message msg = mHandler.obtainMessage();
		msg.obj = jsObj;
		msg.what = HANDLE_TRACK_DATA;
		msg.sendToTarget();
	}
}
