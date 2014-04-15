package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.ConsumptionRecordAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForJSON;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class ConsumptionRecordController extends BaseController {

	private ListView lv_record;
	private ConsumptionRecordAdapter adapter;
	private List<JSONObject> recordDataList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		JSONArray recordList = data.optJSONArray("recordList");
		recordDataList = UtilForJSON.JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);
		lv_record = (ListView) findViewById(R.id.consumption_record_lv_record);
		adapter = new ConsumptionRecordAdapter(this);
		adapter.setList(recordDataList);
		boolean hasMore = data.optBoolean("hasMore");
		adapter.setHasMore(hasMore);
		lv_record.setAdapter(adapter);
		lv_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == recordDataList.size()) {
					onCall("ConsumptionRecord.reqMore", null);
				} else {
					JSONObject recordData = recordDataList.get(position);					
					onCall("ConsumptionRecord.getRecordDetail", recordData);
				}
			}
		});
		
	}
	
	@Override
	protected View viewForIdentifier(String name) {
		if("lv_record".equals(name)) {
			return lv_record;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		if (null == view || null == key) {
			return;
		}
		if ("addList".equals(key)) {
			JSONObject data = (JSONObject)value;
			JSONArray dataArray = data.optJSONArray("recordList");
			for (int i = 0; i < dataArray.length(); i++) {
				recordDataList.add(dataArray.optJSONObject(i));
			}
			boolean hasMore = data.optBoolean("hasMore");
			adapter.setHasMore(hasMore);
			adapter.notifyDataSetChanged();
		}
		super.setView(view, key, value);
	}
	
	@Override
	protected void onStart() {
		adapter.notifyDataSetChanged();
		super.onStart();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_consumption_record_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_consumption_record);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_ConsumptionRecord);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_ConsumptionRecord);
	}

}
