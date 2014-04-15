package cn.koolcloud.pos.controller.others;

import org.json.JSONObject;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BalanceResultController extends BaseController {

	private TextView tv_title_balance;
	private TextView tv_balance;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		
		tv_title_balance = (TextView) findViewById(R.id.balance_result_tv_title_balance);
		tv_balance = (TextView) findViewById(R.id.balance_result_tv_balance);
		
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		onCall("BalanceResult.reqBalance", data);
	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("tv_title_balance".equals(name)) {
			return tv_title_balance;
		} else if ("tv_balance".equals(name)) {
			return tv_balance;
		} 
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_balance_result_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_balance_result_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_BalanceResult);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_BalanceResult);
	}

}
