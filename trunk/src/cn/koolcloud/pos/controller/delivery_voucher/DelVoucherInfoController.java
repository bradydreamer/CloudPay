package cn.koolcloud.pos.controller.delivery_voucher;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.wd.R;

public class DelVoucherInfoController extends BaseController {

	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		TextView tv_voucherId = (TextView) findViewById(R.id.del_voucher_info_tv_voucherId);
		tv_voucherId.setText(data.optString("voucherId"));
		TextView tv_productName = (TextView) findViewById(R.id.del_voucher_info_tv_productName);
		tv_productName.setText(data.optString("productName"));
		TextView tv_DateRange = (TextView) findViewById(R.id.del_voucher_info_tv_dateRange);
		tv_DateRange.setText(data.optString("dateRange"));
		TextView tv_details = (TextView) findViewById(R.id.del_voucher_info_tv_details);
		tv_details.setText(data.optString("brief"));
	}

	public void onConfirm(View view) {
		onCall("DeliveryVocherConsume.onConfirmConsume", null);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_del_voucher_info_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_del_voucher_info_controller);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return null;
	}

	@Override
	public void onBackPressed() {

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
