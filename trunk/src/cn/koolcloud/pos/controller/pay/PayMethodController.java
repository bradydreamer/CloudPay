package cn.koolcloud.pos.controller.pay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.widget.ViewPagerIndicator;

public class PayMethodController extends BaseController {
	private HorizontalScrollView sView;
	private ViewPagerIndicator pageIndicator;
	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		sView = (HorizontalScrollView) findViewById(R.id.pay_method_sView);
		sView.setHorizontalScrollBarEnabled(false);

		pageIndicator = (ViewPagerIndicator) findViewById(R.id.pay_method_indicator);

		onCall("PayMethod.reqProductInfo", null);
	}

	private OnClickListener btnClickListener = new OnClickListener() {

		@Override
		public void onClick(View view) {
			String tag = view.getTag().toString();

			JSONObject msg = new JSONObject();
			try {
				msg.put("tag", tag);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			onCall("PayMethod.onConfirmMethod", msg);
		}
	};

	private void setMethods(JSONArray mDataArray) {
		int count = mDataArray.length();
		int index = 0;
		int pages = 0;
		LinearLayout contentView = (LinearLayout) sView
				.findViewById(R.id.pay_method_sView_content);

		while (index < count) {
			LinearLayout table = (LinearLayout) LayoutInflater.from(this)
					.inflate(R.layout.table_method, null);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					sView.getWidth(), LinearLayout.LayoutParams.MATCH_PARENT);
			table.setLayoutParams(p);
			contentView.addView(table);
			pages++;

			for (int i = 0; i < table.getChildCount(); i++) {
				LinearLayout row = (LinearLayout) table.getChildAt(i);
				for (int j = 0; j < row.getChildCount(); j++) {
					LinearLayout methodBtn = (LinearLayout) row.getChildAt(j);
					JSONObject mData = mDataArray.optJSONObject(index);
					if (mData != null) {
						updateMethodBtn(methodBtn, mData.optString("tag"),
								mData.optString("title"),
								mData.optString("type"));
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

		pageIndicator.setPageSize(pages);
		if (pages == 1) {
			pageIndicator.setVisibility(View.INVISIBLE);
		} else {
			/*
			 * sView.setOnTouchListener(new OnTouchListener() {
			 * 
			 * @Override public boolean onTouch(View v, MotionEvent event) { if
			 * (v == sView) { return pageIndicator.onScroll(sView, event); }
			 * return false; } });
			 */
		}
	}

	private void updateMethodBtn(LinearLayout methodBtn, String tag,
			String title, String type) {
		for (int i = 0; i < methodBtn.getChildCount(); i++) {
			View v = methodBtn.getChildAt(i);
			if (v instanceof Button) {
				Button btn = (Button) v;
				btn.setTag(tag);
				btn.setOnClickListener(btnClickListener);
				if (type.equalsIgnoreCase("3")) {
					btn.setBackgroundResource(R.drawable.logo_ec);
				} else if (type.equalsIgnoreCase("4")) {
					btn.setBackgroundResource(R.drawable.logo_cp);
				} else if (type.equalsIgnoreCase("5")) {
					btn.setBackgroundResource(R.drawable.logo_delivery_voucher);
				} else if (type.equalsIgnoreCase("account")) {
					btn.setBackgroundResource(R.drawable.logo_allinpay);
				} else if (type.equalsIgnoreCase("swipeCard")) {
					btn.setBackgroundResource(R.drawable.logo_card);
				}
			} else if (v instanceof TextView) {
				TextView tv = (TextView) v;
				tv.setText(title);
			}
		}
	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("sView".equals(name)) {
			return sView;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if (sView.equals(view)) {
			if ("data".equals(key)) {
				JSONArray data = (JSONArray) value;
				setMethods(data);
			}
		}
		super.setView(view, key, value);
	}

	@Override
	public void onBackPressed() {
		onCall("PayMethod.clear", null);
		super.onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_pay_method_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_manual_goods_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_PayMethod);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_PayMethod);
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
