package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.util.HashSet;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.wd.R;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.printer.exception.PrinterException;

public class OrderDetailController extends BaseController {

	private final static int PAY_SUCCESS = 1;
	private final static int ORDER_STATUS_SUCCESS = 0;
	private final static int TRAN_TYPE_REVERSE = 3021;
	private final static int TRAN_TYPE_REFUND = 3051;
	private final static int TRAN_TYPE_AUTH = 1011;
	private final static int TRAN_TYPE_AUTH_CANCEL = 3011;
	private final static int TRAN_TYPE_AUTH_COMPLETE_CANCEL = 3031;
	private final static int TRAN_TYPE_AUTH_SETTLEMENT = 1091;

	private boolean cancelEnable;
	private String rrn;
	private String transTime;
	private String transAmount;
	private String oriTransTime;
	private String func_confirm;
	private String openBrh;
	private String paymentId;
	private String paymentName;
	private String txnId;
	private String authCode;
	private int orderState = -1;
	private int paymentOrder = -1;
	private boolean removeJSTag = true;
	private JSONObject data;

	// add new item
	private String batchNo;
	private String traceNo;
	private String payKeyIndex;

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
	private Button couponButton;

	private HashSet<String> orderStateSet = new HashSet<String>();
	private int transType = -1;
	UtilFor8583 util8583 = UtilFor8583.getInstance();

	private boolean isExternalOrder = false;
	// private Typeface faceTypeLanTing;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		data = formData.optJSONObject(getString(R.string.formData_key_data));
		initTextView(R.id.order_detail_tv_orderId, data, "refNo");
		initTextView(R.id.order_detail_tv_orderStatus, data, "orderStateDesc");
		initTextView(R.id.order_detail_tv_payType, data, "payTypeDesc");
		initTextView(R.id.order_detail_tv_transAmount, data, "transAmount");
		initTextView(R.id.order_detail_tv_transDate, data, "transTime");
		initTextView(R.id.order_detail_tv_transType, data, "transTypeDesc");
		initTextView(R.id.order_detail_tv_authcode, data, "authNo");

