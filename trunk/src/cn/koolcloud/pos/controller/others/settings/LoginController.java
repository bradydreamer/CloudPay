package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.JavaScriptEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.R.color;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.secure.SecureEngine;

public class LoginController extends BaseController implements View.OnKeyListener {

    private EditText customerId;
	private EditText configuration_userName;
	private EditText configuration_pwd;
	private String str_customerId = "";
	private String str_userName = "";
	private String str_pwd = "0";
	private int str_pwd_len = -1;
	private JSONObject data;
	private CheckBox checkBox;
	private Boolean remember_tag = false;
	private static final String NO_PWD = "0";
	private static final String STR = "******************************";
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
		if (data != null) {
			str_pwd = data.optString("pwd", NO_PWD);
		}
		if (!str_pwd.equals(NO_PWD)) {
			if (data != null) {
				str_pwd_len = data.optInt("pwd_len", -1);
			}
		} else {
			str_pwd_len = -1;
		}

		customerId = (EditText) findViewById(R.id.customer_id);
		checkBox = (CheckBox) findViewById(R.id.remember_pwd);

		configuration_userName = (EditText) findViewById(R.id.configuration_userName);
		configuration_pwd = (EditText) findViewById(R.id.configuration_pwd);
        configuration_pwd.setOnKeyListener(this);
		if (!(str_customerId == null) && !(str_customerId.equals(""))) {
			customerId.setText(str_customerId);
			customerId.setTextColor(color.gray);
			customerId.setInputType(InputType.TYPE_NULL);
			customerId.setFocusable(false);
		}
		if (!str_pwd.equals(NO_PWD)) {
			setRemenberPwd();
		}
		configuration_userName.setText(str_userName);
		checkBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				if (isChecked) {
					remember_tag = true;
				} else {
					remember_tag = false;
				}
			}
		});

		configuration_userName.addTextChangedListener(new TextWatcher() {

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String userNameStr = configuration_userName.getText()
						.toString();
				if (!userNameStr.equals(str_userName)) {
					configuration_pwd.setText(null);
					checkBox.setChecked(false);
					remember_tag = false;
				} else {
					setRemenberPwd();
				}

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}

		});
	}

	private void setRemenberPwd() {
		if (str_pwd_len != -1) {
			configuration_pwd.setText(STR.substring(0, str_pwd_len));
			checkBox.setChecked(true);
			remember_tag = true;
		} else {
			configuration_pwd.setText(null);
			checkBox.setChecked(false);
			remember_tag = false;
		}
	}

	@Override
	public void onClickBtnOK(View view) {
		String pwd;
		String customer_Id = customerId.getText().toString();
		String userName = configuration_userName.getText().toString();
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String pwdStr = configuration_pwd.getText().toString();
		int pwd_len = pwdStr.length();
		if (str_pwd.equals(NO_PWD)) {
			pwd = "_TDS_" + se.md5(pwdStr).toLowerCase();
		} else {
			if (!pwdStr.equals(STR.substring(0, str_pwd_len))) {
				pwd = "_TDS_" + se.md5(pwdStr).toLowerCase();
			} else {
				pwd = str_pwd;
			}
		}
		String ssn = android.os.Build.SERIAL;

		JSONObject msg = new JSONObject();
		try {
			msg.put("merchId", customer_Id);
			msg.put("userName", userName);
			msg.put("pwd", pwd);
			msg.put("ssn", ssn);
			msg.put("pwd_len", pwd_len);
			msg.put("remenber_tag", remember_tag);
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
    public boolean onKey(View view, int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            onClickBtnOK(view);
        }
        return false;
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
