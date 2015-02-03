package cn.koolcloud.pos.controller.pay;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.NumberUtil;

public class CashConsumeController extends BaseController {

	private static final int EDITTYPE_SUMPAYABLE = 1;
	private static final int EDITTYPE_PAIDAMOUNT = 2;
	private boolean removeJSTag = true;

	private JSONObject data;
	private EditText sumPayable = null;
	private EditText paidAmount = null;
	private TextView cashChange = null;
	private Button cashOKbtn = null;
	private String paidHint = null;
	private String sumPayableHint = null;
	private String cashAmount = null;
	private Boolean cashChangeEnable = true;
	private String changeAmountStr = "0.00";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}

		data = formData.optJSONObject(getString(R.string.formData_key_data));

		setTitle(formData.optString(getString(R.string.formData_key_title)));
		sumPayable = (EditText) findViewById(R.id.cash_sum_payable);
		paidAmount = (EditText) findViewById(R.id.cash_paid_amount);
		cashChange = (TextView) findViewById(R.id.cash_change_amount);
		cashOKbtn = (Button) findViewById(R.id.cashOKbtn);
		paidHint = paidAmount.getHint().toString();
		sumPayableHint = sumPayable.getHint().toString();

		if (data != null) {
			cashChangeEnable = data.optBoolean("cashChangeEnable",true);
			cashAmount = data.optString("maxAmount","0");
			if (!cashAmount.equals("0") && cashAmount != null) {
				sumPayable.setText(UtilForMoney.fen2yuan(cashAmount));
				sumPayable.setSelection(UtilForMoney.fen2yuan(cashAmount)
						.length());
			}
			if(!cashChangeEnable){
				sumPayable.setEnabled(cashChangeEnable);
			}
		}

		setCashListener();
	}

	private void setCashListener() {
		paidAmount.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String paidAmountStr = paidAmount.getText().toString();
				paidAmount.setSelection(paidAmountStr.length());
			}

		});
		paidAmount.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				paidAmount.requestFocus();
				String paidAmountStr = paidAmount.getText().toString();
				paidAmount.setSelection(paidAmountStr.length());
				return false;
			}

		});
		sumPayable.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String sumPayableStr = sumPayable.getText().toString();
				sumPayable.setSelection(sumPayableStr.length());
			}

		});
		sumPayable.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				sumPayable.requestFocus();
				String sumPayableStr = sumPayable.getText().toString();
				sumPayable.setSelection(sumPayableStr.length());
				return false;
			}

		});
		sumPayable.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String change = "0.00";
				String subStr = "0";

				String paidAmountStr = paidAmount.getText().toString();
				String sumPayableStr = sumPayable.getText().toString();

				sumPayable.setSelection(sumPayableStr.length());
				Boolean needGoOn = checkAmount(sumPayableStr, sumPayable,EDITTYPE_SUMPAYABLE);
				if (!needGoOn) {
					return;
				}
				if (paidAmountStr.equals("")) {
					paidAmountStr = "0.00";
				}
				change = NumberUtil.sub(paidAmountStr, sumPayableStr);
				changeAmountStr = change;
				cashChange.setText(change);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});
		paidAmount.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String change = "0.00";
				String paidAmountStr = paidAmount.getText().toString();
				String sumPayableStr = sumPayable.getText().toString();
				if(paidAmountStr.equals("")){
					return;
				}
				paidAmount.setSelection(paidAmountStr.length());
				Boolean needGoOn = checkAmount(paidAmountStr, paidAmount,EDITTYPE_PAIDAMOUNT);
				if (!needGoOn) {
					return;
				}

				if (sumPayableStr.equals("")) {
					sumPayableStr = "0.00";
				}
				change = NumberUtil.sub(paidAmountStr, sumPayableStr);
				changeAmountStr = change;
				cashChange.setText(change);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

		});

		paidAmount.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (!hasFocus) {
					paidAmount.setHint(paidHint);
				} else {
					paidAmount.setCursorVisible(true);
					paidAmount.setHint("");
				}
			}

		});
		sumPayable.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (!hasFocus) {
					sumPayable.setHint(sumPayableHint);
				} else {
					sumPayable.setCursorVisible(true);
					sumPayable.setHint("");
				}
			}

		});

	}

	/**
	 *
	 * @param amount
	 * @param edit
	 * @param type 1：表示应付金额，2：表示实付金额
	 * @return
	 */
	private Boolean checkAmount(String amount, EditText edit,int type) {
		String amountStr;
		String subStr = "0";
		String[] amStr = amount.split("\\.");
		if (amount.length() == 1 && (amount.equals("0") || amount.equals("."))) {
			edit.setText("0.00");
			return false;
		}
		if (amStr.length == 2) {
			if (amStr[1].length() > 2) {
				BigDecimal b1 = new BigDecimal(amount);
				BigDecimal b2 = new BigDecimal("1000");
				amountStr = String.valueOf(b1.multiply(b2).intValue());
				amountStr = NumberUtil.mul(amountStr, "0.01");
				if(type == 1) {
					if (!cashAmount.equals("0") && !cashAmount.equals("0.00") && cashAmount != null) {
						subStr = NumberUtil.sub(UtilForMoney.fen2yuan(cashAmount), amountStr);
						if (subStr.startsWith("-")) {
							edit.setText(UtilForMoney.fen2yuan(cashAmount));
							amountStr = UtilForMoney.fen2yuan(cashAmount);
						}
					}
				}
				edit.setText(amountStr);
				return false;
			} else if (amStr[1].length() == 1) {
				BigDecimal b1 = new BigDecimal(amount);
				BigDecimal b2 = new BigDecimal("10");
				amountStr = String.valueOf(b1.multiply(b2).intValue());
				amountStr = NumberUtil.mul(amountStr, "0.01");
				if(type == 1) {
					if (!cashAmount.equals("0") && !cashAmount.equals("0.00") && cashAmount != null) {
						subStr = NumberUtil.sub(UtilForMoney.fen2yuan(cashAmount), amountStr);
						if (subStr.startsWith("-")) {
							edit.setText(UtilForMoney.fen2yuan(cashAmount));
							amountStr = UtilForMoney.fen2yuan(cashAmount);
						}
					}
				}
				edit.setText(amountStr);
				return false;
			} else {
				amountStr = amount;
				if(type == 1) {
					if (!cashAmount.equals("0") && !cashAmount.equals("0.00") && cashAmount != null) {
						subStr = NumberUtil.sub(UtilForMoney.fen2yuan(cashAmount), amountStr);
						if (subStr.startsWith("-")) {
							edit.setText(UtilForMoney.fen2yuan(cashAmount));
							amountStr = UtilForMoney.fen2yuan(cashAmount);
						}
					}
				}
				return true;
			}
		} else {
			amountStr = NumberUtil.add("0.0" + amount, "0.00");
			if(type == 1) {
				if (!cashAmount.equals("0") && !cashAmount.equals("0.00") && cashAmount != null) {
					subStr = NumberUtil.sub(UtilForMoney.fen2yuan(cashAmount), amountStr);
					if (subStr.startsWith("-")) {
						edit.setText(UtilForMoney.fen2yuan(cashAmount));
						amountStr = UtilForMoney.fen2yuan(cashAmount);
					}
				}
			}
			edit.setText(amountStr);
			return false;
		}
	}

	private void openCashBox() {
		byte[] openCashBox = { 0x1B, 0x70, 0x00, (byte) 0xC8, (byte) 0xC8 };
		try {
			PrinterInterface.open();
			PrinterInterface.set(1);
			PrinterInterface.begin();
			PrinterInterface.end();
			PrinterInterface.begin();
			PrinterHelper.getInstance(this).printerWrite(openCashBox);//
		} finally {
			PrinterInterface.end();
			PrinterInterface.close();
		}
	}

	public void onClickBtnOK(View view) {
		JSONObject msg = new JSONObject();
		String transTime = null;
		String transType = null;
		String batchNo = null;
		String traceNo = null;
		int traceId = 0;
		String resCode = "00";
		Date now = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// 可以方便地修改日期格式
		System.setProperty("user.timezone", "GMT+8");
		transTime = dateFormat.format(now);
		transType = "1021";
		DecimalFormat dataFormat = new DecimalFormat("000000");

		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(MyApplication.getContext(),
						"merchant");
		if (null == map.get("transId")) {
			traceNo = "000000";
		} else {
			traceId = ((Integer) map.get("transId")).intValue();
			traceNo = dataFormat.format(traceId);
		}
		if (null == map.get("batchId")) {
			batchNo = "000000";
		} else {
			batchNo = dataFormat.format(((Integer) map.get("batchId"))
					.intValue());
		}

		Map<String, Object> newMerchantMap = new HashMap<String, Object>();
		newMerchantMap.put("transId", Integer.valueOf(traceId + 1));
		UtilForDataStorage.savePropertyBySharedPreferences(
				MyApplication.getContext(), "merchant", newMerchantMap);
		String sumPayableStr = sumPayable.getText().toString();
		String paidAmountStr = paidAmount.getText().toString();
		if (paidAmountStr.equals("")) {
			return;
		}
		if (sumPayableStr.equals("")) {
			sumPayableStr = "0.00";
		}
		if (paidAmountStr.equals("")) {
			paidAmountStr = "0.00";
		}
		if(!changeAmountStr.startsWith("-")) {
			openCashBox();
		}
		try {
			msg.put("transAmount", UtilForMoney.yuan2fen(sumPayableStr));
			msg.put("cashPaidAmount", UtilForMoney.yuan2fen(paidAmountStr));
			msg.put("changeAmount", UtilForMoney.yuan2fen(changeAmountStr));
			msg.put("transTime", transTime);
			msg.put("transType", transType);
			msg.put("batchNo", batchNo);
			msg.put("traceNo", traceNo);
			msg.put("resCode", resCode);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		paidAmount.setText("");
		paidAmount.setHint(paidHint);
		sumPayable.requestFocus();
		cashChange.setText("0.00");
		cashOKbtn.setEnabled(false);
		onCall("CashConsume.cashExePay", msg);
	}

	@Override
	public void onResume(){
		super.onResume();
		cashOKbtn.setEnabled(true);
	}

	@Override
	public void onBackPressed() {
		onCall("CashConsume.clear", null);
		super.onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.cash_consume);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_cash_consume_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_CashConsume);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_CashConsume);
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
