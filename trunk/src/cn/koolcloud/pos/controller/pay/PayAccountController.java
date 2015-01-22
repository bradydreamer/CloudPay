package cn.koolcloud.pos.controller.pay;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.zbar.Image;
import net.sourceforge.zbar.ImageScanner;
import net.sourceforge.zbar.Symbol;
import net.sourceforge.zbar.SymbolSet;

import org.json.JSONArray;
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
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.LedInterface;
import cn.koolcloud.parameter.EMVICData;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.HostMessage;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.Utility;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.external.CardSwiper;
import cn.koolcloud.pos.external.CardSwiper.CardSwiperListener;
import cn.koolcloud.pos.external.CodeScanner;
import cn.koolcloud.pos.external.CodeScanner.CodeScannerListener;
import cn.koolcloud.pos.external.EMVICManager;
import cn.koolcloud.pos.external.PinPadManager;
import cn.koolcloud.pos.external.SoundWave;
import cn.koolcloud.pos.external.SoundWave.SoundWaveListener;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.zbar.client.android.InterfaceBarCode;
import cn.koolcloud.zbar.client.android.ZbarScannerRelativeLayout;
import cn.koolcloud.zbar.client.android.camera.CameraSettings;

import com.google.zxing.client.android.ScannerRelativeLayout;

