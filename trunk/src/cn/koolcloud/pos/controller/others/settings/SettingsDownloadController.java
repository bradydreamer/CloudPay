package cn.koolcloud.pos.controller.others.settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;

public class SettingsDownloadController extends BaseController {
	private ProgressBar progressBar;
	private boolean removeJSTag = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		progressBar = (ProgressBar) findViewById(R.id.set_download_process_bar);
		progressBar.setProgress(0);
		onCall("SettingsDownload.start", null);
	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("loading".equals(name)) {
			return progressBar;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if (progressBar.equals(view)) {
			if ("process".equals(key)) {
				final int progress = Integer.parseInt("" + value);
				Log.d(TAG, "loading process:" + progress);
				mainHandler.post(new Runnable() {

					@Override
					public void run() {
						progressBar.setProgress(progress);
					}
				});
				return;
			}
		}
		super.setView(view, key, value);
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_settings_download_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_settings_download_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_SettingsDownload);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_SettingsDownload);
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
