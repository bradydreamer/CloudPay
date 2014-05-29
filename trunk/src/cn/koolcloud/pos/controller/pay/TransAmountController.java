package cn.koolcloud.pos.controller.pay;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForMoney;

public class TransAmountController extends BaseController {

	private EditText et_money;
	private long maxAmount = 0;
	private Typeface faceType;
	private Typeface faceTypeLanTing;
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
	private TextView qcquireTerminalTextView;
	private TextView acquireTerminalNumTextView;
	private JSONObject data;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		faceType = Typeface.createFromAsset(getAssets(), "font/digital-7.ttf");
		faceTypeLanTing = Typeface.createFromAsset(getAssets(), "font/fzltxhk.ttf");
		et_money = (EditText) findViewById(R.id.input_money_et_money);
		et_money.setTypeface(faceType);
		data = formData.optJSONObject(getString(R.string.formData_key_data));
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
		koolCloudMerchNumNameTextView = (TextView) findViewById(R.id.koolCloudMerchNumNameTextView);
		koolCloudMerchNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView = (TextView) findViewById(R.id.koolCloudMerchNumTextView);
		koolCloudMerchNumTextView.setTypeface(faceTypeLanTing);
		koolCloudMerchNumTextView.setText(data.optString("merchId"));
		koolCloudDeviceNumNameTextView = (TextView) findViewById(R.id.koolCloudDeviceNumNameTextView);
		koolCloudDeviceNumNameTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView = (TextView) findViewById(R.id.koolCloudDeviceNumTextView);
		koolCloudDeviceNumTextView.setTypeface(faceTypeLanTing);
		koolCloudDeviceNumTextView.setText(data.optString("iposId"));
		acquireNameTextView = (TextView) findViewById(R.id.acquireNameTextView);
		acquireNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView = (TextView) findViewById(R.id.acquireNickNameTextView);
		acquireNickNameTextView.setTypeface(faceTypeLanTing);
		acquireNickNameTextView.setText(data.optString("openBrhName"));
		acquireMerchNameTextView = (TextView) findViewById(R.id.acquireMerchNameTextView);
		acquireMerchNameTextView.setTypeface(faceTypeLanTing);
		acquireMerchNumTextView = (TextView) findViewById(R.id.acquireMerchNumTextView);
		acquireMerchNumTextView.setTypeface(faceTypeLanTing);
		acquireMerchNumTextView.setText(data.optString("brhMchtId"));
		qcquireTerminalTextView = (TextView) findViewById(R.id.qcquireTerminalTextView);
		qcquireTerminalTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView = (TextView) findViewById(R.id.acquireTerminalNumTextView);
		acquireTerminalNumTextView.setTypeface(faceTypeLanTing);
		acquireTerminalNumTextView.setText(data.optString("brhTermId"));
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
		if (null != text && numberInputString.toString().length() < 9) {
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
		onCall("InputAmount.onCompleteInput", msg);
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
