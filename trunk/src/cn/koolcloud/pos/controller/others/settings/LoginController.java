package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.postest.R;
import cn.koolcloud.pos.controller.BaseController;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class LoginController extends BaseController {
	private EditText et_userName;
	private EditText et_pwd;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_userName = (EditText) findViewById(R.id.login_et_userName);
//		setCurrentNumberEditText(et_userName);
		et_pwd = (EditText) findViewById(R.id.login_et_pwd);
//		initETWithKBHiddenListener(et_userName);
//		initETWithKBHiddenListener(et_pwd);
	}

//	@Override
//	protected void setOnFocusChangeExtraAction(EditText editText) {
//		super.setOnFocusChangeExtraAction(editText);
//		setCurrentNumberEditText(editText);
//	}

	@Override
	public void onClickBtnOK(View view) {
//		if (getCurrentNumberEditText() == et_userName
//				&& (et_userName.getText().toString().isEmpty() || et_pwd.getText().toString().isEmpty())) {
//			et_pwd.requestFocus();
//		} else {
			String userName = et_userName.getText().toString();
			String pwd = et_pwd.getText().toString();
			JSONObject msg = new JSONObject();
			try {
				msg.put("userName", userName);
				msg.put("pwd", pwd);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			onCall("LoginIndex.onLogin", msg);
//		}
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
