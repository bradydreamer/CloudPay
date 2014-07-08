package cn.koolcloud.pos.controller;

import java.util.ArrayList;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.HomePagerAdapter;
import cn.koolcloud.pos.controller.mispos.MisposController;
import cn.koolcloud.pos.widget.ViewPagerIndicator;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

public abstract class BaseHomeController extends BaseController {

	protected LinearLayout layout_funcModule;
	public View selectedBtn;

	protected LinearLayout home_layout;

	protected ViewPager viewPager;
	protected ViewPagerIndicator pageIndicator;

	public final String INIT = "init";
	private Map<Integer, String> recordMap = new HashMap<Integer, String>();
	public static List<Activity> activityList = new LinkedList<Activity>();

	private int drawableId[] = new int[4];

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
				setMethods(data);
			}
		} else if (layout_funcModule.equals(view)) {
			if ("data".equals(key)) {
				JSONArray data = (JSONArray) value;
				/**
				 * init button background
				 */
				changeButtonBackground(INIT, data.length());
				updateLayoutFuncModule(data);
			}
		}
		super.setView(view, key, value);
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
				btnFuncModule.setSelected(true);
			}
		}
	}

	protected void setMethods(JSONArray mDataArray) {
		Log.d(TAG, "setMethods mDataArray : " + mDataArray);
		int count = mDataArray.length();
		int index = 0;
		int pages = 0;

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

	protected void updateMethodBtn(LinearLayout methodBtn, JSONObject data) {
		for (int i = 0; i < methodBtn.getChildCount(); i++) {
			View v = methodBtn.getChildAt(i);
			if (v instanceof ImageView) {
				ImageView iv = (ImageView) v;
				iv.setTag(data.toString());
				iv.setOnClickListener(btnClickListener);
				String imageName = data.optString("imgName");
				if (imageName.startsWith("http")) {
					ImageLoader.getInstance().displayImage(imageName, iv,
							options);
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
					} else if (imageName.startsWith("logo_unionpay")) {
						iv.setImageResource(R.drawable.logo_unionpay);
					} else if (imageName.startsWith("logo_wechat")) {
						iv.setImageResource(R.drawable.logo_wechat);
					} else if (imageName.startsWith("logo_fufeitong")) {
						iv.setImageResource(R.drawable.logo_fufeitong);
					}
					// iv.setBackgroundResource(R.drawable.icon_bg);
				}
			} else {
				TextView tv = (TextView) v;
				tv.setText(data.optString("paymentName"));
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
			JSONObject msg = new JSONObject();
			try {
				JSONObject tagObj = new JSONObject(tag);
				// indexNo = "90";
				indexNo = tagObj.getString("brhKeyIndex");
				tranType = tagObj.getString(MisposController.KEY_TRAN_TYPE);
				paymentId = tagObj.getString("paymentId");

				msg.put("tag", tag);
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
					paramObj.put("typeId", tranType);
					paramObj.put("payKeyIndex", indexNo);
					paramObj.put("paymentId", paymentId);

					onCall("window.util.showMisposWithLoginChecked", paramObj);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			} else {
				onCall("PayMethod.onConfirmMethod", msg);
			}

			// get index no (90) from tag then using for mispos --end mod by
			// Teddy on 1th July
		}
	};

	public void onSwitchFuncModule(View view) {
		if (selectedBtn == view) {
			return;
		}
		String tag = view.getTag().toString();
		String preTag = "";

		if (selectedBtn != null) {
			selectedBtn.setSelected(false);
			preTag = selectedBtn.getTag().toString();
		}

		view.setSelected(true);
		selectedBtn = view;
		/*
		 * change button background
		 */
		changeButtonBackground(tag, 0);
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
	}
}
