package cn.koolcloud.pos.controller.pay;

import java.util.Enumeration;
import java.util.Hashtable;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CardSwiper;
import cn.koolcloud.pos.external.CardSwiper.CardSwiperListener;
import cn.koolcloud.pos.external.CodeScanner;
import cn.koolcloud.pos.external.CodeScanner.CodeScannerListener;
import cn.koolcloud.pos.external.SoundWave;
import cn.koolcloud.pos.external.SoundWave.SoundWaveListener;

public class PayAccountController extends BaseController implements
		CardSwiperListener, SoundWaveListener, CodeScannerListener {
	protected LinearLayout layout_qrcode;
	protected LinearLayout layout_sound;
	protected LinearLayout layout_swiper;
	protected LinearLayout layout_keyboard;

	protected EditText et_id;

	private CardSwiper ex_cardSwiper;
	private CodeScanner ex_codeScanner;
	private SoundWave ex_soundWave;

	private View selectedBtn;
	private LinearLayout selectedLayout;

	protected String func_swipeCard;
	protected String func_inputAccount;
	protected String func_nearfieldAccount;
	private boolean removeJSTag = true;
	
	//muilti info bar components
	private TextView koolCloudMerchNumNameTextView;
	private TextView koolCloudMerchNumTextView;
	private TextView koolCloudDeviceNumNameTextView;
	private TextView koolCloudDeviceNumTextView;
	private TextView acquireNameTextView;
	private TextView acquireNickNameTextView;
	private TextView acquireMerchNameTextView;
	private TextView acquireMerchNumTextView;
	private TextView acquireTerminalTextView;
	private TextView acquireTerminalNumTextView;
	
//	private Typeface faceTypeLanTing;
	
	private JSONObject data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}

		data = formData.optJSONObject(getString(R.string.formData_key_data));

		layout_qrcode = (LinearLayout) findViewById(R.id.pay_account_layout_qrcode);
		layout_sound = (LinearLayout) findViewById(R.id.pay_account_layout_sound);
		layout_swiper = (LinearLayout) findViewById(R.id.pay_account_layout_swiper);
		layout_keyboard = (LinearLayout) findViewById(R.id.pay_account_layout_keyboard);

		et_id = (EditText) findViewById(R.id.pay_account_et_id);
		setCurrentNumberEditText(et_id);

		TextView tv_amount = (TextView) findViewById(R.id.pay_account_tv_amount);
		tv_amount.setText(data.optString("transAmount"));

		func_swipeCard = data.optString("swipeCard");
		func_inputAccount = data.optString("inputAccount");
		func_nearfieldAccount = data.optString("nearfieldAccount");

		initBtnStatus(R.id.pay_account_btn_swiper, data.optInt("btn_swipe", -1));
		initBtnStatus(R.id.pay_account_btn_keyboard,
				data.optInt("btn_input", -1));
		initBtnStatus(R.id.pay_account_btn_sound, data.optInt("btn_sound", -1));
		initBtnStatus(R.id.pay_account_btn_qrcode,
				data.optInt("btn_qrcode", -1));

		if (findViewById(R.id.pay_account_btn_qrcode).isEnabled()) {
			ex_codeScanner = new CodeScanner();
			ex_codeScanner.onCreate(PayAccountController.this,
					PayAccountController.this);
		}
		
//		faceTypeLanTing = Typeface.createFromAsset(getAssets(), "font/fzltxhk.ttf");
		findViews();
		setTitle(formData.optString(getString(R.string.formData_key_title)));
	}
	
	private void findViews() {
		koolCloudMerchNumNameTextView = (TextView) findViewById(R.id.koolCloudMerchNumNameTextView);
//		koolCloudMerchNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView = (TextView) findViewById(R.id.koolCloudMerchNumTextView);
//		koolCloudMerchNumTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView.setText(data.optString("merchId"));
		koolCloudDeviceNumNameTextView = (TextView) findViewById(R.id.koolCloudDeviceNumNameTextView);
//		koolCloudDeviceNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView = (TextView) findViewById(R.id.koolCloudDeviceNumTextView);
//		koolCloudDeviceNumTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView.setText(data.optString("iposId"));
		acquireNameTextView = (TextView) findViewById(R.id.acquireNameTextView);
//		acquireNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView = (TextView) findViewById(R.id.acquireNickNameTextView);
//		acquireNickNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView.setText(data.optString("openBrhName"));
		acquireMerchNameTextView = (TextView) findViewById(R.id.acquireMerchNameTextView);
//		acquireMerchNameTextView.setTypeface(faceTypeLaTing);
		//check print type
		String printType = data.optString("printType");
		if (printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY)) {
			acquireMerchNameTextView.setText(getResources().getString(R.string.bar_acquire_merch_msg_pid));
		}
		acquireMerchNumTextView = (TextView) findViewById(R.id.acquireMerchNumTextView);
