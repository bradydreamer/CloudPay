package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class LoginController extends BaseController {
	private EditText et_userName;
	private EditText et_pwd;
	private JSONObject data;
	private String loginType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (formData != null) {
			data = formData
					.optJSONObject(getString(R.string.formData_key_data));
		}
		if (data != null && !data.isNull("Login")) {
			loginType = data.optString("Login");
		} else {
			loginType = "LoginIndex.onLogin";
		}
		et_userName = (EditText) findViewById(R.id.login_et_userName);
		// setCurrentNumberEditText(et_userName);
		et_pwd = (EditText) findViewById(R.id.login_et_pwd);
		// initETWithKBHiddenListener(et_userName);
		// initETWithKBHiddenListener(et_pwd);
		String titleName = formData
				.optString(getString(R.string.formData_key_title));
		if (!titleName.equals("")) {
			setTitle(titleName);
		}

	}

	// @Override
	// protected void setOnFocusChangeExtraAction(EditText editText) {
	// super.setOnFocusChangeExtraAction(editText);
	// setCurrentNumberEditText(editText);
	// }

	@Override
	public void onClickBtnOK(View view) {
		// if (getCurrentNumberEditText() == et_userName
		// && (et_userName.getText().toString().isEmpty() ||
		// et_pwd.getText().toString().isEmpty())) {
		// et_pwd.requestFocus();
		// } else {
		String userName = et_userName.getText().toString();
		String pwd = et_pwd.getText().toString();
		JSONObject msg = new JSONObject();
		try {
			msg.put("userName", userName);
			msg.put("pwd", pwd);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall(loginType, msg);
		// }
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_login_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_login_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_Login);
	}

}
