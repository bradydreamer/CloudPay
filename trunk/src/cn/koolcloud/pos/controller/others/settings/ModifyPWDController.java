package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

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
		String origPwdStr = origPwd.getText().toString();
		String firstNewPwdStr = firstNewPwd.getText().toString();
		String secondNewPwdStr = secondNewPwd.getText().toString();

		if (origPwdStr.isEmpty() || firstNewPwdStr.isEmpty()
				|| secondNewPwdStr.isEmpty()) {
			return;
		}
		if (!firstNewPwdStr.equals(secondNewPwdStr)) {
			secondNewPwd.setText(null);
			secondNewPwd.setHint(R.string.msg_mod_pwd_hint_error_dialog);
			return;
		}
		// TODO:need MD5 and 3DES.

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
