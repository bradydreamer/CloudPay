package cn.koolcloud.pos.controller.transaction_manage;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;

public class TransactionManageIndexController extends BaseController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	public void gotoConsumptionRecord(View view) {
		onCall("TransactionManageIndex.gotoConsumptionRecord", null);
	}
	
	public void gotoConsumptionRecordSearch(View view) {
		onCall("TransactionManageIndex.gotoConsumptionRecordSearch", null);
	}
	
	public void gotoSingleRecordSearch(View view) {
		onCall("TransactionManageIndex.gotoSingleRecordSearch", null);
	}
	
	public void gotoDelVoucherRecordSearch(View view) {
		onCall("TransactionManageIndex.gotoDelVoucherRecordSearch", null);
	}
	
	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_transaction_manage_index_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_transaction_manage_index);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_TransactionManageIndex);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_TransactionManageIndex);
	}

}
