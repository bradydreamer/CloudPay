package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class OrderDetailController extends BaseController {
	
	private final static int PAY_SUCCESS = 1; 
	private boolean cancelEnable;
	private String rrn;
	private String transTime;
	private String transAmount;
	private String func_confirm;
	private String openBrh;
	private String paymentId;
	private int paymentOrder = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		initTextView(R.id.order_detail_tv_orderId, data, "ref");
		initTextView(R.id.order_detail_tv_orderStatus, data, "orderStateDesc");
		initTextView(R.id.order_detail_tv_payType, data, "payTypeDesc");
		initTextView(R.id.order_detail_tv_transAmount, data, "transAmount");
		initTextView(R.id.order_detail_tv_transDate, data, "transTime");
		initTextView(R.id.order_detail_tv_transType, data, "transTypeDesc");

		cancelEnable = data.optBoolean("cancelEnable");
		rrn = data.optString("ref");
		transTime = data.optString("transTime");
		transAmount = data.optString("transAmount");
		func_confirm = data.optString("confirm");

		openBrh = data.optString("openBrh");
		paymentId = data.optString("paymentId");
		
		paymentOrder = data.optInt("paymentOrder");
		initButtons();
	}

	private void initTextView(int resourceId, JSONObject data, String key) {
		initTextView(resourceId, data, key, false);
	}

	private void initTextView(int resourceId, JSONObject data, String key,
			boolean removeIfNull) {
		TextView textView = (TextView) findViewById(resourceId);
		textView.setText(data.optString(key, ""));
		if (removeIfNull && textView.getText().equals("")) {
			((ViewGroup) textView.getParent()).setVisibility(View.GONE);
		}
	}
	
	private void initButtons() {
		Button refundBtn = (Button) findViewById(R.id.order_detail_btn_refund);
		Button reverseBtn = (Button) findViewById(R.id.order_detail_btn_cancel);
		if (paymentOrder == PAY_SUCCESS) {
			refundBtn.setVisibility(View.INVISIBLE);
			reverseBtn.setVisibility(View.INVISIBLE);
		}
	}

	public void onCancel(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("transTime", transTime);
			msg.put("transAmount", transAmount);
			msg.put("openBrh", openBrh);
			msg.put("paymentId", paymentId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onCancel", msg);
	}

	public void onRefund(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("transTime", transTime);
			msg.put("transAmount", transAmount);
			msg.put("openBrh", openBrh);
			msg.put("paymentId", paymentId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		onCall("OrderDetail.onRefund", msg);
	}

	public void onPrint(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onPrint", msg);
	}

	public void onConfirm(View view) {
		onCall(func_confirm, null);
	}

	@Override
	public void onBackPressed() {
		onCall(func_confirm, null);
	}

	@Override
	protected View viewForIdentifier(String name) {
		if (null == name) {
			return null;
		} else if (name.equals("orderStatus")) {
			return findViewById(R.id.order_detail_tv_orderStatus);
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_order_detail_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_order_detail);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_OrderDetail);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_OrderDetail);
	}

}
