package cn.koolcloud.pos.controller.others.settings;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.wd.R;

public class SetMachineIdController extends BaseController {

	private EditText et_id;
	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		et_id = (EditText) findViewById(R.id.set_machine_id_et_id);
		setCurrentNumberEditText(et_id);
	}

	@Override
	public void onClickBtnOK(View view) {
		String machineId = et_id.getText().toString();
		if (machineId.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("machineId", machineId);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("SetMachineId.onConfirm", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_set_machine_id_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		// TODO Auto-generated method stub
		return getString(R.string.title_activity_set_machine_id_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return getString(R.string.controllerJSName_SetMachineId);
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
