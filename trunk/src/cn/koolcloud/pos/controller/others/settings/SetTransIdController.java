package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.postest.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SetTransIdController extends BaseController {

	private EditText et_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_id = (EditText) findViewById(R.id.set_trans_id_et_id);
		setCurrentNumberEditText(et_id);
	}

	@Override
	public void onClickBtnOK(View view) {
		String transId = et_id.getText().toString();
		if (transId.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("transId", transId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("SetTransId.onConfirm", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_set_trans_id_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_set_trans_id_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_SetTransId);
	}

}
