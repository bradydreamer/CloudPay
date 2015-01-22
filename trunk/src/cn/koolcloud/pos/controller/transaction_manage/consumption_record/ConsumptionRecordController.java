package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import cn.koolcloud.interfaces.OrderHeaderInterface;
import cn.koolcloud.pos.MyApplication;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.ConsumptionRecordAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.database.ConsumptionRecordDB;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.widget.ContentHeader;
import cn.koolcloud.util.DateUtil;

public class ConsumptionRecordController extends BaseController implements OrderHeaderInterface {

	private ListView lv_record;
	private Spinner operatorSpinner;
	private ContentHeader orderContentHeader;
	private ConsumptionRecordAdapter adapter;
	private List<JSONObject> recordDataList;
	private boolean removeJSTag = true;
    private final String FROM_TODAY_TAG = "TODAY";
    private final String FROM_HISTORY_TAG = "HISTORY";

	private String mStartDate;
	private String mEndDate;
	private boolean hasMore;
	
	private ConsumptionRecordDB consumptionDB;

    private List<String> spinnerData = new ArrayList<String>();
    private ArrayAdapter<String> spinnerAdapter;
    private String operator = "";
    private int spinnerSelectCount = 0;

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
        operatorSpinner = (Spinner) findViewById(R.id.operatorSpinner);
//        initSpinner();

		adapter = new ConsumptionRecordAdapter(this);
		adapter.setList(recordDataList);
		hasMore = data.optBoolean("hasMore");
		adapter.setHasMore(hasMore);
		lv_record.setAdapter(adapter);
		