public class PayAccountController extends BaseController implements
		CardSwiperListener, SoundWaveListener, CodeScannerListener, View.OnClickListener, InterfaceBarCode {

	private final int PAY_ACOUNT_MAX_LENGTH = 20;
	private final int HANDLE_INIT_QRSCANNER = 1;
	private final int OPEN_CAMERA_DELAY_TIME = 50;
	protected LinearLayout layout_qrcode;
	protected LinearLayout layout_sound;
	protected RelativeLayout layout_swiper;
	protected LinearLayout layout_keyboard;
	protected LinearLayout layout_ic;
	protected TextView ic_dis;

	protected EditText et_id;

    private ImageView torchImageView;

	private CardSwiper ex_cardSwiper;
	private CodeScanner ex_codeScanner;
	private SoundWave ex_soundWave;

	private View selectedBtn;
	private LinearLayout selectedLayout;

	protected String func_swipeCard;
	protected String func_inputAccount;
	protected String func_nearfieldAccount;
	protected String func_icSwipeCard;
	private String transType;
	protected String keyIndex;
	private boolean removeJSTag = true;

	private String misc;
	private ImageScanner imageScanner;
	private FrameLayout previewFrameLayout;
	private MediaPlayer mediaPlayer;
	private boolean playBeep = true;
	private static final float BEEP_VOLUME = 0.10f;
	private static final long VIBRATE_DURATION = 200L;
	private static final String ZBTAG = "alipay";
	private static final String PREPAID_QRCODE = "prepaid_qrcode";

	private EMVICManager emvManager = null;
	private Boolean needPwd = false;
	private Boolean backEnable = true;
	private Boolean scanerStarted = false;
	private Boolean isPreferential = false;

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
	private TextView ic_swiper_guide_text;
	private ImageView icswiper_img;
	private ScannerRelativeLayout zscanner;
	private ZbarScannerRelativeLayout zBarScannerLayout;

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
	private String transAmountIC;
	// private Typeface faceTypeLanTing;

	private JSONObject data;
	private JSONObject pinpad_transData;
	private UtilFor8583 util8583 = UtilFor8583.getInstance();

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
		layout_swiper = (RelativeLayout) findViewById(R.id.pay_account_layout_swiper);
		layout_keyboard = (LinearLayout) findViewById(R.id.pay_account_layout_keyboard);
		et_id = (EditText) findViewById(R.id.pay_account_et_id);
        torchImageView = (ImageView) findViewById(R.id.torchImageView);
        torchImageView.setOnClickListener(this);

        //check H4 OR H5
        String displayVersion = android.os.Build.DISPLAY;
        String reg = "H5$";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(displayVersion);
        if (matcher.find()) {
            torchImageView.setVisibility(View.VISIBLE);
        } else {
            torchImageView.setVisibility(View.GONE);
        }

		setCurrentNumberEditText(et_id);
		ic_swiper_guide_text = (TextView) findViewById(R.id.pay_account_swiper_ic_text_guide);
		keyIndex = data.optString("brhKeyIndex","00");
		util8583.terminalConfig.setKeyIndex(keyIndex);
		transType = data.optString("transType");
		misc = data.optString("misc");
		icswiper_img = (ImageView) findViewById(R.id.icswiper_img);

		amountTextView = (TextView) findViewById(R.id.pay_account_tv_amount);
		if (transType.equals(APMP_TRAN_CONSUMECANCE)
				|| transType.equals(APMP_TRAN_PRAUTHCANCEL)
				|| transType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
			transAmount = "-" + data.optString("transAmount");
		} else {
			transAmount = data.optString("transAmount");
		}
		transAmountIC = data.optString("transAmount");
		amountTextView.setText(transAmount + "("+ Env.getCurrencyResource(this)+")");

		func_swipeCard = data.optString("swipeCard");
		func_inputAccount = data.optString("inputAccount");
		func_nearfieldAccount = data.optString("nearfieldAccount");
		func_icSwipeCard = data.optString("icSwipeCard");
		isPreferential = data.optBoolean("isPreferential",false);

        previewFrameLayout = (FrameLayout) findViewById(R.id.scanner_zb);
		zscanner = (ScannerRelativeLayout) findViewById(R.id.scanner);

		if (misc != null && !misc.equals("")) {
			if(misc.equals(ZBTAG))
			{

                previewFrameLayout.setVisibility(View.VISIBLE);

				zscanner.setVisibility(View.GONE);
			}else{
                previewFrameLayout.setVisibility(View.GONE);

				zscanner.setVisibility(View.VISIBLE);
				if (findViewById(R.id.pay_account_btn_qrcode).isEnabled()) {
					ex_codeScanner = new CodeScanner();
					ex_codeScanner.onCreate(PayAccountController.this,
							PayAccountController.this);
				}
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
	    initZbarLib();
		setTitle(HostMessage.getJsMsg(formData.optString(getString(R.string.formData_key_title))));
	}

    private void initZbarLib() {
        zBarScannerLayout = new ZbarScannerRelativeLayout(PayAccountController.this);
        zBarScannerLayout.setBarCodeCallBack(PayAccountController.this);
    }

	private void findViews() {
		barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
        // hidden bar title on clicking searching result item
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
		String misc = data.optString("misc");
		if (misc.equals(ConstantUtils.MISC_ALIPAY)) {
			acquireMerchNameTextView.setText(getResources().getString(
					R.string.bar_acquire_merch_msg_pid));
		}
		acquireMerchNumTextView = (TextView) findViewById(R.id.acquireMerchNumTextView);
		// acquireMerchNumTextView.setTypeface(faceTypeLanTing);
		acquireMerchNumTextView.setText(data.optString("brhMchtId"));
		acquireTerminalTextView = (TextView) findViewById(R.id.acquireTerminalTextView);
		if (misc.equals(ConstantUtils.MISC_ALIPAY)) {
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

	@Override
	public void setProperty(JSONArray data) {
		JSONObject jsonObj = data.optJSONObject(0);
		Boolean closeTag = jsonObj.optBoolean("close",false);
		Boolean isICPreferential = jsonObj.optBoolean("isICPreferential",false);
		String changedAmount = jsonObj.optString("changedAmount","0");
		pinpad_transData = jsonObj.optJSONObject("pinpad_data");
		if (closeTag) {
			// 停止IC卡读取功能
			if(isPreferential) {
				onStopReadICCardId();
			}else {
				onStopReadICData();
			}
		} else {
			if(isICPreferential){
				//之所以在这个地方进行finish,是为了防止在弹出dialog时，拔出卡时，没有状态显示。
				onStopReadICCardId();
				transAmountIC = changedAmount;
				onStartReadICData();
			}else if(pinpad_transData != null) {
				needPwd = true;
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.pay_account_tag_input_password));
				//needPwd = true;
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.pin_pad_tv_input_pwd_bg);
				icswiper_img.setImageDrawable(icDrawable);
				PinPadManager ppm = PinPadManager.getInstance();
				ppm.setParams(this,pinpad_transData,mHandler);
				ppm.start();
			}else{
				ic_swiper_guide_text.setText(getResources().getString(R.string.pay_account_input_ic_guide));
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.start_ic);
				icswiper_img.setImageDrawable(icDrawable);
			}
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
			zscanner.setVisibility(View.GONE);
			previewFrameLayout.setVisibility(View.VISIBLE);

			scanerStarted = true;
			mHandler.sendEmptyMessageDelayed(HANDLE_INIT_QRSCANNER,OPEN_CAMERA_DELAY_TIME);
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			//DO NOTHING
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
			onStartSwiper();
			if(isPreferential) {
				onStartReadICCardId();
			}else {
				onStartReadICData();
			}
		} else if (preTag.equalsIgnoreCase(actionTag)) {
			onStopReadICData();
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
				if (misc != null && misc.equals(ZBTAG)) {
					UtilFor8583.getInstance().trans
							.setEntryMode(ConstantUtils.ENTRY_QRCODE_MODE);
					startZbarScaner();
				} else if (misc != null && misc.equals(PREPAID_QRCODE)) {
					// for prepaid card then set entry mode
					UtilFor8583.getInstance().trans
							.setEntryMode(ConstantUtils.ENTRY_PREPAID_CARD_QRCODE_MODE);
					previewFrameLayout.setVisibility(View.GONE);
					zscanner.setVisibility(View.VISIBLE);
					onStartQRScanner();
				}else {
					UtilFor8583.getInstance().trans
							.setEntryMode(ConstantUtils.ENTRY_QRCODE_MODE);
					previewFrameLayout.setVisibility(View.GONE);
					zscanner.setVisibility(View.VISIBLE);
					onStartQRScanner();
				}
				break;
			case EMVICManager.STATUS_VALUE_0: {
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_1));
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.start_ic);
				icswiper_img.setImageDrawable(icDrawable);
				setLeftButtonHidden();
				backEnable = false;
				break;
			}
			case EMVICManager.STATUS_VALUE_1: {
				if (needPwd) {
					ic_swiper_guide_text.setTextColor(getResources().getColor(
							R.color.red));
					ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_2));
				} else {
					ic_swiper_guide_text.setTextColor(getResources().getColor(
							R.color.blue));
					ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_3));
				}
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.start_icswiper);
				icswiper_img.setImageDrawable(icDrawable);
				setLeftButtonVisible();
				backEnable = true;
				break;
			}
			case EMVICManager.STATUS_VALUE_2:
				break;
			case EMVICManager.STATUS_VALUE_3:
				break;
			case EMVICManager.STATUS_VALUE_4: {
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_4));
				needPwd = true;
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.pin_pad_tv_input_pwd_bg);
				icswiper_img.setImageDrawable(icDrawable);
				break;
			}
			case EMVICManager.TRADE_STATUS_0:
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_5));
				break;
			case EMVICManager.TRADE_STATUS_1:
				onStopSwiper();// 停止刷卡功能
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_6));
				break;
			case EMVICManager.TRADE_STATUS_2:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_7));
				break;
			case EMVICManager.TRADE_STATUS_3:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_5));
				break;
			case EMVICManager.TRADE_STATUS_4:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_6));
				break;
			case EMVICManager.TRADE_STATUS_5:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_7));
				break;
			case EMVICManager.TRADE_STATUS_6:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_5));
				break;
			case EMVICManager.TRADE_STATUS_7:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_6));
				break;
			case EMVICManager.TRADE_STATUS_8:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_7));
				break;
			case EMVICManager.TRADE_STATUS_9:
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.red));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_8));
				break;
			case EMVICManager.TRADE_STATUS_10:
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_14));
				break;
			case EMVICManager.TRADE_STATUS_BAN: {
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_9));
				needPwd = false;
				JSONObject transData = new JSONObject();
				try {
					transData.put("isCancelled", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onStopReadICData();
				onCall(func_icSwipeCard, transData);
				break;
			}
			case EMVICManager.TRADE_STATUS_ABORT: {
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_10));
				needPwd = false;
				JSONObject transData = new JSONObject();
				try {
					transData.put("isCancelled", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onStopReadICData();
				onCall(func_icSwipeCard, transData);
				break;
			}
			case EMVICManager.TRADE_STATUS_APPROVED:
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_11));
				needPwd = false;
				break;
			case EMVICManager.TRADE_STATUS_DISABLESERVICE: {
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				needPwd = false;
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_12));
				JSONObject transData = new JSONObject();
				try {
					transData.put("isCancelled", true);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				onStopReadICData();
				onCall(func_icSwipeCard, transData);
				break;
			}
			case EMVICManager.TRADE_STATUS_ONLINE:
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.ic_status_insert_warning_13));
				needPwd = false;
				break;
			case EMVICManager.TRADE_STATUS_AFGETICDATA: {
				JSONObject transData = new JSONObject();
				EMVICData mEMVICData = EMVICData.getEMVICInstance();
				String pwd = Utility.hexString(mEMVICData.getPinBlock());
				try {
					transData.put("cardID", mEMVICData.getICPan());
					transData.put("track2", mEMVICData.getTrack2());
					transData.put("validTime", mEMVICData.getDataOfExpired());
					transData.put("pwd", pwd);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				UtilFor8583.getInstance().trans
						.setEntryMode(ConstantUtils.ENTRY_IC_MODE);
				onCall(func_icSwipeCard, transData);
				break;
			}
			case EMVICManager.TRADE_STATUS_READICCARDID:{
				EMVICData mEMVICData = EMVICData.getEMVICInstance();
				emvManager.getPAN();
				String pan = mEMVICData.getICPan();
				JSONObject sendMsg = new JSONObject();
				try {
					sendMsg.put("ICCardID",pan);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onCall("PayAccount.isPreferential",sendMsg);
				break;
			}
			case PinPadManager.MODE_INPUT_AUTH_CODE:
				ic_swiper_guide_text.setTextColor(getResources().getColor(
						R.color.blue));
				ic_swiper_guide_text.setText(getResources().getString(R.string.pin_pad_tv_java_input_authCode));
				//needPwd = true;
				Drawable icDrawable = getResources().getDrawable(
						R.drawable.pin_pad_tv_input_pwd_bg);
				icswiper_img.setImageDrawable(icDrawable);
				break;
			case PinPadManager.MODE_PAY_NEXT:
				Bundle bd = msg.getData();
				String pwd = bd.getString("pwd", null);
				Boolean isCancelled = bd.getBoolean("isCancelled",false);
				String authCode = bd.getString("authCode",null);
				try {
					pinpad_transData.put("pwd",pwd);
					pinpad_transData.put("isCancelled",isCancelled);
					pinpad_transData.put("authCode",authCode);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				onCall("PayAccount.CompleteInput",pinpad_transData);
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
			ex_cardSwiper = null;
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

	private void onDestroySound() {
		if (ex_soundWave != null) {
			ex_soundWave.onDestroy();
			ex_soundWave = null;
		}
	}

	private void onStartKeyBoard() {

	}

	private void onStopKeyBoard() {

	}

	/**
	 * 读取IC卡信息
	 */
	private void onStartReadICData() {
		if (emvManager == null) {
			emvManager = EMVICManager.getEMVICManagerInstance();
			emvManager.setTransAmount(transAmountIC);
			emvManager.onCreate(this, mHandler);
		}
		emvManager.onStart();
	}

	private void onStartReadICCardId(){
		if (emvManager == null) {
			emvManager = EMVICManager.getEMVICManagerInstance();
			emvManager.onCreate(this, mHandler);
			emvManager.initForRead();
		}
		emvManager.onStart();

	}

	private void onStopReadICCardId(){
		if (emvManager != null) {
			emvManager.finishRead();
			emvManager = null;
		}
	}

	/**
	 * 停止读取IC卡信息
	 */
	private void onStopReadICData() {
		if (emvManager != null) {
			emvManager.onPause();
			emvManager = null;
		}
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
		// onStopReadICData();
		// onStopSwiper();
		onStopSound();
		if (misc != null && !misc.equals("")) {
			if (misc.equals(ZBTAG)) {
				stopZbarScaner();
			} else {
				onStopQRScanner();
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
		/*
		 * if (emvManager != null) { emvManager.onStart(); } else {
		 * onStartReadICData(); } if (ex_cardSwiper != null) {
		 * ex_cardSwiper.onStart(); } else { onStartSwiper(); }
		 */
		if (ex_soundWave != null) {
			ex_soundWave.onStart();
		}
		if(selectedBtn.getTag().toString().equalsIgnoreCase(getString(R.string.pay_account_tag_qrcode))){
			scanerStarted = true;
			mHandler.sendEmptyMessageDelayed(HANDLE_INIT_QRSCANNER,OPEN_CAMERA_DELAY_TIME);
		}
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, this + "onDestroy");

		onStopReadICData();
		onStopSwiper();
		onDestroySound();
		if (misc != null && !misc.equals("")) {
			if (misc.equals(ZBTAG)) {
				if (zBarScannerLayout != null) {
                    zBarScannerLayout.stopScan();
					previewFrameLayout.removeAllViews();
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
	public void onClickLeftButton(View view) {
		// TODO Auto-generated method stub
		if (needPwd) {
			onCall("PayAccount.cancelDialog", null);
		} else {
			onPause();
			super.onClickLeftButton(view);
		}
	}

	@Override
	public void onBackPressed() {
		if (needPwd) {
			onCall("PayAccount.cancelDialog", null);
		} else {
			if (backEnable) {
				onCall("PayAccount.clear", null);
				onPause();
				super.onBackPressed();
			}
		}
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
		UtilFor8583.getInstance().trans
				.setEntryMode(ConstantUtils.ENTRY_SWIPER_MODE);
		onCall(func_swipeCard, msg);
	}

    @Override
    public void onRecvTrackDataError(int resCode, int trackIndex) {
        String reSwiptCardStr = getResources().getString(R.string.msg_swipe_card_error);
        Toast.makeText(this, reSwiptCardStr, Toast.LENGTH_SHORT).show();
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

    boolean ledTag = false;

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
    public void getBarCodeData(String data) {
        JSONObject transData = new JSONObject();
        try {
            transData.put(getString(R.string.formData_key_payData_field0), data);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        zBarScannerLayout.stopScan();
	    previewFrameLayout.removeAllViews();
        Log.d(TAG, "processReceivedData : " + transData.toString());
        onCall(func_nearfieldAccount, transData);
    }
}
