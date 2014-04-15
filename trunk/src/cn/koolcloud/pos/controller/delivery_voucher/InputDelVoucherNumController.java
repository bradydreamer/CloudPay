package cn.koolcloud.pos.controller.delivery_voucher;

import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class InputDelVoucherNumController extends BaseController {
	
	private EditText et_num;
	private String open_brh;
	private String payment_id;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		
		open_brh = data.optString("open_brh");
		payment_id = data.optString("payment_id");

		et_num = (EditText) findViewById(R.id.input_del_voucher_num_et_num);
		setCurrentNumberEditText(et_num);
	}

	@Override
	protected void addInputNumber(String text) {
		if (null != text) {
			if (numberInputString.toString().equals("0")) {
				numberInputString.replace(0, 1, text);
			} else {
				numberInputString.append(text);
			}
		}
	}

	@Override
	public void onClickBtnOK(View view) {
		String num = et_num.getText().toString();
		if ("0".equals(num)) {
			return;
		}
		if (num.isEmpty()) {
			num = "1";
		}
		JSONObject msg = new JSONObject();
		try {
			msg.put("open_brh", open_brh);
			msg.put("payment_id", payment_id);
			msg.put("num", num);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		onCall("DeliveryVocherConsume.onConfirmNum", msg);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_input_del_voucher_num_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_input_del_voucher_num_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_DelVoucherConsume);
	}

}
