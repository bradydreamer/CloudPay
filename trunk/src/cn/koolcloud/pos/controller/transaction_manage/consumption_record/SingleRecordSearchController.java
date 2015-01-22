package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.google.zxing.client.android.ScannerRelativeLayout;
import cn.koolcloud.jni.LedInterface;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CodeScanner;
import cn.koolcloud.zbar.client.android.InterfaceBarCode;
import cn.koolcloud.zbar.client.android.ZbarScannerRelativeLayout;

public class SingleRecordSearchController extends BaseController implements InterfaceBarCode,CodeScanner.CodeScannerListener,View.OnClickListener{

	public EditText et_id;
	private String paymentId;
	private boolean removeJSTag = true;

	protected LinearLayout layout_qrcode;
	protected LinearLayout layout_sound;
	protected RelativeLayout layout_swiper;
	protected LinearLayout layout_keyboard;
	private View selectedBtn;
	private Boolean scanerStarted = false;
	private String misc = null;
	private ScannerRelativeLayout zscanner;
	private ZbarScannerRelativeLayout zBarScannerLayout;
	private FrameLayout previewFrameLayout;
	private CodeScanner ex_codeScanner;
	private static final String ZBTAG = "alipay";
	private final int PAY_ACOUNT_MAX_LENGTH = 20;
	private final int HANDLE_INIT_QRSCANNER = 1;
	private final int OPEN_CAMERA_DELAY_TIME = 50;
	private boolean ledTag = false;
	private ImageView torchImageView;
	ScannerRelativeLayout srr;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		paymentId = data.optString("paymentId");
		misc = data.optString("misc");
		layout_qrcode = (LinearLayout) findViewById(R.id.pay_account_layout_qrcode);
		layout_sound = (LinearLayout) findViewById(R.id.pay_account_layout_sound);
		layout_swiper = (RelativeLayout) findViewById(R.id.pay_account_layout_swiper);
		layout_keyboard = (LinearLayout) findViewById(R.id.pay_account_layout_keyboard);
		layout_qrcode.setVisibility(View.GONE);
		layout_keyboard.setVisibility(View.GONE);
		layout_sound.setVisibility(View.GONE);
		layout_swiper.setVisibility(View.GONE);
		initMethodType();
		et_id = (EditText) findViewById(R.id.pay_account_et_id);
		setCurrentNumberEditText(et_id);
		torchImageView = (ImageView) findViewById(R.id.torchImageView);
		torchImageView.setOnClickListener(this);

	}

	private void initMethodType() {
		RelativeLayout rl = (RelativeLayout) findViewById(R.id.rootlayout);
		rl.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
		barTitleLayout.setVisibility(View.GONE);
		LinearLayout moneyAmountLayout = (LinearLayout) findViewById(R.id.money_amount_view);
		moneyAmountLayout.setVisibility(View.GONE);
		setDisableBtn((Button) findViewById(R.id.pay_account_btn_swiper));
		setDisableBtn((Button) findViewById(R.id.pay_account_btn_sound));
		previewFrameLayout = (FrameLayout) findViewById(R.id.scanner_zb);
		zscanner = (ScannerRelativeLayout) findViewById(R.id.scanner);

		if (misc != null && !misc.equals("")){
			if (misc.equals(ZBTAG)) {
				previewFrameLayout.setVisibility(View.VISIBLE);
				zscanner.setVisibility(View.GONE);
			} else {
				previewFrameLayout.setVisibility(View.GONE);
				zscanner.setVisibility(View.VISIBLE);

				if (findViewById(R.id.pay_account_btn_qrcode).isEnabled()) {
					ex_codeScanner = new CodeScanner();
					ex_codeScanner.onCreate(this, this);
				}
			}
		}
//		srr = (ScannerRelativeLayout)zscanner.getRootView();
		initZbarLib();
		((Button)findViewById(R.id.pay_account_btn_sound)).setEnabled(false);
		((Button)findViewById(R.id.pay_account_btn_swiper)).setEnabled(false);
		setDisableBtn((Button) findViewById(R.id.pay_account_btn_sound));
		setDisableBtn((Button)findViewById(R.id.pay_account_btn_swiper));
		onSwitchAccount((Button)findViewById(R.id.pay_account_btn_keyboard));

	}

	private void initZbarLib() {
		zBarScannerLayout = new ZbarScannerRelativeLayout(this);
		zBarScannerLayout.setBarCodeCallBack(this);
	}

	private void setDisableBtn(Button btn) {
		switch (btn.getId()) {
			case R.id.pay_account_btn_swiper:
				Drawable swiperDrawable = getResources().getDrawable(
						R.drawable.icon_swiper_disable);
				swiperDrawable.setBounds(0, 0, swiperDrawable.getMinimumWidth(),
						swiperDrawable.getMinimumHeight());
				btn.setCompoundDrawables(swiperDrawable, null, null, null);
				btn.setTextColor(getResources().getColor(
						R.color.trade_statistics_textcolor_gray));
				break;
			case R.id.pay_account_btn_keyboard:
				Drawable keyboadDrawable = getResources().getDrawable(
						R.drawable.icon_keyboard_disable);
				keyboadDrawable.setBounds(0, 0, keyboadDrawable.getMinimumWidth(),
						keyboadDrawable.getMinimumHeight());
				btn.setCompoundDrawables(keyboadDrawable, null, null, null);
				btn.setTextColor(getResources().getColor(
						R.color.trade_statistics_textcolor_gray));
				break;
			case R.id.pay_account_btn_sound:
				Drawable soundDrawable = getResources().getDrawable(
						R.drawable.icon_sound_disable);
				soundDrawable.setBounds(0, 0, soundDrawable.getMinimumWidth(),
						soundDrawable.getMinimumHeight());
				btn.setCompoundDrawables(soundDrawable, null, null, null);
				btn.setTextColor(getResources().getColor(
						R.color.trade_statistics_textcolor_gray));
				break;
			case R.id.pay_account_btn_qrcode:
				Drawable qrcodeDrawable = getResources().getDrawable(
						R.drawable.icon_qrcode_disable);
				qrcodeDrawable.setBounds(0, 0, qrcodeDrawable.getMinimumWidth(),
						qrcodeDrawable.getMinimumHeight());
				btn.setCompoundDrawables(qrcodeDrawable, null, null, null);
				btn.setTextColor(getResources().getColor(
						R.color.trade_statistics_textcolor_gray));
				break;
			default:
				break;
		}
	}
	public void onSwitchAccount(View view) {
		if (selectedBtn == view) {
			return;
		}
		//如果scanerStarted为true，表示摄像头还未启动完毕，所以禁止其它操作。
		if(scanerStarted){
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

		if(preTag.equals(getString(R.string.pay_account_tag_qrcode))){
			layout_qrcode.setVisibility(View.GONE);

		}else if(preTag.equals(getString(R.string.pay_account_tag_sound))){
			layout_sound.setVisibility(View.GONE);

		}else if(preTag.equals(getString(R.string.pay_account_tag_swiper))){
			layout_swiper.setVisibility(View.GONE);

		}else if(preTag.equals(getString(R.string.pay_account_tag_keyboard))){
			layout_keyboard.setVisibility(View.GONE);

		}

		if(tag.equalsIgnoreCase(getString(R.string.pay_account_tag_qrcode))){
			layout_qrcode.setVisibility(View.VISIBLE);
			zscanner.setVisibility(View.GONE);
			previewFrameLayout.setVisibility(View.VISIBLE);

			scanerStarted = true;
			mHandler.sendEmptyMessageDelayed(HANDLE_INIT_QRSCANNER,OPEN_CAMERA_DELAY_TIME);

		}else if(tag.equalsIgnoreCase(getString(R.string.pay_account_tag_sound))){
			layout_sound.setVisibility(View.VISIBLE);

		}else if(tag.equalsIgnoreCase(getString(R.string.pay_account_tag_swiper))){
			layout_swiper.setVisibility(View.VISIBLE);

		}else if(tag.equalsIgnoreCase(getString(R.string.pay_account_tag_keyboard))){
			layout_keyboard.setVisibility(View.VISIBLE);
		}
	}

	private void onStartQRScanner() {
		if (ex_codeScanner == null) {
			ex_codeScanner = new CodeScanner();
			ex_codeScanner.onCreate(this,this);
		} else {
			ex_codeScanner.onResume();
		}
		scanerStarted = false;
	}

	private void onStopQRScanner() {
		if (ex_codeScanner != null) {
			ex_codeScanner.onPause();
		}
	}

	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case HANDLE_INIT_QRSCANNER:
					if (misc != null && misc.equals(ZBTAG)) {
						startZbarScaner();
					} else{
						previewFrameLayout.setVisibility(View.GONE);
						zscanner.setVisibility(View.VISIBLE);
						onStartQRScanner();
					}
					break;
				default:
					break;
			}
		}

	};

	private void startZbarScaner(){
		zscanner.setVisibility(View.GONE);
		previewFrameLayout.setVisibility(View.VISIBLE);
		zBarScannerLayout.setCameraView(previewFrameLayout);
		zBarScannerLayout.startScan();
		scanerStarted = false;
	}

	private void stopZbarScaner(){
		if (zBarScannerLayout != null) {
			zBarScannerLayout.stopScan();
			previewFrameLayout.removeAllViews();
		}
	}

	@Override
	public void onBackPressed() {
		if (misc != null && !misc.equals("")) {
			if (misc.equals(ZBTAG)) {
				if (zBarScannerLayout != null) {
					zBarScannerLayout.stopScan();
					zBarScannerLayout = null;
				}
			} else {
				if (ex_codeScanner != null) {
					ex_codeScanner.onDestroy();
					ex_codeScanner = null;
				}
			}
		}
		closeLed();
		super.onBackPressed();
	}

	@Override
	protected void onPause() {
		if (misc != null && misc.equals(ZBTAG)) {
			stopZbarScaner();
		} else{
			onStopQRScanner();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		if(selectedBtn.getTag().toString().equalsIgnoreCase(getString(R.string.pay_account_tag_qrcode))){
			scanerStarted = true;
			mHandler.sendEmptyMessageDelayed(HANDLE_INIT_QRSCANNER,OPEN_CAMERA_DELAY_TIME);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this + "onDestroy");

		if (misc != null && !misc.equals("")) {
			if (misc.equals(ZBTAG)) {
				if (zBarScannerLayout != null) {
					zBarScannerLayout.stopScan();
					zBarScannerLayout = null;
				}
			} else {
				if (ex_codeScanner != null) {
					ex_codeScanner.onDestroy();
					ex_codeScanner = null;
				}
			}
		}
		closeLed();
		super.onDestroy();
	};

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.torchImageView:
				if (!ledTag) {
					LedInterface.open();
					LedInterface.set(LedInterface.LED_ON, LedInterface.CAMERA_LED);
					LedInterface.close();
					ledTag = true;
				} else {
					closeLed();
				}
				break;
		}
	}
	private void closeLed() {
		LedInterface.open();
		LedInterface.set(LedInterface.LED_OFF, LedInterface.CAMERA_LED);
		LedInterface.close();
		ledTag = false;
	}
	@Override
	public void onClickBtnOK(View view) {
		String id = et_id.getText().toString();
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("id", id);
			msg.put("paymentId", paymentId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("TransactionManageIndex.gotoSingleRecord", msg);
	}

	@Override
	protected void addInputNumber(String text) {
		if (null != text && numberInputString.toString().length() < 25) {
			numberInputString.append(text);
		}
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_pay_account_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_single_record_search_controller);
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
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}

	@Override
	public void getBarCodeData(String data) {
		stopZbarScaner();
		String id = data;
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("id", id);
			msg.put("paymentId", paymentId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("TransactionManageIndex.gotoSingleRecord", msg);
	}

	@Override
	public void onRecvData(byte[] receivedBytes) {
		String id = new String(receivedBytes);
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("id", id);
			msg.put("paymentId", paymentId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("TransactionManageIndex.gotoSingleRecord", msg);
	}
}
