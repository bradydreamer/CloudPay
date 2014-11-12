package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.secure.SecureEngine;
import cn.koolcloud.pos.wd.R;
import cn.koolcloud.pos.wd.R.color;

public class LoginController extends BaseController {
	private EditText customerId;
	private EditText configuration_userName;
	private EditText configuration_pwd;
	private String str_customerId = "";
	private String str_userName = "";
	private JSONObject data;
	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (formData != null) {
			data = formData
					.optJSONObject(getString(R.string.formData_key_data));
		}
		if (data != null && !data.isNull("merchId")) {
			str_customerId = data.optString("merchId");
		}
		if (data != null && !data.isNull("operator")) {
			str_userName = data.optString("operator");
		}

		customerId = (EditText) findViewById(R.id.customer_id);

		configuration_userName = (EditText) findViewById(R.id.configuration_userName);
		configuration_pwd = (EditText) findViewById(R.id.configuration_pwd);
		if (!(str_customerId == null) && !(str_customerId.equals(""))) {
			customerId.setText(str_customerId);
			customerId.setTextColor(color.gray);
			customerId.setInputType(InputType.TYPE_NULL);
			customerId.setFocusable(false);
		}
		configuration_userName.setText(str_userName);
	}

	@Override
	public void onClickBtnOK(View view) {

		String customer_Id = customerId.getText().toString();
		String userName = configuration_userName.getText().toString();
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String pwdStr = configuration_pwd.getText().toString();
		// String pwd = se.fieldDecrypt(se.md5(pwdStr));
		String pwd = "_TDS_" + se.md5(pwdStr).toLowerCase();
		String ssn = android.os.Build.SERIAL;

		JSONObject msg = new JSONObject();
		try {
			msg.put("merchId", customer_Id);
			msg.put("userName", userName);
			msg.put("pwd", pwd);
			msg.put("ssn", ssn);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("LoginIndex.onLogin", msg);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void loadRelatedJS() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(getString(R.string.controllerJSName_SettingsIndex));
		super.loadRelatedJS();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_configure_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_configure_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_Login);
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
