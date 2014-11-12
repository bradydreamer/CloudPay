package cn.koolcloud.pos.controller.prepaid_card;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.wd.R;


public class PrepaidCardSearchResultController extends BaseController {

	private JSONObject data;
	private boolean removeJSTag = true;
	private String func_confirm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		data = formData.optJSONObject(getString(R.string.formData_key_data));
		
		initTextView(R.id.prepaid_card_tv_band_name, data, "band_name");
		initTextView(R.id.prepaid_card_tv_balance, data, "trans_amount");
		initTextView(R.id.prepaid_card_tv_status, data, "card_status");
		
		func_confirm = data.optString("confirm");
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_prepaid_card_search_result_controller);
	}
	
	private void initTextView(int resourceId, JSONObject data, String key) {
		initTextView(resourceId, data, key, true);
	}
	
	private void initTextView(int resourceId, JSONObject data, String key,
			boolean removeIfNull) {
		TextView textView = (TextView) findViewById(resourceId);
		textView.setText(data.optString(key, ""));
		if (removeIfNull && textView.getText().equals("")) {
			((ViewGroup) textView.getParent()).setVisibility(View.GONE);
		}
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	public void onCancel(View view) {
		finish();
	}
	
	public void onConfirm(View view) {
			onCall(func_confirm, null);
	}

	@Override
	protected String getTitlebarTitle() {
		return getResources().getString(R.string.title_activity_my_prepaid_card_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
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
		return removeJSTag;
	}

}
