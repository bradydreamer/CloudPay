package cn.koolcloud.pos.controller.others.settings;

import android.os.Bundle;
import android.view.View;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class SettingsIndexController extends BaseController {
	private boolean removeJSTag = true;

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
