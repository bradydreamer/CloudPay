package cn.koolcloud.pos.controller.others.settings;

import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;

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
	
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		new LoadAcquireInstitutesThread().start();
        //download bank data.
        onCall("Home.downloadBankData", null);
	}

	class LoadAcquireInstitutesThread extends Thread {

		@Override
		public void run() {
			Map<String, ?> map = UtilForDataStorage.readPropertyBySharedPreferences(SettingsDownloadController.this, "merchSettings");
			JSONArray jsonArray = null;
			if (map.containsKey("settingString")) {
				String jsArrayStr = String.valueOf(map.get("settingString"));
				CacheDB cacheDB = CacheDB.getInstance(SettingsDownloadController.this);
				try {
					jsonArray = new JSONArray(jsArrayStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				//clear acquire institute table
				cacheDB.clearAcquireInstituteTableData();
				
				List<AcquireInstituteBean> acquireList = UtilForJSON.parseJsonArray2AcquireInstitute(jsonArray);
				if (acquireList != null && acquireList.size() > 0) {
					cacheDB.insertAcquireInstitute(acquireList);
				}
				
				//clear cached payment table
				cacheDB.clearPaymentActivityTableData();
				List<AcquireInstituteBean> acquireJsonList = UtilForJSON.parseJsonArray2AcquireInstituteWithJson(jsonArray);
				if (acquireJsonList != null && acquireJsonList.size() > 0) {
					cacheDB.insertPayment(acquireJsonList);
				}
			}
			
		}
	}

}
