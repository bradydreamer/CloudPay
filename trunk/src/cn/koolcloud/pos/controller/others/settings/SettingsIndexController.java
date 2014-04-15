package cn.koolcloud.pos.controller.others.settings;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

import android.os.Bundle;
import android.view.View;

public class SettingsIndexController extends BaseController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void gotoLogin(View view) {
		onCall("SettingsIndex.gotoLogin", null);
	}
	
	public void gotoLogout(View view) {
		onCall("SettingsIndex.gotoLogout", null);
	}
	
	public void gotoSetMerchId(View view) {
		onCall("SettingsIndex.gotoSetMerchId", null);
	}
	
	public void gotoSetMachineId(View view) {
		onCall("SettingsIndex.gotoSetMachineId", null);
	}
	
	public void gotoMerchantInfo(View view) {
		onCall("SettingsIndex.gotoMerchantInfo", null);
	}
	
	public void clearReverseData(View view) {
		onCall("SettingsIndex.clearReverseData", null);
	}

	public void downloadMerchData(View view) {
		onCall("SettingsIndex.downloadMerchData", null);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_settings_index_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_settings_index_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_SettingsIndex);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_SettingsIndex);
	}

}
