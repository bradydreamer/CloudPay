package cn.koolcloud.pos.controller.others.settings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.AcquireListAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.database.CacheDB;
import cn.koolcloud.pos.entity.AcquireInstituteBean;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;

public class MerchantInfoController extends BaseController {
	
	private final int HANDLE_ACQUIRE_INSTITUTES = 0;
	private boolean removeJSTag = true;
	private ListView acquireInstituteList;
	
	private List<AcquireInstituteBean> dataSource = new ArrayList<AcquireInstituteBean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		if (null != data) {
			String merchId = data.optString("merchId");
			String machineId = data.optString("machineId");
			String merchName = data.optString("merchName");

			TextView tv_merchId = (TextView) findViewById(R.id.merchant_info_tv_merchId);
			tv_merchId.setText(merchId);
			TextView tv_machineId = (TextView) findViewById(R.id.merchant_info_tv_machineId);
			tv_machineId.setText(machineId);
			TextView tv_merchName = (TextView) findViewById(R.id.merchant_info_tv_merchName);
			tv_merchName.setText(merchName);
		}
		
		findViews();
	}
	
	private Handler mHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HANDLE_ACQUIRE_INSTITUTES:
				if (null != dataSource && dataSource.size() > 0) {
					AcquireListAdapter acquireListAdapter = new AcquireListAdapter(dataSource, MerchantInfoController.this);
					acquireInstituteList.setAdapter(acquireListAdapter);
					acquireListAdapter.notifyDataSetChanged();
					
//					new InsertAcquireInstitutesThread().start();
				}
				
				break;

			default:
				break;
			}
		}
	};
	
	private void findViews() {
		acquireInstituteList = (ListView) findViewById(R.id.acquireInstituteList);
		new LoadAcquireInstitutesThread().start();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_merchant_info_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_merchant_info_controller);
	}

	@Override
	protected String getControllerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getControllerJSName() {
		// TODO Auto-generated method stub
		return null;
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
	
	class InsertAcquireInstitutesThread extends Thread {

		@Override
		public void run() {
			CacheDB cacheDB = CacheDB.getInstance(MerchantInfoController.this);
			
			//clear acquire institute table
//			cacheDB.clearAcquireInstituteTableData();
			
			cacheDB.insertAcquireInstitute(dataSource);
		}
		
	}
	
	class LoadAcquireInstitutesThread extends Thread {

		@Override
		public void run() {
			Map<String, ?> map = UtilForDataStorage.readPropertyBySharedPreferences(MerchantInfoController.this, "merchSettings");
			JSONArray jsonArray = null;
			if (map.containsKey("settingString")) {
				String jsArrayStr = String.valueOf(map.get("settingString"));
				try {
					jsonArray = new JSONArray(jsArrayStr);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				dataSource = UtilForJSON.parseJsonArray2AcquireInstitute(jsonArray);
			}
			
			Message msg = mHandler.obtainMessage();
			msg.what = HANDLE_ACQUIRE_INSTITUTES;
			msg.obj = dataSource;
			mHandler.sendMessage(msg);
		}
	}

}
