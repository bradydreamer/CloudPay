package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.secure.SecureEngine;

public class CreateUserController extends BaseController {

	private boolean removeJSTag = true;
	private EditText userName = null;
	private EditText firstPassword = null;
	private EditText secondPassword = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userName = (EditText) findViewById(R.id.text_username);
		firstPassword = (EditText) findViewById(R.id.text_first_password);
		secondPassword = (EditText) findViewById(R.id.text_second_password);
	}

	@Override
	public void onClickBtnOK(View view) {
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String userNameStr = userName.getText().toString();
		String firstPasswordStr = "_TDS_"
				+ se.md5(firstPassword.getText().toString()).toLowerCase();
		String secondPasswordStr = "_TDS_"
				+ se.md5(secondPassword.getText().toString()).toLowerCase();

		if (userNameStr.isEmpty() || firstPasswordStr.isEmpty()
				|| secondPasswordStr.isEmpty()) {
			return;
		}
		if (!firstPasswordStr.equals(secondPasswordStr)) {
			secondPassword.setText(null);
			secondPassword.setHint(R.string.msg_mod_pwd_hint_error_dialog);
			return;
		}

		JSONObject msg = new JSONObject();
		try {
			msg.put("newOperator", userNameStr);
			msg.put("pwd", firstPasswordStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("CreateUser.gotoCreate", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_create_user_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		// TODO Auto-generated method stub
		return getString(R.string.title_activity_create_user_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_CreateUser);
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
