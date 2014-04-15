package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONObject;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.widget.TextView;

public class MerchantInfoController extends BaseController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		if (null != data) {
			String merchId = data.optString("merchId");
			String machineId = data.optString("machineId");
			String merchName = data.optString("merchName");
			String merchAccount = data.optString("merchAccount");
			
			TextView tv_merchId = (TextView) findViewById(R.id.merchant_info_tv_merchId);
			tv_merchId.setText(merchId);
			TextView tv_machineId = (TextView) findViewById(R.id.merchant_info_tv_machineId);
			tv_machineId.setText(machineId);
			TextView tv_merchName = (TextView) findViewById(R.id.merchant_info_tv_merchName);
			tv_merchName.setText(merchName);
			TextView tv_merchAccount = (TextView) findViewById(R.id.merchant_info_tv_merchAccount);
			tv_merchAccount.setText(merchAccount);
		}
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_merchant_info_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_merchant_info_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return null;
	}

}
