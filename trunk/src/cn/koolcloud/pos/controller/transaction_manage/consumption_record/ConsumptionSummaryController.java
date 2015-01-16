package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.SummaryListAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.printer.exception.PrinterException;
import cn.koolcloud.printer.PrinterHelper;
import cn.koolcloud.util.DateUtil;
import cn.koolcloud.util.NumberUtil;

public class ConsumptionSummaryController extends BaseController {

	private final String APMP_TRAN_CONSUME = "1021";
	private final String APMP_TRAN_CONSUMECANCE = "3021";
	private final String APMP_TRAN_PREAUTH = "1011";
	private final String APMP_TRAN_PRAUTHCOMPLETE = "1031";
	private final String APMP_TRAN_PRAUTHSETTLEMENT = "1091";
	private final String APMP_TRAN_PRAUTHCANCEL = "3011";
	private final String APMP_TRAN_PREAUTHCOMPLETECANCEL = "3031";
	private String CONSUME_ORIVALUE = "消费-0-0.00";
	private String CONSUMECANCE_ORIVALUE = "消费撤销-0-0.00";
	private String PREAUTH_ORIVALUE = "预授权-0-0.00";
	private String PRAUTHCOMPLETE_ORIVALUE = "预授权完成联机-0-0.00";
	private String PRAUTHSETTLEMENT_ORIVALUE = "预授权完成离线-0-0.00";
	private String PRAUTHCANCEL_ORIVALUE = "预授权撤销-0-0.00";
	private String PREAUTHCOMPLETECANCEL_ORIVALUE = "预授权完成撤销-0-0.00";
	private final String CACHE_TITLE = "Cache";
	private String jsonData1="[{\"transType\":\"1021\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"3021\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"1011\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"1031\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"1091\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"3011\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
			"{\"transType\":\"3031\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}]";

	private boolean removeJSTag = true;
	private JSONObject data, printData;

