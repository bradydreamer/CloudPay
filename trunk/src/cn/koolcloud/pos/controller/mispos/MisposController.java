package cn.koolcloud.pos.controller.mispos;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.koolcloud.jni.MisPosInterface;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseHomeController;
import cn.koolcloud.pos.controller.pay.TransAmountController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.entity.MisposData;
import cn.koolcloud.pos.util.MisposOperationUtil;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.printer.PrinterException;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.AppUtil;

public class MisposController extends BaseHomeController implements
		View.OnClickListener {
	private static final String TAG = "MisposController";

	private static final int SEND_DELAYED_COMMAND_HANDLER = 0;
	private static final int RECEIVE_BEAN_DATA_HANDLER = 1;

	// private components
	private LinearLayout commonMisposLayout;
	private LinearLayout indicatorMisposLayout;
	private LinearLayout balanceLayout;
	private LinearLayout orderDetailslayout;
	private TextView commonTextView;
	private TextView amountTextView;
	private TextView balanceAmountTextView;
	private TextView order_detail_tv_transAmount;
	private TextView order_detail_tv_transDate;
	private TextView order_detail_tv_transType;
	private TextView order_detail_tv_payType;
	private TextView order_detail_tv_orderId;
	private TextView order_detail_tv_orderStatus;
	private Button order_detail_btn_confirm;

	public static final String SALE_TRAN_CONSTANT = "SALE";
	public static final String SALE_REVERSE_TRAN_CONSTANT = "REVERSE";
	public static final String PRE_AUTHORIZATION_TRAN_CONSTANT = "PREPAID";
	public static final String BALANCE_TRAN_CONSTANT = "BALANCE";
	public static final String LOGOUT_TRAN_CONSTANT = "LOGOUT";

	public static final int GET_AMOUNT_REQUEST_CODE = 70;

	public static final String KEY_REQUEST_FROM_MISPOS = "mispos";
	public static final String KEY_AMOUNT = "amount";
	public static final String KEY_TRAN_TYPE = "typeId";
	public static final String KEY_INDEX_NO = "indexNo";
	public static final String KEY_PAYMENT_ID = "paymentId";

	// All In Pay product
	public static final String IP = "116.228.223.216";
	public static final int PORT = 10021;

	// All In Pay test
	// public static final String IP = "116.236.252.102";
	// public static final int PORT = 8880;

	private Thread thread;
	private String deliveryTranType = "";
	private String indexNo = "";
	private String amount = "";
	private String batchNo = "";
	private String traceNo = "";
	private String oriTxnId = "";
	private String paymentId = "";
	private boolean isExternalOrder;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (formData != null) {
			JSONObject jsObj;
			try {
				jsObj = formData.getJSONObject("data");
				deliveryTranType = jsObj.optString(KEY_TRAN_TYPE);
				indexNo = jsObj.optString("payKeyIndex");
				amount = jsObj.optString("transAmount");
				batchNo = jsObj.optString("batchNo");
				traceNo = jsObj.optString("traceNo");
				paymentId = jsObj.optString("paymentId");
				oriTxnId = jsObj.optString("txnId");
				isExternalOrder = jsObj.optBoolean("isExternalOrder");
			} catch (JSONException e) {
				e.printStackTrace();
			}

		} else {

			deliveryTranType = getIntent().getStringExtra(KEY_TRAN_TYPE);
			indexNo = getIntent().getStringExtra(KEY_INDEX_NO);
			amount = getIntent().getStringExtra(KEY_AMOUNT);
			paymentId = getIntent().getStringExtra(KEY_PAYMENT_ID);
		}

		// communication open
		int openResult = MisPosInterface.communicationOpen();
		if (openResult < 0) {
			batchCallBack();
		}
		findViews();

		thread = new DaemonThread();
		thread.start();
		// checkTranTypeFlow();
		mHandler.sendEmptyMessageDelayed(SEND_DELAYED_COMMAND_HANDLER, 1000);
	}

	private void checkTranTypeFlow() {
		if (deliveryTranType.equals(SALE_TRAN_CONSTANT)) {
			if (!TextUtils.isEmpty(amount)) {
				MisposOperationUtil.consume(amount);
			} else {

				Intent mIntent = new Intent(MisposController.this,
						TransAmountController.class);
				mIntent.putExtra(KEY_REQUEST_FROM_MISPOS,
						KEY_REQUEST_FROM_MISPOS);
				startActivityForResult(mIntent, GET_AMOUNT_REQUEST_CODE);
			}
		} else if (deliveryTranType.equals(BALANCE_TRAN_CONSTANT)) {
			MisposOperationUtil.getBalance();
		} else if (deliveryTranType.equals(SALE_REVERSE_TRAN_CONSTANT)) {
			MisposOperationUtil.consumeRevoke(amount, traceNo);
		} else if (deliveryTranType.equals(LOGOUT_TRAN_CONSTANT)) {
			MisposOperationUtil.unregistration();
		}
	}

	@Override
	public void onClickLeftButton(View view) {
		finish();
	}

	private void findViews() {
		commonMisposLayout = (LinearLayout) findViewById(R.id.commonMisposLayout);
		indicatorMisposLayout = (LinearLayout) findViewById(R.id.indicatorMisposLayout);
		balanceLayout = (LinearLayout) findViewById(R.id.balanceLayout);
		orderDetailslayout = (LinearLayout) findViewById(R.id.orderDetailslayout);
		commonTextView = (TextView) findViewById(R.id.commonTextView);
		amountTextView = (TextView) findViewById(R.id.amountTextView);
		
		if (isExternalOrder) {
			amountTextView.setVisibility(View.VISIBLE);
			amountTextView.setText(getResources().getString(R.string.mispos_amount_msg) + UtilForMoney.fen2yuan(amount));
		}
		
		balanceAmountTextView = (TextView) findViewById(R.id.balanceAmountTextView);

		// order details
		order_detail_tv_transAmount = (TextView) findViewById(R.id.order_detail_tv_transAmount);
		order_detail_tv_transDate = (TextView) findViewById(R.id.order_detail_tv_transDate);
		order_detail_tv_transType = (TextView) findViewById(R.id.order_detail_tv_transType);
		order_detail_tv_payType = (TextView) findViewById(R.id.order_detail_tv_payType);
		order_detail_tv_orderId = (TextView) findViewById(R.id.order_detail_tv_orderId);
		order_detail_tv_orderStatus = (TextView) findViewById(R.id.order_detail_tv_orderStatus);

		order_detail_btn_confirm = (Button) findViewById(R.id.order_detail_btn_confirm);
		order_detail_btn_confirm.setOnClickListener(this);

		setLeftButton(R.drawable.titlebar_btn_back);
	}

	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case SEND_DELAYED_COMMAND_HANDLER:
				MisposOperationUtil.registration();
				break;
			case RECEIVE_BEAN_DATA_HANDLER:
				MisposData beanData = (MisposData) msg.obj;
				String responseCode = beanData.getResponseCode();
				if (responseCode
						.equals(MisposOperationUtil.RESPONSE_CODE_SUCCESS)) {

					// get payment name
					Map<String, ?> map = UtilForDataStorage
							.readPropertyBySharedPreferences(
									MisposController.this, "paymentInfo");
					String paymentStr = (String) map.get(paymentId);
					String paymentName = "";
					try {
						if (!TextUtils.isEmpty(paymentStr)) {

							JSONObject jsonObj = new JSONObject(paymentStr);
							paymentName = jsonObj.getString("paymentName");
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					beanData.setPaymentName(paymentName);

					// get operator
					Map<String, ?> merchantMap = UtilForDataStorage
							.readPropertyBySharedPreferences(
									MisposController.this, "merchant");
					String operatorName = (String) merchantMap.get("operator");
					beanData.setOperatorId(operatorName);

					if (!TextUtils.isEmpty(beanData.getTransType())) {
						// Separate tran type
						if (beanData.getTransType().equals(
								MisposOperationUtil.TRAN_TYPE_SIGN_IN)) {
							// FIXME: write sign in state to local
							CacheDB cacheDB = CacheDB
									.getInstance(MisposController.this);
							if (cacheDB.isExistMerchantIdTermId(
									beanData.getMerchantId(),
									beanData.getTerminalId())) {
								checkTranTypeFlow();
							} else {
								beanData.setResponseMsg(getResources()
										.getString(
												R.string.mispos_check_bind_config_msg));
								showCommonMessage(beanData);
							}
						}

						// logout
						if (beanData.getTransType().equals(
								MisposOperationUtil.TRAN_TYPE_SIGN_OUT)) {
							if (!TextUtils.isEmpty(deliveryTranType)
									&& deliveryTranType
											.equals(LOGOUT_TRAN_CONSTANT)) {
								batchCallBack();
							}
						}

						if (beanData.getTransType().equals(
								MisposOperationUtil.TRAN_TYPE_CONSUMPTION)) {
							// FIXME: consumption write back to server and print
							// receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							handleExtenalOrder(beanData);
						}

						if (beanData
								.getTransType()
								.equals(MisposOperationUtil.TRAN_TYPE_CONSUMPTION_REVERSE)) {
							// FIXME: consumption reverse write back to server
							// and print receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							// handleExtenalOrder(beanData);
						}

						if (beanData
								.getTransType()
								.equals(MisposOperationUtil.TRAN_TYPE_PRE_AUTHORIZATION)) {
							// FIXME: pre authorization write back to server and
							// print receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							handleExtenalOrder(beanData);
						}

						if (beanData
								.getTransType()
								.equals(MisposOperationUtil.TRAN_TYPE_PRE_AUTHORIZATION_REVERSE)) {
							// FIXME: pre authorization reverse write back to
							// server and print receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							handleExtenalOrder(beanData);
						}

						if (beanData
								.getTransType()
								.equals(MisposOperationUtil.TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE)) {
							// FIXME: pre authorization complete write back to
							// server and print receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							handleExtenalOrder(beanData);
						}

						if (beanData
								.getTransType()
								.equals(MisposOperationUtil.TRAN_TYPE_PRE_AUTHORIZATION_COMPLETE_REVERSE)) {
							// FIXME: pre authorization complete reverse write
							// back to server and print receipt
							new PrinterThread(beanData).start();
							new WriteBackThread(beanData).start();
							showOrderDetails(beanData);
							handleExtenalOrder(beanData);
						}

						if (beanData.getTransType().equals(
								MisposOperationUtil.TRAN_TYPE_BALANCE)) {
							showBalance(beanData);
						}
					}
					/*
					 * if (isExternalOrder) { JSONObject jsObj = new
					 * JSONObject(); try { jsObj.put("refNo",
					 * beanData.getRefNo()); jsObj.put("transTime",
					 * beanData.getTranDate() + beanData.getTranTime());
					 * jsObj.put("paymentName", beanData.getPaymentName());
					 * jsObj.put("transAmount", beanData.getAmount());
					 * jsObj.put("bankCardNum", beanData.getCardNo()); } catch
					 * (JSONException e) { e.printStackTrace(); }
					 * onCall("Pay.misposSuccRestart", jsObj); }
					 */
				} else {
					if (isExternalOrder) {
						JSONObject jsObj = new JSONObject();
						try {
							jsObj.put("transAmount", beanData.getAmount());
						} catch (JSONException e) {
							e.printStackTrace();
						}
						onCall("Pay.misposErrRestart", jsObj);
					}
					commonTextView.setTextColor(getResources().getColor(
							R.color.black));
					showCommonMessage(beanData);

					if (!TextUtils.isEmpty(deliveryTranType)
							&& deliveryTranType.equals(LOGOUT_TRAN_CONSTANT)) {
						batchCallBack();
					}
				}

				break;
			default:
				break;
			}
		}

	};

	private int socket_send(byte[] send, int sendLen) {
		Socket sock;
		try {
			sock = new Socket(IP, PORT);
			OutputStream out = sock.getOutputStream();
			InputStream sin = sock.getInputStream();
			out.write(send, 0, sendLen);
			byte ibuf[] = new byte[1024];
			int len = sin.read(ibuf);
			MisPosInterface.sendMessage(ibuf, len);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private void message_process() {
		int recvDataLen = 0;
		byte[] recvData = new byte[1024];

		recvDataLen = MisPosInterface.recvMessage(recvData);
		Log.i(TAG, "recvDataLen: " + recvDataLen);

		try {
			if (recvDataLen != 0) {
				socket_send(recvData, recvDataLen);
			} else {
				Log.i(TAG, "response recvDataLen = 0");
				MisposData beanData = MisposOperationUtil.parseMisposData();
				Message msg = mHandler.obtainMessage();
				msg.obj = beanData;
				msg.what = RECEIVE_BEAN_DATA_HANDLER;
				mHandler.sendMessage(msg);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_mispos_layout);
	}

	@Override
	protected String getTitlebarTitle() {
		return "";
	}

	private void showOrderDetails(MisposData beanData) {
		hiddenLayouts();
		orderDetailslayout.setVisibility(View.VISIBLE);
		order_detail_tv_transAmount.setText(AppUtil.formatAmount(Long
				.parseLong(beanData.getAmount())));
		order_detail_tv_transDate.setText(MisposOperationUtil
				.parseDateTimeString(beanData.getTranDate(),
						beanData.getTranTime()));
		order_detail_tv_transType.setText(beanData.getTranTypeName());
		order_detail_tv_payType.setText(getResources().getString(
				R.string.mispos_str_mispos));
		order_detail_tv_orderId.setText(beanData.getRefNo());
		order_detail_tv_orderStatus.setText(getResources().getString(
				R.string.mispos_tran_success));

	}

	private void showBalance(MisposData beanData) {
		hiddenLayouts();
		balanceLayout.setVisibility(View.VISIBLE);
		balanceAmountTextView.setText(getResources().getString(
				R.string.mispos_str_balance)
				+ AppUtil.formatAmount(Long.parseLong(beanData.getAmount()))
				+ getResources().getString(R.string.mispos_str_dollar));
		balanceAmountTextView.setTextColor(getResources().getColor(
				R.color.black));
	}

	private void showCommonMessage(MisposData beanData) {
		hiddenLayouts();
		commonMisposLayout.setVisibility(View.VISIBLE);
		commonTextView.setText(beanData.getResponseMsg());
	}

	private void hiddenLayouts() {
		indicatorMisposLayout.setVisibility(View.GONE);
		commonMisposLayout.setVisibility(View.GONE);
		balanceLayout.setVisibility(View.GONE);
		orderDetailslayout.setVisibility(View.GONE);
	}

	private void handleExtenalOrder(MisposData beanData) {
		if (isExternalOrder) {
			JSONObject jsObj = new JSONObject();
			try {
				jsObj.put("refNo", beanData.getRefNo());
				jsObj.put("transTime",
						beanData.getTranDate() + beanData.getTranTime());
				jsObj.put("paymentName", beanData.getPaymentName());
				jsObj.put("transAmount", beanData.getAmount());
				jsObj.put("bankCardNum", beanData.getCardNo());
			} catch (JSONException e) {
				e.printStackTrace();
			}
			onCall("Pay.misposSuccRestart", jsObj);
		}
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
	}

	@Override
	protected boolean getRemoveJSTag() {
		return false;
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.order_detail_btn_confirm:
			finish();
			break;

		default:
			break;
		}
	}

	private void batchCallBack() {
		if (!TextUtils.isEmpty(deliveryTranType)
				&& deliveryTranType.equals(LOGOUT_TRAN_CONSTANT)) {
			// FIXME:sign out
			onCall("SettingsIndex.batchCallBack", null);
			finish();
		} else {
			Toast.makeText(MisposController.this,
					getResources().getString(R.string.mispos_str_reconnected),
					Toast.LENGTH_SHORT).show();
			finish();
		}
	}

	@Override
	protected void onDestroy() {
		// dismissLoading();
		MisPosInterface.communicationClose();
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i(TAG, "requestCode:" + requestCode + " --resultCode" + resultCode);
		if (requestCode == GET_AMOUNT_REQUEST_CODE
				&& resultCode == TransAmountController.RESULT_CODE_AMOUNT) {
			String amount = data.getStringExtra(KEY_AMOUNT);
			Log.i(TAG, "amount:" + amount);
			if (deliveryTranType.equals(SALE_TRAN_CONSTANT)) {
				MisposOperationUtil.consume(amount);
			}
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * <p>
	 * Title: MisposController.java
	 * </p>
	 * <p>
	 * Description: The daemon thread to receive data
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2014
	 * </p>
	 * <p>
	 * Company: KoolCloud
	 * </p>
	 * 
	 * @author Teddy
	 * @date 2014-7-2
	 * @version
	 */
	class DaemonThread extends Thread {

		@Override
		public void run() {
			while (true) {
				int ret = MisPosInterface.serialPoll(-1);
				Log.i(TAG, "serialPoll ret:" + ret);
				if (ret >= 0) {
					Log.i(TAG, "serialPoll succ");
					message_process();
				}
			}
		}
	}

	/**
	 * <p>
	 * Title: MisposController.java
	 * </p>
	 * <p>
	 * Description: printer thread
	 * </p>
	 * <p>
	 * Copyright: Copyright (c) 2014
	 * </p>
	 * <p>
	 * Company: KoolCloud
	 * </p>
	 * 
	 * @author Teddy
	 * @date 2014-7-2
	 * @version
	 */
	class PrinterThread extends Thread {
		private MisposData beanData;

		public PrinterThread(MisposData beanData) {
			this.beanData = beanData;
		}

		@Override
		public void run() {
			try {
				PrinterHelper.getInstance(MisposController.this)
						.printMisposReceipt(beanData);
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}

	class WriteBackThread extends Thread {
		private MisposData beanData;

		public WriteBackThread(MisposData beanData) {
			this.beanData = beanData;
		}

		@Override
		public void run() {
			// TODO:request network interface
			JSONObject req = new JSONObject();

			try {
				req.put("action", "txn/90");
				req.put("batchNo", beanData.getBatchNo()); // M
				req.put("transTime",
						beanData.getTranDate() + beanData.getTranTime());// M
				// req.put("traceNo", traceNo++);// M
				req.put("transType", beanData.getTransType());// M
				req.put("paymentId", paymentId);// M
				req.put("transAmount", beanData.getAmount());
				req.put("cardNo", beanData.getCardNo());
				req.put("resCode", beanData.getResponseCode());
				req.put("resMsg", beanData.getResponseMsg());
				req.put("refNo", beanData.getRefNo());
				req.put("authNo", beanData.getAuthNo());
				req.put("issuerId", beanData.getIssuerId());
				req.put("traceNo", beanData.getVoucherNo());

				if (deliveryTranType.equals(SALE_REVERSE_TRAN_CONSTANT)) {
					req.put("oriTxnId", oriTxnId);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			onCall("ConsumptionData.startSingleBatchTask", req);
		}
	}

}
