package cn.koolcloud.pos.controller.others;

import cn.koolcloud.pos.ClientEngine;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.controller.others.settings.SettingsIndexController;

import android.os.Bundle;
import android.view.View;

public class OthersIndexController extends BaseController {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}
	
	public void gotoSettings(View view) {
		ClientEngine clientEngine = ClientEngine.engineInstance();
		clientEngine.showController(SettingsIndexController.class);
	}
	
	public void gotoBalance(View view) {
		onCall("OthersIndex.gotoBalance", null);
	}
	
	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_others_index_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_others_index);
	}

	@Override
	protected String getControllerName() {
		return null;
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_OthersIndex);
	}

}
