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

public class ModifyPWDController extends BaseController {

	private boolean removeJSTag = true;
	private EditText origPwd = null;
	private EditText firstNewPwd = null;
	private EditText secondNewPwd = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		origPwd = (EditText) findViewById(R.id.orignalPwdTextView);
		firstNewPwd = (EditText) findViewById(R.id.firstPwdTextView);
		secondNewPwd = (EditText) findViewById(R.id.secondPwdTextView);
	}

	@Override
	public void onClickBtnOK(View view) {
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String origPwdStr = "_TDS_"
				+ se.md5(origPwd.getText().toString()).toLowerCase();
		String firstNewPwdStr = "_TDS_"
				+ se.md5(firstNewPwd.getText().toString()).toLowerCase();
		String secondNewPwdStr = "_TDS_"
				+ se.md5(secondNewPwd.getText().toString()).toLowerCase();

		if (origPwdStr.isEmpty() || firstNewPwdStr.isEmpty()
				|| secondNewPwdStr.isEmpty()) {
			return;
		}
		if (origPwdStr.equals(firstNewPwdStr)) {
			firstNewPwd.setText(null);
			firstNewPwd.setHint(R.string.msg_mod_pwd_hint_diffent_pwd_dialog);
			secondNewPwd.setText(null);
			secondNewPwd.setHint(R.string.msg_mod_pwd_hint_pwd_input_confirm);
			return;
		}
		if (!firstNewPwdStr.equals(secondNewPwdStr)) {
			secondNewPwd.setText(null);
			secondNewPwd.setHint(R.string.msg_mod_pwd_hint_error_dialog);
			return;
		}

		JSONObject msg = new JSONObject();
		try {
			msg.put("origalPwd", origPwdStr);
			msg.put("newPwd", firstNewPwdStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("ModifyPwd.gotoModifyPwd", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_modify_pwd_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		// TODO Auto-generated method stub
		return getString(R.string.title_activity_modify_pwd_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_ModifyPwd);
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