		String operator = data.optString("operator");
		if (TextUtils.isEmpty(operator)) {
			try {
				String userInfo = ClientEngine.engineInstance()
						.getSecureService().getUserInfo();
				if (!TextUtils.isEmpty(userInfo)) {
					try {
						JSONObject userInfoObj = new JSONObject(userInfo);
						operator = userInfoObj.getString("userName");
						data.put("operator", operator);
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
		initTextView(R.id.order_detail_tv_operator, data, "operator");

		// faceTypeLanTing = Typeface.createFromAsset(getAssets(),
		// "font/fzltxhk.ttf");

		rrn = data.optString("refNo");
		transTime = data.optString("transTime");
		oriTransTime = data.optString("oriTransTime");
		transAmount = data.optString("transAmount");
		func_confirm = data.optString("confirm");
		txnId = data.optString("txnId");
		isExternalOrder = data.optBoolean("isExternalOrder");

		// get order state and trans type
		String orderStateDesc = data.optString("orderStateDesc");
		orderStateSet.add(orderStateDesc);
		orderState = data.optInt("orderState");
		transType = data.optInt("transType");

		openBrh = data.optString("openBrh");
		paymentId = data.optString("paymentId");
		paymentName = data.optString("paymentName");
		paymentOrder = data.optInt("paymentOrder");
		authCode = data.optString("authNo");

		// get batch no. and trace no.
		// set default value
		batchNo = data.optString("batchNo");
		traceNo = data.optString("traceNo");
		payKeyIndex = data.optString("payKeyIndex");
		util8583.terminalConfig.setKeyIndex(payKeyIndex);
		findViews();
		initButtons();
	}

	private void initTextView(int resourceId, JSONObject data, String key) {
		initTextView(resourceId, data, key, true);
	}

	private void findViews() {
		// hidden bar title on clicking searching result item
		barTitleLayout = (RelativeLayout) findViewById(R.id.barTitleLayout);
		String merchId = data.optString("merchId");
		if (TextUtils.isEmpty(merchId)) {
			barTitleLayout.setVisibility(View.INVISIBLE);
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
		// acquireMerchNameTextView.setTypeface(faceTypeLanTing);
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

		couponButton = (Button) findViewById(R.id.order_detail_btn_coupon);

		if (Env.checkApkExist(OrderDetailController.this,
				ConstantUtils.COUPON_APP_PACKAGE_NAME)) {
			couponButton.setVisibility(View.VISIBLE);
		} else {
			couponButton.setVisibility(View.GONE);
		}

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
		Button authCompleteBtn = (Button) findViewById(R.id.order_detail_btn_auth_complete);
		Button authSettlementBtn = (Button) findViewById(R.id.order_detail_btn_auth_settlement);
		RelativeLayout layout_auth_complete = (RelativeLayout) findViewById(R.id.layout_auth_complete);
		RelativeLayout layout_auth_settlement = (RelativeLayout) findViewById(R.id.layout_auth_settlement);
		if (paymentOrder == PAY_SUCCESS
				|| orderState != ORDER_STATUS_SUCCESS
				|| (orderState == ORDER_STATUS_SUCCESS && transType == TRAN_TYPE_REVERSE)
				|| (orderState == ORDER_STATUS_SUCCESS && transType == TRAN_TYPE_REFUND)
				|| (orderState == ORDER_STATUS_SUCCESS && transType == this.TRAN_TYPE_AUTH_CANCEL)
				|| (orderState == ORDER_STATUS_SUCCESS && transType == this.TRAN_TYPE_AUTH_COMPLETE_CANCEL)
				|| (orderState == ORDER_STATUS_SUCCESS && transType == this.TRAN_TYPE_AUTH_SETTLEMENT)) {
			if (transType == TRAN_TYPE_AUTH) {
				layout_auth_complete.setVisibility(View.VISIBLE);
				layout_auth_settlement.setVisibility(View.VISIBLE);
				authCompleteBtn.setClickable(false);
				authSettlementBtn.setClickable(false);
				authCompleteBtn
						.setBackgroundResource(R.drawable.button_disable_background_color);
				authSettlementBtn
						.setBackgroundResource(R.drawable.button_disable_background_color);
			}
			refundBtn.setClickable(false);
			reverseBtn.setClickable(false);
			refundBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);

			reverseBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);

		}
		if (orderState == ORDER_STATUS_SUCCESS && transType == TRAN_TYPE_AUTH) {
			layout_auth_complete.setVisibility(View.VISIBLE);
			layout_auth_settlement.setVisibility(View.VISIBLE);
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
			msg.put("txnId", txnId);
			msg.put("authNo", authCode);
			msg.put("transType", transType);

			// put new items
			msg.put("batchNo", batchNo);
			msg.put("traceNo", traceNo);
			msg.put("payKeyIndex", payKeyIndex);
			msg.put("formatedTransDate",
					transTime.split(" ")[0].replace("-", ","));
			// for cash
			msg.put("resCode", "00");
			msg.put("oriTransTime", oriTransTime);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onCancel", msg);
	}

	public void onCoupon(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("transAmount", transAmount);
			msg.put("txnId", txnId);
			msg.put("payKeyIndex", payKeyIndex);
		} catch (Exception e) {
			e.printStackTrace();
		}

		onCall("OrderDetail.showCoupon", msg);
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
			msg.put("payKeyIndex", payKeyIndex);
			msg.put("transType", transType);
			msg.put("txnId", txnId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		onCall("OrderDetail.onRefund", msg);
	}

	public void onAuthComplete(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("transTime", transTime);
			msg.put("transAmount", transAmount);
			msg.put("openBrh", openBrh);
			msg.put("paymentId", paymentId);
			msg.put("paymentName", paymentName);
			msg.put("payKeyIndex", payKeyIndex);
			msg.put("authNo", authCode);
			msg.put("transType", transType);
			msg.put("txnId", txnId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onAuthComplete", msg);
	}

	public void onAuthSettlement(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("transTime", transTime);
			msg.put("transAmount", transAmount);
			msg.put("openBrh", openBrh);
			msg.put("paymentId", paymentId);
			msg.put("paymentName", paymentName);
			msg.put("payKeyIndex", payKeyIndex);
			msg.put("authNo", authCode);
			msg.put("transType", transType);
			msg.put("txnId", txnId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("OrderDetail.onAuthSettlement", msg);
	}

	public void onPrint(View view) {
		JSONObject msg = new JSONObject();
		try {
			msg.put("ref", rrn);
			msg.put("paymentId", paymentId);
			msg.put("paymentName", paymentName);
			msg.put("payKeyIndex", payKeyIndex);
			msg.put("txnId", txnId);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// get print type
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(OrderDetailController.this,
						"paymentInfo");
		String paymentStr = (String) map.get(paymentId);

		String printType = "";
		String openBrhName = "";
		String brhMchtId = "";
		String brhTermId = "";
		try {
			JSONObject jsonObj = new JSONObject(paymentStr);
			printType = jsonObj.getString("printType");
			openBrhName = jsonObj.getString("openBrhName");
			brhMchtId = jsonObj.getString("brhMchtId");
			brhTermId = jsonObj.getString("brhTermId");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (!TextUtils.isEmpty(printType)
				&& printType.equals(ConstantUtils.PRINT_TYPE_MISPOS)) {
			// TODO:organize mispos data then print receipt
			MisposData beanData = new MisposData();

			// get operator
			Map<String, ?> merchantMap = UtilForDataStorage
					.readPropertyBySharedPreferences(
							OrderDetailController.this, "merchant");
			String operatorName = (String) merchantMap.get("operator");
			beanData.setOperatorId(operatorName);

			beanData.setPaymentName(paymentName);
			beanData.setCardNo(data.optString("cardNo"));
			beanData.setTransType(String.valueOf(transType));
			beanData.setAmount(transAmount.replace(".", "").replaceFirst("^0+",
					""));
			beanData.setBatchNo(batchNo);
			beanData.setTranDate(transTime.split(" ")[0].replace("-", ""));
			beanData.setTranTime(transTime.split(" ")[1].replace(":", ""));
			beanData.setRefNo(rrn);
			beanData.setVoucherNo(traceNo);

			beanData.setMerchantName(openBrhName);
			beanData.setMerchantId(brhMchtId);
			beanData.setTerminalId(brhTermId);
			beanData.setAuthNo("");
			new PrinterThread(beanData).start();
		} else {
			onCall("OrderDetail.onPrint", msg);
		}
	}

	public void onConfirm(View view) {
		handleBackAndConfirmButton();
	}

	@Override
	public void onBackPressed() {
		handleBackAndConfirmButton();
	}

	private void handleBackAndConfirmButton() {
		// call research js
		TextView orderStatusTextView = (TextView) findViewById(R.id.order_detail_tv_orderStatus);
		String orderStatus = orderStatusTextView.getText().toString();

		// mod for reverse status for 3th start reverse mod by Teddy on 1st
		// August -- start

		try {
			if (!TextUtils.isEmpty(orderStatus)
					&& orderStatus.equals(ConstantUtils.ALREADY_REVERSED)) {
				data.put("reverse_status", 1);
			} else {
				data.put("reverse_status", 0);
			}
			data.put("orderStateDesc", orderStatus);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall(func_confirm, data);

		// mod for reverse status for 3th start reverse mod by Teddy on 1st
		// August -- end

		// if (!TextUtils.isEmpty(orderStatus) && (orderStatus.equals("已撤销") ||
		// orderStatus.equals("已退货"))) {
		//external order return directly fixed by Teddy on 7th August --start
		if (isExternalOrder) {
			return;
		}
		//external order return directly fixed by Teddy on 7th August --end
		if (!orderStateSet.contains(orderStatus)) {// refresh the record list
													// when status changed
			onCall("TransactionManageIndex.refreshResearch", null);
		}
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
		if (!orderStateSet.contains(orderStatus)) {// refresh button status
			Button refundBtn = (Button) findViewById(R.id.order_detail_btn_refund);
			Button reverseBtn = (Button) findViewById(R.id.order_detail_btn_cancel);
			refundBtn.setClickable(false);
			refundBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);
			reverseBtn.setClickable(false);
			reverseBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);

			Button authCompleteBtn = (Button) findViewById(R.id.order_detail_btn_auth_complete);
			Button authSettlementBtn = (Button) findViewById(R.id.order_detail_btn_auth_settlement);
			authCompleteBtn.setClickable(false);
			authSettlementBtn.setClickable(false);
			authCompleteBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);
			authSettlementBtn
					.setBackgroundResource(R.drawable.button_disable_background_color);
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

	class PrinterThread extends Thread {
		private MisposData beanData;

		public PrinterThread(MisposData beanData) {
			this.beanData = beanData;
		}

		@Override
		public void run() {
			try {
				PrinterHelper.getInstance(OrderDetailController.this)
						.printMisposReceipt(beanData);
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}
}
