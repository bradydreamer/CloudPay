package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class SigninController extends BaseController {

	private boolean removeJSTag = true;
	private TextView sign_text = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		sign_text = (TextView) findViewById(R.id.sign_tx);
		onCall("SignIn.gotoSignIn", null);
	}

	@Override
	public void setProperty(JSONArray data) {
		// TODO Auto-generated method stub
		String title = null;
		JSONObject json = null;
		try {
			json = data.getJSONObject(0);
			title = json.optString("title");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle(title);
		sign_text.setText(json.optString("content"));
		super.setProperty(data);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// onCall("SignIn.gotoSignIn", null);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_sign_in_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_sign_in_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_SignIn);
	}

	@Override
	protected String getControllerJSName() {
		return null;
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
