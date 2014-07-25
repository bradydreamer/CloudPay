package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import cn.koolcloud.interfaces.OrderHeaderInterface;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.ConsumptionRecordAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.database.ConsumptionRecordDB;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.widget.OrderContentHeader;

public class ConsumptionRecordController extends BaseController implements OrderHeaderInterface {

	private ListView lv_record;
	private OrderContentHeader orderContentHeader;
	private ConsumptionRecordAdapter adapter;
	private List<JSONObject> recordDataList;
	private boolean removeJSTag = true;
	
	private String startDate;
	private String endDate;
	private boolean hasMore;
	
	private ConsumptionRecordDB consumptionDB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		JSONObject data = formData
				.optJSONObject(getString(R.string.formData_key_data));
		JSONArray recordList = data.optJSONArray("recordList");
		recordDataList = UtilForJSON
				.JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);
		lv_record = (ListView) findViewById(R.id.consumption_record_lv_record);
		adapter = new ConsumptionRecordAdapter(this);
		adapter.setList(recordDataList);
		hasMore = data.optBoolean("hasMore");
		adapter.setHasMore(hasMore);
		lv_record.setAdapter(adapter);
		
		//init startDate and endDate
		startDate = data.optString("start_date");
		endDate = data.optString("end_date");
		
		lv_record.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				setRemoveJSTag(true);
				loadRelatedJS();
				if (position == recordDataList.size()) {
					onCall("ConsumptionRecord.reqMore", null);
				} else {
					JSONObject recordData = recordDataList.get(position);
					onCall("ConsumptionRecord.getRecordDetail", recordData);
				}
			}
		});
		
		//cache record to sqlite --start by Teddy 17th July
		if (recordDataList != null && recordDataList.size() > 0) {
			new CacheRecordThread().start();
		}
		//cache record to sqlite --end by Teddy 17th July
		
		consumptionDB = ConsumptionRecordDB.getInstance(ConsumptionRecordController.this);
		orderContentHeader = (OrderContentHeader) findViewById(R.id.header);
		orderContentHeader.setOrderHeaderInterface(this);
	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("lv_record".equals(name)) {
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
			JSONObject data = (JSONObject) value;
			JSONArray dataArray = data.optJSONArray("recordList");
			
			//cache record to sqlite --start by Teddy 17th July
			List<JSONObject> tmpList = new ArrayList<JSONObject>();
			//cache record to sqlite --end by Teddy 17th July
			
			for (int i = 0; i < dataArray.length(); i++) {
				recordDataList.add(dataArray.optJSONObject(i));
				tmpList.add(dataArray.optJSONObject(i));
			}
			hasMore = data.optBoolean("hasMore");
			adapter.setHasMore(hasMore);
			adapter.notifyDataSetChanged();
			
			//cache record to sqlite --start by Teddy 17th July
			if (tmpList != null && tmpList.size() > 0) {
				new CacheRecordThread().start();
			}
			//cache record to sqlite --end by Teddy 17th July
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

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}
	
	class CacheRecordThread extends Thread {

		@Override
		public void run() {
			ConsumptionRecordDB cacheDB = ConsumptionRecordDB.getInstance(ConsumptionRecordController.this);
			cacheDB.insertConsumptionRecord(recordDataList);
		}
	}

	@Override
	public void clicked(int col, int sortType) {
		List<JSONObject> sortedList = null;
		String sortColumn;
		switch (col) {
		case OrderContentHeader.COL_1:
			sortColumn = ConsumptionRecordDB.TRANS_TYPE_RECORD;
			break;
		case OrderContentHeader.COL_2:
			sortColumn = ConsumptionRecordDB.PAYMENT_ID_RECORD;
			break;
		case OrderContentHeader.COL_3:
			sortColumn = ConsumptionRecordDB.REF_NO_RECORD;
			break;
		case OrderContentHeader.COL_4:
			sortColumn = ConsumptionRecordDB.TRANS_DATE_RECORD;
			break;
		case OrderContentHeader.COL_5:
			sortColumn = ConsumptionRecordDB.TRANS_TIME_RECORD;
			break;
		case OrderContentHeader.COL_6:
			sortColumn = ConsumptionRecordDB.TRANS_AMOUNT_RECORD;
			break;
		case OrderContentHeader.COL_7:
			sortColumn = ConsumptionRecordDB.ORDER_STATE_RECORD;
			
			break;
		default:
			sortColumn = ConsumptionRecordDB.TRANS_TYPE_RECORD;
		}
		
		boolean isDesc = false;
		if (sortType == OrderContentHeader.SORT_DESCENDING) {
			isDesc = true;
		} else {
			isDesc = false;
			
		}
		String tmpStartDate = "";
		String tmpEndDate = "";
		if (!TextUtils.isEmpty(startDate) || !TextUtils.isEmpty(endDate)) {
			tmpStartDate = startDate.substring(0, 8);
			tmpEndDate = endDate.substring(0, 8);
		}
		
		sortedList = consumptionDB.getSortedRecords(tmpStartDate, tmpEndDate, sortColumn, isDesc);
		if (sortedList != null && sortedList.size() > 0) {
			recordDataList.clear();
			recordDataList.addAll(sortedList);
			adapter.setHasMore(hasMore);
			adapter.notifyDataSetChanged();
		}
	}
}