//		acquireMerchNumTextView.setTypeface(faceTypeLanTing);
		acquireMerchNumTextView.setText(data.optString("brhMchtId"));
		acquireTerminalTextView = (TextView) findViewById(R.id.acquireTerminalTextView);
		if (printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY)) {
			acquireTerminalTextView.setText(getResources().getString(R.string.bar_acquire_terminal_msg_beneficiary_account_no));
		}
//		qcquireTerminalTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView = (TextView) findViewById(R.id.acquireTerminalNumTextView);
//		acquireTerminalNumTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView.setText(data.optString("brhTermId"));
	}

	private void initBtnStatus(int id, int status) {
		Button view = (Button) findViewById(id);

		switch (status) {
		case -1:
			view.setEnabled(false);
//			view.setBackgroundResource(R.drawable.btn_account_grey);
			setDisableBtn(view);
			break;
		case 0:
			view.setEnabled(true);
			break;
		case 1:
			view.setEnabled(true);
			onSwitchAccount(view);
			break;
		default:
			break;
		}
	}
	
	private void setDisableBtn(Button btn) {
		switch (btn.getId()) {
		case R.id.pay_account_btn_swiper:
			Drawable swiperDrawable = getResources().getDrawable(R.drawable.icon_swiper_disable);
			swiperDrawable.setBounds(0, 0, swiperDrawable.getMinimumWidth(), swiperDrawable.getMinimumHeight());
			btn.setCompoundDrawables(swiperDrawable, null, null, null);
			btn.setTextColor(getResources().getColor(R.color.trade_statistics_textcolor_gray));
			break;
		case R.id.pay_account_btn_keyboard:
			Drawable keyboadDrawable = getResources().getDrawable(R.drawable.icon_keyboard_disable);
			keyboadDrawable.setBounds(0, 0, keyboadDrawable.getMinimumWidth(), keyboadDrawable.getMinimumHeight());
			btn.setCompoundDrawables(keyboadDrawable, null, null, null);
			btn.setTextColor(getResources().getColor(R.color.trade_statistics_textcolor_gray));
			break;
		case R.id.pay_account_btn_sound:
			Drawable soundDrawable = getResources().getDrawable(R.drawable.icon_sound_disable);
			soundDrawable.setBounds(0, 0, soundDrawable.getMinimumWidth(), soundDrawable.getMinimumHeight());
			btn.setCompoundDrawables(soundDrawable, null, null, null);
			btn.setTextColor(getResources().getColor(R.color.trade_statistics_textcolor_gray));
			break;
		case R.id.pay_account_btn_qrcode:
			Drawable qrcodeDrawable = getResources().getDrawable(R.drawable.icon_qrcode_disable);
			qrcodeDrawable.setBounds(0, 0, qrcodeDrawable.getMinimumWidth(), qrcodeDrawable.getMinimumHeight());
			btn.setCompoundDrawables(qrcodeDrawable, null, null, null);
			btn.setTextColor(getResources().getColor(R.color.trade_statistics_textcolor_gray));
			break;

		default:
			break;
		}
	}

	public void onSwitchAccount(View view) {
		if (selectedBtn == view) {
			return;
		}
		String tag = view.getTag().toString();
		String preTag = "-1";

		if (selectedBtn != null) {
			selectedBtn.setSelected(false);
			preTag = selectedBtn.getTag().toString();
		}

		view.setSelected(true);
		selectedBtn = view;

		if (selectedLayout != null) {
			selectedLayout.setVisibility(View.GONE);
		} else {
			layout_qrcode.setVisibility(View.GONE);
			layout_sound.setVisibility(View.GONE);
			layout_swiper.setVisibility(View.GONE);
			layout_keyboard.setVisibility(View.GONE);
		}

		String actionTag = getString(R.string.pay_account_tag_qrcode);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_qrcode.setVisibility(View.VISIBLE);
			onStartQRScanner();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopQRScanner();
		}

		actionTag = getString(R.string.pay_account_tag_sound);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_sound.setVisibility(View.VISIBLE);
			onStartSound();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopSound();
		}

		actionTag = getString(R.string.pay_account_tag_swiper);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_swiper.setVisibility(View.VISIBLE);
			onStartSwiper();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopSwiper();
		}

		actionTag = getString(R.string.pay_account_tag_keyboard);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_keyboard.setVisibility(View.VISIBLE);
			onStartKeyBoard();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopKeyBoard();
		}
	}

	private void onStartSwiper() {
		if (ex_cardSwiper == null) {
			ex_cardSwiper = new CardSwiper();
			ex_cardSwiper.onCreate(this, this);
		}
		ex_cardSwiper.onStart();
	}

	private void onStopSwiper() {
		if (ex_cardSwiper != null) {
			ex_cardSwiper.onPause();
		}
	}

	private void onStartQRScanner() {
		if (ex_codeScanner == null) {
			ex_codeScanner = new CodeScanner();
			ex_codeScanner.onCreate(PayAccountController.this,
					PayAccountController.this);

			mainHandler.post(new Runnable() {

				@Override
				public void run() {
				}
			});
		} else {
			ex_codeScanner.onResume();
		}
	}

	private void onStopQRScanner() {
		if (ex_codeScanner != null) {
			ex_codeScanner.onPause();
		}
	}

	private void onStartSound() {
		if (ex_soundWave == null) {
			ex_soundWave = new SoundWave();
			ex_soundWave.onCreate(this, this);
		}
		ex_soundWave.onStart();
	}

	private void onStopSound() {
		if (ex_soundWave != null) {
			ex_soundWave.onPause();
		}
	}

	private void onStartKeyBoard() {

	}

	private void onStopKeyBoard() {

	}

	private boolean isPause = false;

	@Override
	protected void onPause() {
		isPause = true;
		if (ex_cardSwiper != null) {
			ex_cardSwiper.onPause();
		}
		if (ex_soundWave != null) {
			ex_soundWave.onPause();
		}
		if (ex_codeScanner != null) {
			ex_codeScanner.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if (!isPause) {
			super.onResume();
			return;
		} else {
			isPause = false;
		}
		Log.d(TAG, "NearFieldController onResume");
		if (ex_cardSwiper != null) {
			ex_cardSwiper.onStart();
		}
		if (ex_soundWave != null) {
			ex_soundWave.onStart();
		}
		if (ex_codeScanner != null) {
			ex_codeScanner.onResume();
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this + "onDestroy");

		if (ex_cardSwiper != null) {
			ex_cardSwiper.onDestroy();
			ex_cardSwiper = null;
		}
		if (ex_soundWave != null) {
			ex_soundWave.onDestroy();
			ex_soundWave = null;
		}
		if (ex_codeScanner != null) {
			ex_codeScanner.onDestroy();
			ex_codeScanner = null;
		}
		super.onDestroy();
	};

	@Override
	public void onBackPressed() {
		onCall("PayAccount.clear", null);
		super.onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_pay_account_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_input_ap_account_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_PayAccount);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_PayAccount);
	}

	@Override
	public void onRecvTrackData(Hashtable<String, String> trackData) {
		JSONObject msg = new JSONObject();
		try {
			Enumeration<String> keys = trackData.keys();
			while (keys.hasMoreElements()) {
				String key = keys.nextElement();
				msg.putOpt(key, trackData.get(key));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		onCall(func_swipeCard, msg);
	}

	@Override
	public void onRecvData(byte[] receivedBytes) {
		Log.d(TAG, "receivedBytes length : " + receivedBytes.length);
		int analyzingIndex = 0;
		String receivedData = "";
		receivedData = new String(receivedBytes);
		Log.d(TAG, "NearFieldController receivedData : " + receivedData);
		JSONObject transData = new JSONObject();

		if (!receivedData.isEmpty()) {
			try {
				transData.put(getString(R.string.formData_key_payData_field0),
						receivedData);
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
			onStopQRScanner();
			Log.d(TAG, "processReceivedData : " + transData.toString());
			onCall(func_nearfieldAccount, transData);
		}
	}

	@Override
	public void onClickBtnOK(View view) {
		String id = et_id.getText().toString();
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put(getString(R.string.formData_key_payData_field0), id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall(func_inputAccount, msg);
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
}