	private TextView merchantName, merchantID, mechineID;
	private String merchantNameStr, merchantIDStr, mechineIDStr;
	private JSONArray summaryListArray;
	private JSONArray summaryOriArray;
	private List<JSONObject> summaryDataList;
	private SummaryListAdapter summaryListAdapter;
	private ListView summaryList;
	private TextView totalAmountContent;
	private Spinner operator_sp;
	private List<String> sp_content = new ArrayList<String>();
	private ArrayAdapter<String> adapter;
	private String curOperator = "All";
	private Map<String, Object> sumMap;
	private String totalAmountStr = "0.00";
	private Boolean isFirst = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		isFirst = true;
		CONSUME_ORIVALUE = getString(R.string.consumption_summary_consume) + "-0-0.00";
		CONSUMECANCE_ORIVALUE = getString(R.string.consumption_summary_consumeCancel) + "-0-0.00";
		PREAUTH_ORIVALUE = getString(R.string.consumption_summary_preAuth) + "-0-0.00";
		PRAUTHCOMPLETE_ORIVALUE = getString(R.string.consumption_summary_preAuthComplete) + "-0-0.00";
		PRAUTHSETTLEMENT_ORIVALUE = getString(R.string.consumption_summary_preAuthCompleteOffline) + "-0-0.00";
		PRAUTHCANCEL_ORIVALUE = getString(R.string.consumption_summary_preAuthCancel) + "-0-0.00";
		PREAUTHCOMPLETECANCEL_ORIVALUE = getString(R.string.consumption_summary_preAuthCompleteCancel) + "-0-0.00";
		try {
			summaryOriArray = new JSONArray(jsonData1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		initSpinner();
		merchantName = (TextView) findViewById(R.id.merchantName);
		merchantID = (TextView) findViewById(R.id.merchantID);
		mechineID = (TextView) findViewById(R.id.mechineID);

		data = formData.optJSONObject(getString(R.string.formData_key_data));
		if (data != null) {
			summaryListArray = data.optJSONArray("statistic");
			merchantNameStr = data.optString("merchName");
			merchantIDStr = data.optString("merchId");
			mechineIDStr = data.optString("machineId");
			merchantName.setText(merchantNameStr);
			merchantID.setText(merchantIDStr);
			mechineID.setText(mechineIDStr);
		}

		cacheAllData("All");
		updateData();
		summaryListAdapter = new SummaryListAdapter(this);
		summaryListAdapter.setList(summaryDataList);
		summaryList = (ListView)findViewById(R.id.summaryList);
		summaryList.setAdapter(summaryListAdapter);


        //add currency mod by Teddy --start on 3th December
        TextView amountTitleTextView = (TextView) findViewById(R.id.amountTitleTextView);
        String formattingCurrency = getResources().getString(R.string.consumption_summary_transAmount);
        String currencyResource = Env.getCurrencyResource(this);
        amountTitleTextView.setText(String.format(formattingCurrency, currencyResource));
        //add currency mod by Teddy --end on 3th December
		TextView totalAmountTitle = (TextView)findViewById(R.id.today_total_amount);
		String totalAmountTitleText = getResources().getString(R.string.consumption_summary_total_amount);
		totalAmountTitle.setText(String.format(totalAmountTitleText,Env.getCurrencyResource(this)));
		totalAmountContent = (TextView)findViewById(R.id.today_total_amount_content);
		totalAmountContent.setText(totalAmountStr);
    }

	private void cacheAllData(String operator){
		UtilForDataStorage.clearPropertyBySharedPreferences(this,CACHE_TITLE + operator);
		sumMap = new HashMap<String,Object>();
		sumMap.put("data",summaryListArray.toString());
		UtilForDataStorage.savePropertyBySharedPreferences(this,CACHE_TITLE + operator,sumMap);
	}
	private void updateData(){
		initPrintData();
		try {
			summaryOriArray = new JSONArray(jsonData1);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		totalAmountStr = "0.00";
		for (int i = 0; i < summaryListArray.length(); i++) {
			JSONObject summaryData = summaryListArray.optJSONObject(i);
			int index = getJsonObjectIndex(summaryData.optString("transType"),summaryOriArray);
			try {
				if (index >= 0) {
					summaryOriArray.put(index,summaryData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			getSummaryPrintData(summaryData);
		}

		summaryDataList = UtilForJSON
				.JSONArrayOfJSONObjects2ListOfJSONObjects(summaryOriArray);
	}

	private void initPrintData(){
		printData = new JSONObject();
		try {
			printData.put("merchantName", merchantNameStr);
			printData.put("merchantID", merchantIDStr);
			printData.put("mechineID", mechineIDStr);
			printData.put(this.APMP_TRAN_CONSUME, CONSUME_ORIVALUE);
			printData.put(this.APMP_TRAN_CONSUMECANCE, CONSUMECANCE_ORIVALUE);
			printData.put(this.APMP_TRAN_PREAUTH, PREAUTH_ORIVALUE);
			printData.put(this.APMP_TRAN_PRAUTHCANCEL, PRAUTHCANCEL_ORIVALUE);
			printData.put(this.APMP_TRAN_PRAUTHCOMPLETE,
					PRAUTHCOMPLETE_ORIVALUE);
			printData.put(this.APMP_TRAN_PRAUTHSETTLEMENT,
					PRAUTHSETTLEMENT_ORIVALUE);
			printData.put(this.APMP_TRAN_PREAUTHCOMPLETECANCEL,
					PREAUTHCOMPLETECANCEL_ORIVALUE);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initSpinner(){
		operator_sp = (Spinner)findViewById(R.id.spinner_operator);
		sp_content.add("All");
		Map<String, ?> map = UtilForDataStorage
				.readPropertyBySharedPreferences(this, "userList");
		if(map.size() > 0) {
			JSONObject operatorObj = new JSONObject(map);
			Iterator<String> keys = operatorObj.keys();
			while (keys.hasNext()) {
				String str = keys.next();
				if (!str.equals("curMonth") && !str.equals("curDate")) {
					sp_content.add(operatorObj.optString(str));
				/*
				 *每次进入当日消费汇总时，都要清除遍前一次进入时缓存的汇总数据，以便实时显示。
				 */
					UtilForDataStorage.clearPropertyBySharedPreferences(this, CACHE_TITLE + operatorObj.optString(str));
				}
			}
		}

		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, sp_content);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		operator_sp.setAdapter(adapter);
		operator_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
			                           int position, long id) {
				curOperator = parent.getItemAtPosition(position).toString();
				if(isFirst && curOperator.equals("All")){
					isFirst = false;
					return;
				}
				if(checkCacheDate(curOperator)){
					Map<String, ?> cacheMp = UtilForDataStorage
							.readPropertyBySharedPreferences(ConsumptionSummaryController.this, CACHE_TITLE + curOperator);
					JSONArray jsonArray = null;
					if(cacheMp.size() > 0) {
						try {
							jsonArray = new JSONArray((String) cacheMp.get("data"));
						} catch (JSONException e) {
							e.printStackTrace();
						}
						freshDateList(jsonArray);
						return;
					}
				}
				Date today = new Date();
				String todayStr = DateUtil.formatDate(today, "yyyy-MM-dd"); //获取当地日期（默认）
				String startDate = todayStr + " 00:00:00"; //获取当的日期+起始时间
				startDate = DateUtil.formatDate(DateUtil.parseDate(startDate),"yyyyMMddHHmmss", TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的起始时间
				String endDate = todayStr + " 23:59:59"; //获取当的日期+最终时间
				endDate = DateUtil.formatDate(DateUtil.parseDate(endDate),"yyyyMMddHHmmss",TimeZone.getTimeZone("GMT+08:00")); //转换成G8时区的最终时间
				JSONObject msg = new JSONObject();
				try {
					msg.put("startDate", startDate);
					msg.put("endDate", endDate);
					msg.put("operator",curOperator);
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if(curOperator.equals("All")){
					msg.remove("operator");
				}
				onCall("ConsumptionSummary.gotoRefreshConsumptionSummary", msg);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {
				// Toast.makeText(getApplicationContext(), "没有改变的处理",
				// Toast.LENGTH_LONG).show();
			}

		});
	}
	private Boolean checkCacheDate(String operator){
		Boolean ret = false;
		if(UtilForDataStorage.readPropertyBySharedPreferences(this,CACHE_TITLE + operator).size() == 0 ||
				UtilForDataStorage.readPropertyBySharedPreferences(this,CACHE_TITLE + operator) == null){
			ret = false;
		}else{
			ret = true;
		}
		return ret;
	}

	private void freshDateList(JSONArray data){
		summaryListArray = data;
		updateData();
		summaryListAdapter.setList(summaryDataList);
		summaryListAdapter.notifyDataSetChanged();
		totalAmountContent.setText(totalAmountStr);
	}

	@Override
	public void setProperty(JSONArray data) {
		if(data != null) {
			for (int i = 0; i < data.length(); i++) {
				JSONObject item = data.optJSONObject(i);
				if(item != null && item.optJSONObject("value") != null) {
					summaryListArray = item.optJSONObject("value").optJSONArray("statistic");
					cacheAllData(curOperator);
					updateData();
					summaryListAdapter.setList(summaryDataList);
					summaryListAdapter.notifyDataSetChanged();
					totalAmountContent.setText(totalAmountStr);
				}
			}
		}
	}

	private int getJsonObjectIndex(String transType,JSONArray jsonArray){
		for(int i = 0; i < jsonArray.length(); i++){
			JSONObject jsData = jsonArray.optJSONObject(i);
			if(jsData.optString("transType").equals(transType)){
				return i;
			}
		}
		return -1;
	}

	@Override
	protected void setControllerContentView() {
		setContentView(R.layout.activity_consumption_summary_info_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_consumption_summary);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_ConsumptionSummary);
	}

	@Override
	protected String getControllerJSName() {
		return getString(R.string.controllerJSName_ConsumptionSummary);
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	public void onBackClick(View v){
		onBackPressed();
	}

	private void getSummaryPrintData(JSONObject summaryData) {
		String transType = summaryData.optString("transType");
		String count = summaryData.optString("totalSize");
		String amount = UtilForMoney.fen2yuan(summaryData
				.optString("totalAmount"));
//		totalAmountStr = NumberUtil.add(totalAmountStr,amount);
		try {
			printData.put("operator", curOperator);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		if (transType.equals(APMP_TRAN_CONSUME)) {
			totalAmountStr = NumberUtil.add(totalAmountStr,amount);
			try {
				printData.put(this.APMP_TRAN_CONSUME, getString(R.string.consumption_summary_consume) + "-" + count + "-"
						+ amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_CONSUMECANCE)) {
			totalAmountStr = NumberUtil.sub(totalAmountStr,amount);
			try {
				printData.put(this.APMP_TRAN_CONSUMECANCE, getString(R.string.consumption_summary_consumeCancel) + "-" + count
						+ "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHCANCEL)) {
			try {
				printData.put(this.APMP_TRAN_PRAUTHCANCEL, getString(R.string.consumption_summary_preAuthCancel) + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHCOMPLETE)) {
			totalAmountStr = NumberUtil.add(totalAmountStr,amount);
			try {
				printData.put(this.APMP_TRAN_PRAUTHCOMPLETE, getString(R.string.consumption_summary_preAuthComplete) + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHSETTLEMENT)) {
			totalAmountStr = NumberUtil.add(totalAmountStr,amount);
			try {
				printData.put(this.APMP_TRAN_PRAUTHSETTLEMENT, getString(R.string.consumption_summary_preAuthCompleteOffline) + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PREAUTH)) {
			try {
				printData.put(this.APMP_TRAN_PREAUTH, getString(R.string.consumption_summary_preAuth) + "-" + count + "-"
						+ amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
			totalAmountStr = NumberUtil.sub(totalAmountStr,amount);
			try {
				printData.put(this.APMP_TRAN_PREAUTHCOMPLETECANCEL, getString(R.string.consumption_summary_preAuthCompleteCancel)
						+ "-" + count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public void onPrintSummary(View v) {
		try {
			printData.put("totalAmount",totalAmountStr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		PrinterThread printTh = new PrinterThread(printData);
		printTh.start();
	}

	@Override
	protected boolean getRemoveJSTag() {
		// TODO Auto-generated method stub
		return removeJSTag;
	}

	private class PrinterThread extends Thread {
		private JSONObject printData;

		public PrinterThread(JSONObject printData) {
			this.printData = printData;
		}

		@Override
		public void run() {
			try {
				PrinterHelper.getInstance(ConsumptionSummaryController.this)
						.printTransSummary(printData);
			} catch (PrinterException e) {
				e.printStackTrace();
			}
		}
	}

}
