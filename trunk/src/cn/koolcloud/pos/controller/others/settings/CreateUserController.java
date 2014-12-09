package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.secure.SecureEngine;

public class CreateUserController extends BaseController {

	private boolean removeJSTag = true;
	private EditText userName = null;
	private EditText firstPassword = null;
	private EditText secondPassword = null;
	private static String gradeIdName[] = null;//new String[] { "收银员", "主管" };
	private Spinner spinner;
	private String spinnerStr;
	private ArrayAdapter<String> adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		userName = (EditText) findViewById(R.id.text_username);
		firstPassword = (EditText) findViewById(R.id.text_first_password);
		secondPassword = (EditText) findViewById(R.id.text_second_password);
		spinner = (Spinner) findViewById(R.id.Spinner01);
        gradeIdName = getResources().getStringArray(R.array.user_grade);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, gradeIdName);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				spinnerStr = parent.getItemAtPosition(position).toString();
				if (spinnerStr.equals(gradeIdName[0])) {
					spinnerStr = "2";
				} else if (spinnerStr.equals(gradeIdName[1])) {
					spinnerStr = "1";
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Toast.makeText(getApplicationContext(), "没有改变的处理",
				// Toast.LENGTH_LONG).show();
			}

		});

	}

	@Override
	public void onClickBtnOK(View view) {
		SecureEngine se = ClientEngine.engineInstance().secureEngine();
		String userNameStr = userName.getText().toString();
		String gradeId = spinnerStr;
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
			msg.put("gradeId", gradeId);
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
