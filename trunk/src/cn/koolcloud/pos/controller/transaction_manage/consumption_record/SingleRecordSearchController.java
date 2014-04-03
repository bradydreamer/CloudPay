package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.postest.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SingleRecordSearchController extends BaseController {
	
	private EditText et_id;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		et_id = (EditText) findViewById(R.id.single_record_search_et_id);
		setCurrentNumberEditText(et_id);
	}

	@Override
	public void onClickBtnOK(View view) {
		String id = et_id.getText().toString();
		if (id.isEmpty()) {
			return;
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("id", id);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("TransactionManageIndex.gotoSingleRecord", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_single_record_search_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_single_record_search_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return null;
	}

}
