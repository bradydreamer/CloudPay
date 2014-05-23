package cn.koolcloud.pos.controller.multipay;

import android.os.Bundle;
import android.view.View;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseHomeController;

public class MultiPayIndex extends BaseHomeController {

	private boolean removeJSTag = true;

	//restart update trans info on 23th May -- start
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		onCall("Home.updateTransInfo", null);
	}
	//restart update trans info on 23th May -- end

	public void onClickClose(View view) {
		onCall("MultiPay.onClickClose", null);
	}

	public void onClickRecord(View view) {
		onCall("MultiPay.onClickRecord", null);
	}

	@Override
	protected void onResume() {
		onCall("MultiPay.resumeMultiPayTag", null);
		super.onResume();
	}

	@Override
	public void onBackPressed() {
		onCall("MultiPay.onClickClose", null);
		super.onBackPressed();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_multipay_index_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_multi_pay_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_MultiPayIndex);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_MultiPay);
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
