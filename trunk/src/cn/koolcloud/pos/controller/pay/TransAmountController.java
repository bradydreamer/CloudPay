package cn.koolcloud.pos.controller.pay;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.controller.mispos.MisposController;
import cn.koolcloud.pos.util.UtilForMoney;

public class TransAmountController extends BaseController {

	public static final int RESULT_CODE_AMOUNT = 60;
	private EditText et_money;
	private long maxAmount = 0;
	private Typeface faceType;
//	private Typeface faceTypeLanTing;
	private boolean removeJSTag = true;
	
	//muilti info bar components
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
	private JSONObject data;
	
	private String requestFromMispos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//mispos get amount from this controller formData is null --start add by Teddy on 1th July
		requestFromMispos = getIntent().getStringExtra(MisposController.KEY_REQUEST_FROM_MISPOS);
		if (TextUtils.isEmpty(requestFromMispos)) {
			if (null == formData) {
				finish();
				return;
			}
			data = formData.optJSONObject(getString(R.string.formData_key_data));
		}
		//mispos get amount from this controller formData is null --end add by Teddy on 1th July
		
		faceType = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
//		faceTypeLanTing = Typeface.createFromAsset(getAssets(), "font/fzltxhk.ttf");
		et_money = (EditText) findViewById(R.id.input_money_et_money);
		et_money.setTypeface(faceType);
		
		if (data != null) {
			String defaulAmount = data.optString("maxAmount");
			if (!defaulAmount.isEmpty()) {
				maxAmount = Long.parseLong(defaulAmount);
				et_money.setText(UtilForMoney.fen2yuan(defaulAmount));
				numberInputString.append(maxAmount);
			}
		}
		findViews();
	}
	
	private void findViews() {
		//hidden bar title on input amount for multi pay
		barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
		if (TextUtils.isEmpty(requestFromMispos)) {
			
			String merchId = data.optString("merchId");
			if (TextUtils.isEmpty(merchId)) {
				barTitleLayout.setVisibility(View.INVISIBLE);
			}
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
//		acquireMerchNameTextView.setTypeface(faceTypeLanTing);
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
		} else {
			barTitleLayout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	protected void showInputNumber() {
		StringBuilder textbuBuilder = getNumberInputString();
		if (null == textbuBuilder || 0 == textbuBuilder.length()) {
			et_money.setText(null);
		} else {
			String amount = textbuBuilder.toString();
			String text = UtilForMoney.fen2yuan(amount);
			et_money.setText(text);
		}
	}

	@Override
	protected void addInputNumber(String text) {
		if (null != text && numberInputString.toString().length() < 12) {
			if (numberInputString.toString().equals("0")) {
				numberInputString.replace(0, 1, text);
			} else {
				numberInputString.append(text);
			}
			if ((maxAmount > 0 && Long.parseLong(numberInputString.toString()) > maxAmount)) {
				numberInputString.delete(0, numberInputString.toString()
						.length());
				numberInputString.append(String.valueOf(maxAmount));
			}
		}
	}

	@Override
	public void onClickBtnC(View view) {
		super.onClickBtnC(view);
	}

	@Override
	public void onClickBtnOK(View view) {
		String numberConfirmed = getNumberInputString().toString();
		if (numberConfirmed.isEmpty()
				|| 0 == Long.parseLong(numberConfirmed)
				|| (0 != maxAmount && maxAmount < Long
						.parseLong(numberInputString.toString()))) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put(getString(R.string.formData_key_transAmount),
					numberConfirmed);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		//if request from mispos then set amount result else execute original flow  --start mod by Teddy on 1th July
		
		if (TextUtils.isEmpty(requestFromMispos)) {
			onCall("InputAmount.onCompleteInput", msg);
		} else {
			Intent mIntent = new Intent();  
	        mIntent.putExtra(MisposController.KEY_AMOUNT, numberConfirmed);
	        if (getParent() == null) {
	            setResult(RESULT_CODE_AMOUNT, mIntent);
	        } else {
	            getParent().setResult(RESULT_CODE_AMOUNT, mIntent);
	        }
	        finish();
		}
		//if request from mispos then set amount result else execute original flow  --end mod by Teddy on 1th July
	}

	@Override
	public void onBackPressed() {
		onCall("InputAmount.clear", null);
		super.onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_trans_amount_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_input_money_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_TransAmount);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_TransAmount);
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