		//init startDate and endDate
		mStartDate = data.optString("start_date");
		mEndDate = data.optString("end_date");
		
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
                    try {
                        recordData.put("from", "recordList");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    onCall("ConsumptionRecord.getRecordDetail", recordData);
				}
			}
		});
		
		//cache record to sqlite --start by Teddy 17th July
		if (recordDataList != null && recordDataList.size() > 0) {
			new CacheRecordThread(true).start();
		}
		//cache record to sqlite --end by Teddy 17th July
		
		consumptionDB = ConsumptionRecordDB.getInstance(ConsumptionRecordController.this);
		orderContentHeader = (ContentHeader) findViewById(R.id.header);
		orderContentHeader.setOrderHeaderInterface(this);

        TextView currencyTextView = (TextView) findViewById(R.id.transAmountHeader);
        String formattingCurrency = getResources().getString(R.string.consumption_record_tv_transAmount);
        String currencyResource = Env.getCurrencyResource(this);
        currencyTextView.setText(String.format(formattingCurrency, currencyResource));

        LinearLayout printerLayout = (LinearLayout) findViewById(R.id.printerLayout);
        String fromTag = data.optString("fromTag");
        if (!TextUtils.isEmpty(fromTag) && fromTag.equals(FROM_TODAY_TAG)) {
            printerLayout.setVisibility(View.VISIBLE);
        } else if (!TextUtils.isEmpty(fromTag) && fromTag.equals(FROM_HISTORY_TAG)) {
            printerLayout.setVisibility(View.VISIBLE);
            Spinner mSpinner = (Spinner) findViewById(R.id.operatorSpinner);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) mSpinner.getLayoutParams();
            params.setMargins(0, 0, 20, 0);
            mSpinner.setLayoutParams(params);

            findViewById(R.id.btnPrintRecord).setVisibility(View.GONE);
        } else {
            printerLayout.setVisibility(View.GONE);
        }

	}

	@Override
	protected View viewForIdentifier(String name) {
		if ("lv_record".equals(name)) {
			return lv_record;
		}
		return super.viewForIdentifier(name);
	}

    @Override
    protected void updateViews(JSONObject item) {
        JSONObject jsonObject = item.optJSONObject("value");
        if (jsonObject != null) {
            JSONArray recordList = jsonObject.optJSONArray("recordList");
            if (recordList != null) {
                adapter = new ConsumptionRecordAdapter(this);
                recordDataList = UtilForJSON.JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);
                adapter.setList(recordDataList);
                hasMore = jsonObject.optBoolean("hasMore");
                adapter.setHasMore(hasMore);
                lv_record.setAdapter(adapter);
                adapter.notifyDataSetChanged();

                if (recordDataList != null && recordDataList.size() > 0) {
                    new CacheRecordThread(true).start();
                }
            }
        }
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
				new CacheRecordThread(false).start();
			}
			//cache record to sqlite --end by Teddy 17th July
		} else if (key.equals("updateList")) {
			//clear ListView data first after finish revoke order --start mod by Teddy on 29th September
			if (recordDataList != null) {
				recordDataList.clear();
			}
			
			adapter.notifyDataSetChanged();	
			//clear ListView data first after finish revoke order --end mod by Teddy on 29th September

//            new ClearRecordThread().start();
		}
		super.setView(view, key, value);
	}

    private void initSpinner() {
        Map<String, ?> map = UtilForDataStorage.readPropertyBySharedPreferences(this, "userList");
        if(map.size() > 0) {
            JSONObject operatorObj = new JSONObject(map);
            Iterator<String> keys = operatorObj.keys();
            spinnerData.add("All");
            while (keys.hasNext()) {
                String str = keys.next();
                if (!str.equals("curMonth") && !str.equals("curDate")) {
                    spinnerData.add(operatorObj.optString(str));
//                    UtilForDataStorage.clearPropertyBySharedPreferences(this, operatorObj.optString(str));
                }
            }

            spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, spinnerData);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            operatorSpinner.setAdapter(spinnerAdapter);

            if (spinnerData != null && spinnerData.size() > 0) {
                operator = spinnerData.get(0);
            }
            operatorSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                    // TODO Auto-generated method stub
                    operator = arg0.getSelectedItem().toString();
                    spinnerSelectCount++;
                    if (spinnerSelectCount > 1) {

                        JSONObject msg = new JSONObject();
                        try {
                            String startDate = "";
                            String endDate = "";

                            if (!TextUtils.isEmpty(mStartDate) && !TextUtils.isEmpty(mEndDate)) {
                                startDate = mStartDate;
                                endDate = mEndDate;
                            } else {

                                Date today = new Date();
                                String todayStr = DateUtil.formatDate(today, "yyyy-MM-dd"); //获取当地日期（默认）
                                startDate = todayStr + " 00:00:00"; //获取当的日期+起始时间
                                startDate = DateUtil.formatDate(DateUtil.parseDate(startDate),"yyyyMMddHHmmss", TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的起始时间

                                endDate = todayStr + " 23:59:59"; //获取当的日期+最终时间
                                endDate = DateUtil.formatDate(DateUtil.parseDate(endDate),"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的最终时间
                            }

                            msg.put("startDate", startDate);
                            msg.put("endDate", endDate);
                            msg.put("ioperator", operator);
                            onCall("TransactionManageIndex.onTodayConsumptionRecord", msg);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> arg0) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

	@Override
	protected void onStart() {
		adapter.notifyDataSetChanged();
		super.onStart();
        spinnerData.clear();
        initSpinner();
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_consumption_record_controller);
	}

    public void onPrintRecord(View view) {
        JSONObject msg = new JSONObject();
        try {
//            Map<String, ?> map = UtilForDataStorage.readPropertyBySharedPreferences(MyApplication.getContext(), "merchant");
//            String operator = (String) map.get("operator");
            if (!TextUtils.isEmpty(operator) && !operator.equals("All")) {
                msg.put("ioperator", operator);
            }
            onCall("ConsumptionRecord.printOperatorTodayRecord", msg);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
        boolean clearData = false;
        CacheRecordThread(boolean needClearData) {
            this.clearData = needClearData;
        }
		@Override
		public void run() {
			ConsumptionRecordDB cacheDB = ConsumptionRecordDB.getInstance(ConsumptionRecordController.this);
            if (clearData) {
                cacheDB.clearRecordTableData();
            }
			cacheDB.insertConsumptionRecord(recordDataList);
		}
	}

	@Override
	public void clicked(int col, int sortType) {
		List<JSONObject> sortedList = null;
		String sortColumn;
		switch (col) {
		case ContentHeader.COL_1:
			sortColumn = ConsumptionRecordDB.TRANS_TYPE_RECORD;
			break;
		case ContentHeader.COL_2:
			sortColumn = ConsumptionRecordDB.PAYMENT_ID_RECORD;
			break;
		case ContentHeader.COL_3:
			sortColumn = ConsumptionRecordDB.REF_NO_RECORD;
			break;
		case ContentHeader.COL_4:
			sortColumn = ConsumptionRecordDB.TRANS_DATE_RECORD;
			break;
		case ContentHeader.COL_5:
			sortColumn = ConsumptionRecordDB.TRANS_TIME_RECORD;
			break;
		case ContentHeader.COL_6:
			sortColumn = ConsumptionRecordDB.TRANS_AMOUNT_RECORD;
			break;
		case ContentHeader.COL_7:
			sortColumn = ConsumptionRecordDB.ORDER_STATE_RECORD;
			
			break;
		default:
			sortColumn = ConsumptionRecordDB.TRANS_TYPE_RECORD;
		}
		
		boolean isDesc = false;
		if (sortType == ContentHeader.SORT_DESCENDING) {
			isDesc = true;
		} else {
			isDesc = false;
			
		}
		String tmpStartDate = "";
		String tmpEndDate = "";
		if (!TextUtils.isEmpty(mStartDate) || !TextUtils.isEmpty(mEndDate)) {
			tmpStartDate = mStartDate.substring(0, 8);
			tmpEndDate = mEndDate.substring(0, 8);
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
