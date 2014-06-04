package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.util.HashSet;

import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.koolcloud.constant.Constant;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class OrderDetailController extends BaseController {

	private final static int PAY_SUCCESS = 1;
	private final static int ORDER_STATUS_SUCCESS = 0;
	private final static String TRAN_TYPE_REVERSE = "6062";
	private final static String TRAN_TYPE_REFUND = "6078";
	private boolean cancelEnable;
	private String rrn;
	private String transTime;
	private String transAmount;
	private String func_confirm;
	private String openBrh;
	private String paymentId;
	private String paymentName;
	private int orderState = -1;
	private int paymentOrder = -1;
	private boolean removeJSTag = true;
	private JSONObject data;
	
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
	
	private HashSet<String> orderStateSet = new HashSet<String>();
	private String transType;
//	private Typeface faceTypeLanTing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		data = formData.optJSONObject(getString(R.string.formData_key_data));
		initTextView(R.id.order_detail_tv_orderId, data, "ref");
		initTextView(R.id.order_detail_tv_orderStatus, data, "orderStateDesc");
		initTextView(R.id.order_detail_tv_payType, data, "payTypeDesc");
		initTextView(R.id.order_detail_tv_transAmount, data, "transAmount");
		initTextView(R.id.order_detail_tv_transDate, data, "transTime");
		initTextView(R.id.order_detail_tv_transType, data, "transTypeDesc");

//		faceTypeLanTing = Typeface.createFromAsset(getAssets(), "font/fzltxhk.ttf");
		
		rrn = data.optString("ref");
		transTime = data.optString("transTime");
		transAmount = data.optString("transAmount");
		func_confirm = data.optString("confirm");

		//get order state and tran type
		String orderStateDesc = data.optString("orderStateDesc");
		orderStateSet.add(orderStateDesc);
		orderState = data.optInt("orderState");
		transType = data.optString("transType");

		openBrh = data.optString("openBrh");
		paymentId = data.optString("paymentId");
		paymentName = data.optString("paymentName");
		paymentOrder = data.optInt("paymentOrder");
		
		findViews();
		initButtons();
	}

	private void initTextView(int resourceId, JSONObject data, String key) {
		initTextView(resourceId, data, key, false);
	}
	
	private void findViews() {
		//hidden bar title on clicking searching result item
		barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
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
		if (paymentOrder == PAY_SUCCESS || orderState != ORDER_STATUS_SUCCESS
				|| (orderState == ORDER_STATUS_SUCCESS && transType.equals(TRAN_TYPE_REVERSE))
				|| (orderState == ORDER_STATUS_SUCCESS && transType.equals(TRAN_TYPE_REFUND))) {
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
			msg.put("paymentName", paymentName);
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
			msg.put("paymentName", paymentName);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		onCall("OrderDetail.onRefund", msg);
	}

	public void onPrint(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("paymentId", paymentId);
			msg.put("paymentName", paymentName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onPrint", msg);
	}

	public void onConfirm(View view) {
		onCall(func_confirm, null);
		//call research js
		TextView orderStatusTextView = (TextView) findViewById(R.id.order_detail_tv_orderStatus);
		String orderStatus = orderStatusTextView.getText().toString();
		//if (!TextUtils.isEmpty(orderStatus) && (orderStatus.equals("已撤销") || orderStatus.equals("已退货"))) {
		if (!orderStateSet.contains(orderStatus)) {//refresh the record list when status changed
			onCall("TransactionManageIndex.refreshResearch", null);
		}
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
	protected void updateViews(JSONObject item) {
		super.updateViews(item);
		String orderStatus = "";
		if (null != item) {
			orderStatus = item.optString("value");
		}
		if (!orderStateSet.contains(orderStatus)) {//refresh button status
			Button refundBtn = (Button) findViewById(R.id.order_detail_btn_refund);
			Button reverseBtn = (Button) findViewById(R.id.order_detail_btn_cancel);
			refundBtn.setVisibility(View.INVISIBLE);
			reverseBtn.setVisibility(View.INVISIBLE);
		}
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
