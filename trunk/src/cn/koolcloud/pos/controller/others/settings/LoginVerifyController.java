package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.HostMessage;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.secure.SecureEngine;

public class LoginVerifyController extends BaseController implements View.OnKeyListener {
	private EditText et_userName;
	private EditText et_pwd;
	private JSONObject data;
	private String loginType;
	private boolean removeJSTag = true;

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
			loginType = "";
		}
		et_userName = (EditText) findViewById(R.id.login_et_userName);
		// setCurrentNumberEditText(et_userName);
		et_pwd = (EditText) findViewById(R.id.login_et_pwd);
        et_pwd.setOnKeyListener(this);
		// initETWithKBHiddenListener(et_userName);
		// initETWithKBHiddenListener(et_pwd);
		if (formData != null) {
			String titleName = formData.optString(getString(R.string.formData_key_title));
			if (!titleName.equals("")) {
				setTitle(HostMessage.getJsMsg(titleName));
			}
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
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String userName = et_userName.getText().toString();
		String pwdStr = et_pwd.getText().toString();
		String pwd = "_TDS_" + se.md5(pwdStr).toLowerCase();
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
	protected void loadRelatedJS() {
		JavaScriptEngine js = ClientEngine.engineInstance().javaScriptEngine();
		js.loadJs(getString(R.string.controllerJSName_SettingsIndex));
		super.loadRelatedJS();
	}

	// call get merchant info start mod by Teddy on 7 April
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

    @Override
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            onClickBtnOK(view);
        }
        return false;
    }

	// call get merchant info end mod by Teddy on 7 April

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
