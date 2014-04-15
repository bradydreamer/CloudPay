package cn.koolcloud.pos.controller.transaction_manage.del_voucher;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.DelVoucherRecordAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForJSON;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class DelVoucherRecordController extends BaseController {

	private DelVoucherRecordAdapter recordAdapter;
	private List<JSONObject> recordDataList;
	private ListView lv_record;
	
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
		lv_record = (ListView) findViewById(R.id.del_voucher_record_lv_record);
		recordAdapter = new DelVoucherRecordAdapter(this, recordDataList);
		boolean hasMore = data.optBoolean("hasMore");
		recordAdapter.setHasMore(hasMore);
		lv_record.setAdapter(recordAdapter);
		lv_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (position == recordDataList.size()) {
					JSONObject msg = new JSONObject();
					try {
						msg.put("isReqMore", true);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					onCall("DelVoucherRecordSearch.onConfirm", msg);
				}
			}
		});
	}

	@Override
	protected View viewForIdentifier(String name) {
		if (null == name) {
			return null;
		} else if (name.equals("lv_record")) {
			return lv_record;
		}
		return super.viewForIdentifier(name);
	}

	@Override
	protected void setView(View view, String key, Object value) {
		super.setView(view, key, value);
		if ("addList".equals(key)) {
			JSONObject data = (JSONObject)value;
			JSONArray dataArray = data.optJSONArray("recordList");
			for (int i = 0; i < dataArray.length(); i++) {
				recordDataList.add(dataArray.optJSONObject(i));
			}
			boolean hasMore = data.optBoolean("hasMore");
			recordAdapter.setHasMore(hasMore);
			recordAdapter.notifyDataSetChanged();
		} else if (key.equals("deleteALine")) {
			int index = (Integer)value;
			recordDataList.remove(index);
			recordAdapter.notifyDataSetChanged();
		}
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_del_voucher_record_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_del_voucher_record_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_DelVoucherRecord);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_DelVoucherRecord);
	}

}
