package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.koolcloud.pos.controller.BaseHomeController;
import cn.koolcloud.pos.wd.R;

public class PaymentMechanismController extends BaseHomeController {

	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCall("PaymentMechanismInfo.updateTransInfo", null);
	}

	@Override
	protected void initHomeTitlebar() {

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
			onCall("PaymentMechanismInfo.researchMethod", msg);
		}
	};

	@Override
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
				updateLayoutFuncModule(data);
			}
		}
	}

	@Override
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
			if (funcModuleId.equals("BALANCE")) {
				continue;
			}
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

	@Override
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

		JSONObject msg = new JSONObject();
		try {
			msg.put("typeId", tag);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		if (tag != preTag) {
			onCall("PaymentMechanismInfo.onSwitchFuncModule", msg);
		}
	}

	@Override
	public void onClickLeftButton(View view) {
		onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.base_home_layout);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_pay_mechanism_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_PaymentMechanismInfo);
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
