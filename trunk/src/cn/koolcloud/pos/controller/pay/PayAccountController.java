package cn.koolcloud.pos.controller.pay;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CardSwiper;
import cn.koolcloud.pos.external.CardSwiper.CardSwiperListener;
import cn.koolcloud.pos.external.CodeScanner;
import cn.koolcloud.pos.external.CodeScanner.CodeScannerListener;
import cn.koolcloud.pos.external.SoundWave;
import cn.koolcloud.pos.external.SoundWave.SoundWaveListener;
import cn.koolcloud.pos.external.scanner.ZBarScanner;

import com.google.zxing.client.android.ScannerRelativeLayout;

public class PayAccountController extends BaseController implements
		CardSwiperListener, SoundWaveListener, CodeScannerListener,
		Camera.PreviewCallback {

	private final int PAY_ACOUNT_MAX_LENGTH = 20;
	private final int HANDLE_INIT_QRSCANNER = 1;
	private final int OPEN_CAMERA_DELAY_TIME = 50;
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
	private String transType;
	private boolean removeJSTag = true;

	private String misc;
	private ZBarScanner scanner;
	private ImageScanner imageScanner;
	private FrameLayout preview;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = true;
	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;
	private static final String ZBTAG = "alipay";

	// muilti info bar components
	private RelativeLayout barTitleLayout;
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
	private TextView amountMarkTextView;
	private TextView amountTextView;
	private ScannerRelativeLayout zscanner;

	private final String APMP_TRAN_PREAUTH = "1011";
	private final String APMP_TRAN_CONSUME = "1021";
	private final String APMP_TRAN_PRAUTHCOMPLETE = "1031";
	private final String APMP_TRAN_PRAUTHSETTLEMENT = "1091";
	private final String APMP_TRAN_PRAUTHCANCEL = "3011";
	private final String APMP_TRAN_CONSUMECANCE = "3021";
	private final String APMP_TRAN_PREAUTHCOMPLETECANCEL = "3031";
	private final String APMP_TRAN_REFUND = "3051";
	private final String APMP_TRAN_OFFSET = "4000";
	private final String APMP_TRAN_SIGNIN = "8011";
	private final String APMP_TRAN_SIGNOUT = "8021";
	private final String APMP_TRAN_BATCHSETTLE = "8031";

	private String transAmount;
	// private Typeface faceTypeLanTing;

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
		transType = data.optString("transType");
		misc = data.optString("misc");

		amountTextView = (TextView) findViewById(R.id.pay_account_tv_amount);
		if (transType.equals(APMP_TRAN_CONSUMECANCE)
				|| transType.equals(APMP_TRAN_PRAUTHCANCEL)
				|| transType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
			transAmount = "-" + data.optString("transAmount");
		} else {
			transAmount = data.optString("transAmount");
		}
		amountTextView.setText(transAmount);

		func_swipeCard = data.optString("swipeCard");
		func_inputAccount = data.optString("inputAccount");
		func_nearfieldAccount = data.optString("nearfieldAccount");
		
		preview = (FrameLayout) findViewById(R.id.scanner_zb);
		zscanner = (ScannerRelativeLayout) findViewById(R.id.scanner);
		
		if (misc != null && misc.equals(ZBTAG)) {
			/*preview = (FrameLayout) findViewById(R.id.scanner_zb);
			preview.setVisibility(View.VISIBLE);
			ScannerRelativeLayout zscanner = (ScannerRelativeLayout) findViewById(R.id.scanner);
			zscanner.setVisibility(View.GONE);
			scanner = new ZBarScanner(this);
			preview.addView(scanner.getMpreview());
			imageScanner = scanner.getMscanner();
			initBeepSound();*/
			
			preview.setVisibility(View.VISIBLE);
			
			zscanner.setVisibility(View.GONE);
		} else {
			preview.setVisibility(View.GONE);
			
			zscanner.setVisibility(View.VISIBLE);
			if (findViewById(R.id.pay_account_btn_qrcode).isEnabled()) {
				ex_codeScanner = new CodeScanner();
				ex_codeScanner.onCreate(PayAccountController.this,
						PayAccountController.this);
			}
		}
		initBtnStatus(R.id.pay_account_btn_swiper, data.optInt("btn_swipe", -1));
		initBtnStatus(R.id.pay_account_btn_keyboard,
				data.optInt("btn_input", -1));
		initBtnStatus(R.id.pay_account_btn_sound, data.optInt("btn_sound", -1));
		initBtnStatus(R.id.pay_account_btn_qrcode,
				data.optInt("btn_qrcode", -1));

		// faceTypeLanTing = Typeface.createFromAsset(getAssets(),
		// "font/fzltxhk.ttf");
		findViews();
		setTitle(formData.optString(getString(R.string.formData_key_title)));
	}

	private void findViews() {
		// hidden bar title on clicking searching result item
		barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
		amountMarkTextView = (TextView) findViewById(R.id.amountMarkTextView);
		String merchId = data.optString("merchId");
		String operationType = data.optString("operationType");

		if (TextUtils.isEmpty(merchId)) {
			barTitleLayout.setVisibility(View.INVISIBLE);
		}
		if (!TextUtils.isEmpty(operationType)) {
			amountMarkTextView.setVisibility(View.INVISIBLE);
			amountTextView.setVisibility(View.INVISIBLE);
		} else {
			amountMarkTextView.setVisibility(View.VISIBLE);
			amountTextView.setVisibility(View.VISIBLE);

		}
		koolCloudMerchNumNameTextView = (TextView) findViewById(R.id.koolCloudMerchNumNameTextView);
		// koolCloudMerchNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView = (TextView) findViewById(R.id.koolCloudMerchNumTextView);
		// koolCloudMerchNumTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView.setText(data.optString("merchId"));
		koolCloudDeviceNumNameTextView = (TextView) findViewById(R.id.koolCloudDeviceNumNameTextView);
		// koolCloudDeviceNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView = (TextView) findViewById(R.id.koolCloudDeviceNumTextView);
		// koolCloudDeviceNumTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView.setText(data.optString("iposId"));
		acquireNameTextView = (TextView) findViewById(R.id.acquireNameTextView);
		// acquireNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView = (TextView) findViewById(R.id.acquireNickNameTextView);
		// acquireNickNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView.setText(data.optString("openBrhName"));
		acquireMerchNameTextView = (TextView) findViewById(R.id.acquireMerchNameTextView);
		// acquireMerchNameTextView.setTypeface(faceTypeLaTing);
		// check print type
		String printType = data.optString("printType");
		if (printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY)) {
			acquireMerchNameTextView.setText(getResources().getString(
					R.string.bar_acquire_merch_msg_pid));
		}
		acquireMerchNumTextView = (TextView) findViewById(R.id.acquireMerchNumTextView);
		// acquireMerchNumTextView.setTypeface(faceTypeLanTing);
		acquireMerchNumTextView.setText(data.optString("brhMchtId"));
		acquireTerminalTextView = (TextView) findViewById(R.id.acquireTerminalTextView);
		if (printType.equals(ConstantUtils.PRINT_TYPE_ALIPAY)) {
			acquireTerminalTextView.setText(getResources().getString(
					R.string.bar_acquire_terminal_msg_beneficiary_account_no));
		}
		// qcquireTerminalTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView = (TextView) findViewById(R.id.acquireTerminalNumTextView);
		// acquireTerminalNumTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView.setText(data.optString("brhTermId"));
	}

	private void initBtnStatus(int id, int status) {
		Button view = (Button) findViewById(id);

		switch (status) {
		case -1:
			view.setEnabled(false);
			// view.setBackgroundResource(R.drawable.btn_account_grey);
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
			UtilFor8583.getInstance().trans
					.setEntryMode(ConstantUtils.ENTRY_QRCODE_MODE);
			// onStartQRScanner();
			if (misc != null && misc.equals(ZBTAG)) {
//				scanner.startScanner();
				mHandler.sendEmptyMessageDelayed(HANDLE_INIT_QRSCANNER, OPEN_CAMERA_DELAY_TIME);
			} else {
				onStartQRScanner();
			}
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			// onStopQRScanner();
			if (misc != null && misc.equals(ZBTAG)) {
				scanner.destroyedScanner();
			} else {
				onStopQRScanner();
			}
		}

		actionTag = getString(R.string.pay_account_tag_sound);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_sound.setVisibility(View.VISIBLE);
			// TODO add entryMode.
			onStartSound();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopSound();
		}

		actionTag = getString(R.string.pay_account_tag_swiper);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_swiper.setVisibility(View.VISIBLE);
			UtilFor8583.getInstance().trans
					.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
			onStartSwiper();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopSwiper();
		}

		actionTag = getString(R.string.pay_account_tag_keyboard);
		if (tag.equalsIgnoreCase(actionTag)) {
			layout_keyboard.setVisibility(View.VISIBLE);
			UtilFor8583.getInstance().trans
					.setEntryMode(ConstantUtils.ENTRY_KEYBOARD_MODE);
			onStartKeyBoard();
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopKeyBoard();
		}
	}
	
	Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_INIT_QRSCANNER:
				preview = (FrameLayout) findViewById(R.id.scanner_zb);
				preview.setVisibility(View.VISIBLE);
				ScannerRelativeLayout zscanner = (ScannerRelativeLayout) findViewById(R.id.scanner);
				zscanner.setVisibility(View.GONE);
				scanner = new ZBarScanner(PayAccountController.this);
				preview.addView(scanner.getMpreview());
				imageScanner = scanner.getMscanner();
				initBeepSound();
				
				scanner.startScanner();
				
				break;
			default:
				break;
			}
		}
		
	};

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

	/**
	 * 初始化声音
	 */
	private void initBeepSound() {
		if (playBeep && mediaPlayer == null) {
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);
			AssetFileDescriptor file = getResources().openRawResourceFd(
					R.raw.beep);
			try {
				mediaPlayer.setDataSource(file.getFileDescriptor(),
						file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			} catch (IOException e) {
				mediaPlayer = null;
			}
		}
	}

	/**
	 * 播放声音和震动
	 */
	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
	}

	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	@Override
	protected void addInputNumber(String text) {
		if (et_id.getText().length() < PAY_ACOUNT_MAX_LENGTH) {
			super.addInputNumber(text);
		}
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
		// if (ex_codeScanner != null) {
		// ex_codeScanner.onPause();
		// }
		if (misc != null && misc.equals(ZBTAG)) {
			if (scanner != null) {
				scanner.destroyedScanner();
			}
		} else {
			if (ex_codeScanner != null) {
				ex_codeScanner.onPause();
			}
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
		// if (ex_codeScanner != null) {
		// ex_codeScanner.onResume();
		// }
		if (misc != null && misc.equals(ZBTAG)) {
			if (scanner != null) {
				scanner.startScanner();
			}
		} else {
			if (ex_codeScanner != null) {
				ex_codeScanner.onResume();
			}
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
		// if (ex_codeScanner != null) {
		// ex_codeScanner.onDestroy();
		// ex_codeScanner = null;
		// }
		if (misc != null && misc.equals(ZBTAG)) {
			if (scanner != null) {
				scanner.destroyedScanner();
			}
		} else {
			if (ex_codeScanner != null) {
				ex_codeScanner.onDestroy();
				ex_codeScanner = null;
			}
		}
		super.onDestroy();
	};

	@Override
	public void onClickLeftButton(View view) {
		// TODO Auto-generated method stub
		onPause();
		super.onClickLeftButton(view);
	}

	@Override
	public void onBackPressed() {
		onCall("PayAccount.clear", null);
		onPause();
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
	public void onPreviewFrame(byte[] data, Camera camera) {
		// TODO Auto-generated method stub
		Camera.Parameters parameters = camera.getParameters();
		Camera.Size size = parameters.getPreviewSize();

		Image barcode = new Image(size.width, size.height, "Y800");
		barcode.setData(data);

		int result = imageScanner.scanImage(barcode);

		if (result != 0) {
			playBeepSoundAndVibrate();// 播放声音代表成功获取二维码
			SymbolSet syms = imageScanner.getResults();
			for (Symbol sym : syms) {
				String symData = sym.getData();
				if (!TextUtils.isEmpty(symData)) {
					JSONObject transData = new JSONObject();
					try {
						transData
								.put(getString(R.string.formData_key_payData_field0),
										symData);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					scanner.stopScanner();
					Log.d(TAG, "processReceivedData : " + transData.toString());
					onCall(func_nearfieldAccount, transData);
					break;
				}
			}
		}
		// textView.setText("" + data.toString());
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
