package cn.koolcloud.pos.controller;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.jni.PrinterInterface;
import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.HomePagerAdapter;
import cn.koolcloud.pos.controller.mispos.MisposController;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.pos.widget.ViewPagerIndicator;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.NumberUtil;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseHomeController extends BaseController {

	private static final String CASHCONSUME = "cash";
	protected LinearLayout layout_funcModule;
	public View selectedBtn;

	protected LinearLayout home_layout;

	protected ViewPager viewPager;
	protected ViewPagerIndicator pageIndicator;

	public final String INIT = "init";
	private Map<Integer, String> recordMap = new HashMap<Integer, String>();
	public static List<Activity> activityList = new LinkedList<Activity>();

	private int drawableId[] = new int[4];
	private String cashAmount = "0";
	private String changeAmountStr = "0.00";
	private String paymentId = null;
	private String paymentName = null;
	private String openBrh = null;
	private String openBrhName = null;
	private String brhMchtId = null;
	private String brhTermId = null;
	private String keyIndex = null;
	private EditText sumPayable = null;
	private EditText paidAmount = null;
	private TextView cashChange = null;
	private String paidHint = null;
	private String sumPayableHint = null;

	private boolean isPaymentClicked = false;
	private String typeId = null;

	DisplayImageOptions options = new DisplayImageOptions.Builder()
			.cacheInMemory(true).cacheOnDisc(true)
			.bitmapConfig(Bitmap.Config.RGB_565).build();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		initHomeTitlebar();
		// activity collections
		activityList.add(this);
		layout_funcModule = (LinearLayout) findViewById(R.id.home_layout_funcModule);
		home_layout = (LinearLayout) findViewById(R.id.home_layout);

		viewPager = (ViewPager) findViewById(R.id.home_viewpager);
		pageIndicator = (ViewPagerIndicator) findViewById(R.id.home_indicator);

		// start update trans info after devices checking on 23th May -- start
		// onCall("Home.updateTransInfo", null);
		// start update trans info after devices checking on 23th May -- end
	}

	protected void initHomeTitlebar() {

		/*
		 * home activity hide left and right title button.
		 */
		setTitleHidden();
		setLeftButton(R.drawable.ic_launcher_home_48);
		setRightButtonHidden();
	}

	@Override
	protected void setLeftButton(int resourceId) {
		Drawable leftPic = getResources().getDrawable(resourceId);
		leftPic.setBounds(0, 0, leftPic.getIntrinsicWidth(),
				leftPic.getMinimumHeight());
		titlebar_btn_left.setCompoundDrawables(leftPic, null, null, null);
		titlebar_btn_left.setText(getTitlebarTitle());
		titlebar_btn_left.setVisibility(View.VISIBLE);
		titlebar_btn_left.setBackgroundDrawable(null);
	}

	@Override
	public void onClickLeftButton(View view) {

	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("viewPager".equals(name)) {
			return viewPager;
		} else if ("layout_funcModule".equals(name)) {
			return layout_funcModule;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if (viewPager.equals(view)) {
			if ("data".equals(key)) {
				JSONArray data = (JSONArray) value;
				if (data == null)
					return;
				JSONObject jsonData = null;
				try {
					jsonData = data.getJSONObject(0);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				// if ((jsonData.optString("typeId")).equals(CASHCONSUME)) {
				// setMethods(data, cashAmount);
				// } else {
				setMethods(data);
				// }
			}
		} else if (layout_funcModule.equals(view)) {
			if ("data".equals(key)) {
				JSONArray data = (JSONArray) value;
				/**
				 * init button background
				 */
				// changeButtonBackground(INIT, data.length());
				updateLayoutFuncModule(data);
			}
		}
		super.setView(view, key, value);
	}

	@Override
	protected void setCashAmount(String cashAmount) {
		// TODO Auto-generated method stub
		this.cashAmount = cashAmount;
	}

	protected void updateLayoutFuncModule(JSONArray mDataArray) {
		layout_funcModule.removeAllViews();
		for (int i = 0; i < mDataArray.length(); i++) {
			Button btnFuncModule = (Button) LayoutInflater.from(this).inflate(
					R.layout.home_btn_funcmodule,
					(LinearLayout) findViewById(R.id.home_layout_funcModule),
					false);
			JSONObject moduleData = mDataArray.optJSONObject(i);
			String funcModuleName = moduleData.optString("typeName");
			String funcModuleId = moduleData.optString("typeId");
			btnFuncModule.setText(funcModuleName);
			btnFuncModule.setTag(funcModuleId);
			layout_funcModule.addView(btnFuncModule);
			/*
			 * add button record.
			 */
			addButtonRecording(i, funcModuleId);
			if (0 == i) {
				selectedBtn = btnFuncModule;
				selectedBtn
						.setBackgroundResource(R.drawable.main_page_bg_bitmap);
				btnFuncModule.setSelected(true);
			}
		}
	}

	protected void setMethods(JSONArray mDataArray) {
		Log.d(TAG, "setMethods mDataArray : " + mDataArray);
		int count = mDataArray.length();
		int index = 0;
		int pages = 0;
		LayoutInflater inflater = LayoutInflater.from(this);

		ArrayList<View> viewList = new ArrayList<View>();
		while (index < count) {
			LinearLayout table = (LinearLayout) LayoutInflater.from(this)
					.inflate(R.layout.table_method, null);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			table.setLayoutParams(p);
			viewList.add(table);
			pages++;

			for (int i = 0; i < table.getChildCount(); i++) {
				LinearLayout row = (LinearLayout) table.getChildAt(i);
				for (int j = 0; j < row.getChildCount(); j++) {
					LinearLayout methodBtn = (LinearLayout) row.getChildAt(j);
					JSONObject mData = mDataArray.optJSONObject(index);
					if (mData != null) {
						updateMethodBtn(methodBtn, mData);
					}
					index++;
					if (index == count) {
						break;
					}
				}
				if (index == count) {
					break;
				}
			}
		}
		pageIndicator.setViewPager(viewPager);
		pageIndicator.setPageSize(pages);
		pageIndicator.setCurrentPage(0);
		if (pages == 1) {
			pageIndicator.setVisibility(View.INVISIBLE);
		} else {
			pageIndicator.setVisibility(View.VISIBLE);
		}
		viewPager.setAdapter(new HomePagerAdapter(viewList));
	}

	protected void setMethods(JSONArray mDataArray, String cashAmount) {
		int pages = 0;
		JSONObject mData = mDataArray.optJSONObject(0);
		paymentId = mData.optString("paymentId");
		paymentName = mData.optString("paymentName");
		openBrh = mData.optString("openBrh");
		openBrhName = mData.optString("openBrhName");
		brhMchtId = mData.optString("brhMchtId");
		brhTermId = mData.optString("brhTermId");
		keyIndex = mData.optString("brhKeyIndex");
		typeId = mData.optString("typeId");
		LayoutInflater inflater = LayoutInflater.from(this);
		ArrayList<View> viewList = new ArrayList<View>();
		View myView = inflater.inflate(R.layout.cash_consume, null);
		sumPayable = (EditText) myView.findViewById(R.id.cash_sum_payable);
		paidAmount = (EditText) myView.findViewById(R.id.cash_paid_amount);
		cashChange = (TextView) myView.findViewById(R.id.cash_change_amount);
		paidHint = paidAmount.getHint().toString();
		sumPayableHint = sumPayable.getHint().toString();
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

				String paidAmountStr = paidAmount.getText().toString();
				String sumPayableStr = sumPayable.getText().toString();

				sumPayable.setSelection(sumPayableStr.length());
				Boolean needGoOn = checkAmount(sumPayableStr, sumPayable);
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

				paidAmount.setSelection(paidAmountStr.length());
				Boolean needGoOn = checkAmount(paidAmountStr, paidAmount);
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

		if (cashAmount != null) {
			if (!cashAmount.equals("0")) {
				sumPayable.setText(UtilForMoney.fen2yuan(cashAmount));
			}
		}
		viewList.add(myView);
		pages++;
		pageIndicator.setViewPager(viewPager);
		pageIndicator.setPageSize(pages);
		pageIndicator.setCurrentPage(0);
		if (pages == 1) {
			pageIndicator.setVisibility(View.INVISIBLE);
		} else {
			pageIndicator.setVisibility(View.VISIBLE);
		}
		viewPager.setAdapter(new HomePagerAdapter(viewList));
	}

	private Boolean checkAmount(String amount, EditText edit) {
		String amountStr;
		String[] amStr = amount.split("\\.");
		if (amount.length() == 1 && amount.equals("0")) {
			edit.setText("0.00");
			return false;
		}
		if (amStr.length == 2) {
			if (amStr[1].length() > 2) {
				BigDecimal b1 = new BigDecimal(amount);
				BigDecimal b2 = new BigDecimal("1000");
				amountStr = String.valueOf(b1.multiply(b2).intValue());
				edit.setText(NumberUtil.mul(amountStr, "0.01"));
				return false;
			} else if (amStr[1].length() == 1) {
				BigDecimal b1 = new BigDecimal(amount);
				BigDecimal b2 = new BigDecimal("10");
				amountStr = String.valueOf(b1.multiply(b2).intValue());
				edit.setText(NumberUtil.mul(amountStr, "0.01"));
				return false;
			} else {
				amountStr = amount;
				return true;
			}
		} else {
			amountStr = NumberUtil.add("0.0" + amount, "0.00");
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

	protected void updateMethodBtn(LinearLayout methodBtn, JSONObject data) {
		for (int i = 0; i < methodBtn.getChildCount(); i++) {
			View v = methodBtn.getChildAt(i);
			if (v instanceof ImageView) {
				ImageView iv = (ImageView) v;
				iv.setTag(data.toString());
				iv.setOnClickListener(btnClickListener);
				String imageName = data.optString("imgName");
				matchLogo(iv, imageName);
			} else {
				TextView tv = (TextView) v;
				tv.setText(data.optString("paymentName"));
			}
		}
	}

	protected void matchLogo(ImageView iv, String imageName) {
		if (imageName.startsWith("http")) {
			ImageLoader.getInstance().displayImage(imageName, iv, options);
		} else {
			if (imageName.startsWith("logo_ec")) {
				iv.setImageResource(R.drawable.logo_ec);
			} else if (imageName.startsWith("logo_cp")) {
				iv.setImageResource(R.drawable.logo_cp);
			} else if (imageName.startsWith("logo_delivery_voucher")) {
				iv.setImageResource(R.drawable.logo_delivery_voucher);
			} else if (imageName.startsWith("logo_allinpay")) {
				iv.setImageResource(R.drawable.logo_allinpay);
			} else if (imageName.startsWith("logo_alipay")) {
				iv.setImageResource(R.drawable.logo_alipay);
			} else if (imageName.startsWith("logo_card")) {
				iv.setImageResource(R.drawable.logo_card);
			} else if (imageName.startsWith("logo_cup")) {
				iv.setImageResource(R.drawable.logo_cup);
			} else if (imageName.startsWith("logo_quickpay")) {
				iv.setImageResource(R.drawable.logo_quickpay);
			} else if (imageName.startsWith("logo_search_balance")) {
				iv.setImageResource(R.drawable.logo_search_balance);
			} else if (imageName.startsWith("logo_test")) {
				iv.setImageResource(R.drawable.logo_test);
			} else if (imageName.startsWith("logo_cash")) {
				iv.setImageResource(R.drawable.logo_cash);
			} else if (imageName.startsWith("logo_unionpay")) {
				iv.setImageResource(R.drawable.logo_unionpay);
			} else if (imageName.startsWith("logo_wechat")) {
				iv.setImageResource(R.drawable.logo_wechat);
			} else if (imageName.startsWith("logo_fufeitong")) {
				iv.setImageResource(R.drawable.logo_fufeitong);
			} else if (imageName.startsWith("logo_coupon")) {
				iv.setImageResource(R.drawable.logo_coupon);
			} else if (imageName.startsWith("logo_rm_coupon")) {
				iv.setImageResource(R.drawable.logo_rm_coupon);
			} else if (imageName.startsWith("logo_prepaid_card")) {
				iv.setImageResource(R.drawable.logo_prepaid_card);
			} else if (imageName.startsWith("logo_openunion")) {
				iv.setImageResource(R.drawable.logo_openunion);
			} else if (imageName.startsWith("logo_xunlian")) {
				iv.setImageResource(R.drawable.logo_xunlian);
			} else if (imageName.startsWith("logo_payfortune")) {
				iv.setImageResource(R.drawable.logo_payfortune);
			} else if (imageName.startsWith("amex")) {
				iv.setImageResource(R.drawable.amex);
			} else if (imageName.startsWith("boc")) {
				iv.setImageResource(R.drawable.boc);
			} else if (imageName.startsWith("dinersclub")) {
				iv.setImageResource(R.drawable.dinersclub);
			} else if (imageName.startsWith("discover")) {
				iv.setImageResource(R.drawable.discover);
			} else if (imageName.startsWith("hsb")) {
				iv.setImageResource(R.drawable.hsb);
			} else if (imageName.startsWith("icbc")) {
				iv.setImageResource(R.drawable.icbc);
			} else if (imageName.startsWith("jcb")) {
				iv.setImageResource(R.drawable.jcb);
			} else if (imageName.startsWith("master")) {
				iv.setImageResource(R.drawable.master);
			} else if (imageName.startsWith("visa")) {
				iv.setImageResource(R.drawable.visa);
			} else if (imageName.startsWith("Asia-Miles")) {
				iv.setImageResource(R.drawable.asia_miles);
			} else if (imageName.startsWith("Caltex")) {
				iv.setImageResource(R.drawable.caltex);
			} else if (imageName.startsWith("Carrefour")) {
				iv.setImageResource(R.drawable.carrefour);
			} else if (imageName.startsWith("city'super")) {
				iv.setImageResource(R.drawable.city_super);
			} else if (imageName.startsWith("CNPC")) {
				iv.setImageResource(R.drawable.cnpc);
			} else if (imageName.startsWith("CODE")) {
				iv.setImageResource(R.drawable.code);
			} else if (imageName.startsWith("Dickson")) {
				iv.setImageResource(R.drawable.dickson);
			} else if (imageName.startsWith("Esso")) {
				iv.setImageResource(R.drawable.esso);
			} else if (imageName.startsWith("Harvey-Nichols")) {
				iv.setImageResource(R.drawable.harvey_nichols);
			} else if (imageName.startsWith("Lane-Crawford")) {
				iv.setImageResource(R.drawable.lane_crawford);
			} else if (imageName.startsWith("NWDS")) {
				iv.setImageResource(R.drawable.nwds);
			} else if (imageName.startsWith("Shell")) {
				iv.setImageResource(R.drawable.shell);
			} else if (imageName.startsWith("SINOPEC")) {
				iv.setImageResource(R.drawable.sinopec);
			} else if (imageName.startsWith("Wal-Mart")) {
				iv.setImageResource(R.drawable.wal_mart);
			} else if (imageName.startsWith("Wing-On")) {
				iv.setImageResource(R.drawable.wing_on);
			} else if (imageName.startsWith("logo_transfer_fufeitong")) {
                iv.setImageResource(R.drawable.logo_fufeitong_transfer);
            }
		}
	}

	private OnClickListener btnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			String tag = view.getTag().toString();
			// get index no (90) from tag then using for mispos --start mod by
			// Teddy on 1th July

			String indexNo = "";
			String tranType = "";
			String paymentId = "";
			String typeId = "";
			String misc = "";
			JSONObject msg = new JSONObject();
			JSONObject tagObj = null;
			UtilFor8583 util8583 = UtilFor8583.getInstance();
			try {
				tagObj = new JSONObject(tag);
				// indexNo = "90";
				indexNo = tagObj.getString("brhKeyIndex");
				if (!indexNo.equals("")) {
					util8583.terminalConfig.setKeyIndex(indexNo);
					tranType = tagObj.getString(MisposController.KEY_TRAN_TYPE);
					paymentId = tagObj.getString("paymentId");
					typeId = tagObj.getString("typeId");
					misc = tagObj.optString("misc");
					msg.put("tag", tag);
				} else {
					msg.put("error", "167");
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (!TextUtils.isEmpty(indexNo)
					&& indexNo.equals(ConstantUtils.MISPOS_INDEX)) {
				// Intent mIntent = new Intent(BaseHomeController.this,
				// MisposController.class);
				// mIntent.putExtra(MisposController.KEY_TRAN_TYPE, tranType);
				// mIntent.putExtra(MisposController.KEY_INDEX_NO, indexNo);
				// mIntent.putExtra(MisposController.KEY_PAYMENT_ID, paymentId);
				// startActivity(mIntent);

				try {
					JSONObject paramObj = new JSONObject();

					paramObj.put("payKeyIndex", indexNo);
					paramObj.put("paymentId", paymentId);
					if (!TextUtils.isEmpty(misc) && misc.equals(ConstantUtils.MISPOS_MISC)) {
						paramObj.put("misc", misc);
                        paramObj.put("typeId", ConstantUtils.MISPOS_TRAN_TYPE);
                    } else {
                        paramObj.put("typeId", tranType);
                    }

					if (!isPaymentClicked) {

						onCall("window.util.showMisposWithLoginChecked", paramObj);
						isPaymentClicked = true;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else if (!TextUtils.isEmpty(misc)
					&& misc.equalsIgnoreCase("rm_coupon")) {
				JSONObject paramObj = null;
				try {
					paramObj = new JSONObject();
					paramObj.put("typeId", tranType);
					paramObj.put("payKeyIndex", indexNo);
					paramObj.put("paymentId", paymentId);
					paramObj.put("coupon_type", "rm_coupon");
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				onCall("window.util.showCouponWithLoginChecked", paramObj);
			}  else if (!TextUtils.isEmpty(misc)
                    && misc.equalsIgnoreCase("rm_coupon_wan")) {
                JSONObject paramObj = null;
                try {
                    paramObj = new JSONObject();
                    paramObj.put("typeId", tranType);
                    paramObj.put("payKeyIndex", indexNo);
                    paramObj.put("paymentId", paymentId);
                    paramObj.put("coupon_type", "rm_coupon_wan");
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                onCall("window.util.showWanCouponWithLoginChecked", paramObj);
            } else {
				if (!isPaymentClicked) {
					onCall("PayMethod.onConfirmMethod", msg);
					isPaymentClicked = true;
				}
			}

			// get index no (90) from tag then using for mispos --end mod by
			// Teddy on 1th July
		}
	};

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
			traceNo = "0";
		} else {
			traceId = ((Integer) map.get("transId")).intValue();
			traceNo = dataFormat.format(traceId);
		}
		if (null == map.get("batchId")) {
			batchNo = "0";
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
		openCashBox();
		try {
			msg.put("transAmount", UtilForMoney.yuan2fen(sumPayableStr));
			msg.put("cashPaidAmount", UtilForMoney.yuan2fen(paidAmountStr));
			msg.put("changeAmount", UtilForMoney.yuan2fen(changeAmountStr));
			msg.put("transTime", transTime);
			msg.put("transType", transType);
			msg.put("batchNo", batchNo);
			msg.put("traceNo", traceNo);
			msg.put("resCode", resCode);
			msg.put("keyIndex", keyIndex);
			msg.put("paymentId", paymentId);
			msg.put("paymentName", paymentName);
			msg.put("openBrh", openBrh);
			msg.put("openBrhName", openBrhName);
			msg.put("brhMchtId", brhMchtId);
			msg.put("brhTermId", brhTermId);
			msg.put("typeId", typeId);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// sumPayable.setText("");
		// sumPayable.setHint(sumPayableHint);
		paidAmount.setText("");
		paidAmount.setHint(paidHint);
		paidAmount.setCursorVisible(false);
		onCall("Pay.cashSuccRestart", msg);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if (paidAmount != null) {
			paidAmount.setCursorVisible(true);
			paidAmount.setHint("");
		}

		isPaymentClicked = false;
		super.onResume();
	}

	public void onSwitchFuncModule(View view) {
		if (selectedBtn == view) {
			return;
		}
		String tag = view.getTag().toString();
		String preTag = "";

		if (selectedBtn != null) {
			selectedBtn.setSelected(false);
			selectedBtn
					.setBackgroundResource(R.drawable.main_btn_release_bitmap);
			preTag = selectedBtn.getTag().toString();
		}

		view.setSelected(true);
		view.setBackgroundResource(R.drawable.main_page_bg_bitmap);
		selectedBtn = view;
		/*
		 * change button background
		 */
		// changeButtonBackground(tag, 0);
		JSONObject msg = new JSONObject();
		try {
			msg.put("typeId", tag);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (tag != preTag) {
			onCall("Home.onSwitchFuncModule", msg);
		}
	}

	/**
	 * record button tag.
	 * 
	 * @param key
	 * @param value
	 */
	protected void addButtonRecording(Integer key, String value) {
		recordMap.put(key, value);
	}

	/**
	 * get the button index for background
	 * 
	 * @param value
	 * @return
	 */
	private int getRecordKey(String value) {
		int key = -1;

		for (int i = 0; i < recordMap.size(); i++) {
			if (recordMap.get(i).equals(value)) {
				key = i;
				break;
			}
		}
		return key;
	}

	/**
	 * change button background
	 * 
	 * @param value
	 */
	protected void changeButtonBackground(String value, int count) {

		if (value.equals(INIT)) {
			switch (count) {
			case 1:
				for (int i = 0; i < count; i++) {
					drawableId[i] = R.drawable.funcmodule_button_bg1_0 + i;
				}
				break;
			case 2:
				for (int i = 0; i < count; i++) {
					drawableId[i] = R.drawable.funcmodule_button_bg2_0 + i;
				}
				break;
			case 3:
				for (int i = 0; i < count; i++) {
					drawableId[i] = R.drawable.funcmodule_button_bg3_0 + i;
				}
				break;
			default:
				break;
			}
			layout_funcModule.setBackgroundResource(drawableId[0]);

		} else {

			switch (getRecordKey(value)) {
			case 0:
				layout_funcModule.setBackgroundResource(drawableId[0]);
				break;
			case 1:
				layout_funcModule.setBackgroundResource(drawableId[1]);
				break;
			case 2:
				layout_funcModule.setBackgroundResource(drawableId[2]);
				break;
			case 3: // need to do.
			case 4:
			case 5:
			default:
				break;
			}
		}
	}

	protected void exit() {
		if (activityList != null && activityList.size() > 0) {

			for (int i = 0; i < activityList.size(); i++) {
				Activity activity = activityList.get(i);
				if (activity != null) {
					activity.finish();
				}
			}
		}
		// clear temporary package name cache
		((MyApplication) getApplication()).setPkgName("");

	}
}
