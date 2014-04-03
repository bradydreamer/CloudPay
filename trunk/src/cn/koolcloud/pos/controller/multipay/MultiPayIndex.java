package cn.koolcloud.pos.controller.multipay;

import android.view.View;
import cn.koolcloud.pos.controller.BaseHomeController;
import cn.koolcloud.postest.R;

public class MultiPayIndex extends BaseHomeController {
	
	public void onClickClose(View view){
		onCall("MultiPay.onClickClose", null);
	}

	public void onClickRecord(View view){
		onCall("MultiPay.onClickRecord", null);
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

}
