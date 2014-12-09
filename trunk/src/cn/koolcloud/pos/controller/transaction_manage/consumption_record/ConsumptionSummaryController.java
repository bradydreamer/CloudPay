package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cn.koolcloud.pos.R;
import cn.koolcloud.pos.adapter.SummaryListAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.Env;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.pos.util.UtilForMoney;
import cn.koolcloud.printer.PrinterException;
import cn.koolcloud.printer.PrinterHelper;

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

	private boolean removeJSTag = true;
	private JSONObject data, printData;

	private TextView merchantName, merchantID, mechineID;
	private String merchantNameStr, merchantIDStr, mechineIDStr;
	private JSONArray summaryListArray;
	private JSONArray summaryOriArray;
	private List<JSONObject> summaryDataList;
	private SummaryListAdapter summaryListAdapter;
	private ListView summaryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		CONSUME_ORIVALUE = getString(R.string.consumption_summary_consume) + "-0-0.00";
		CONSUMECANCE_ORIVALUE = getString(R.string.consumption_summary_consumeCancel) + "-0-0.00";
		PREAUTH_ORIVALUE = getString(R.string.consumption_summary_preAuth) + "-0-0.00";
		PRAUTHCOMPLETE_ORIVALUE = getString(R.string.consumption_summary_preAuthComplete) + "-0-0.00";
		PRAUTHSETTLEMENT_ORIVALUE = getString(R.string.consumption_summary_preAuthCompleteOffline) + "-0-0.00";
		PRAUTHCANCEL_ORIVALUE = getString(R.string.consumption_summary_preAuthCancel) + "-0-0.00";
		PREAUTHCOMPLETECANCEL_ORIVALUE = getString(R.string.consumption_summary_preAuthCompleteCancel) + "-0-0.00";
		String jsonData1="[{\"transType\":\"1021\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"3021\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"1011\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"1031\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"1091\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"3011\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}," +
				"{\"transType\":\"3031\",\"totalSize\":\"0\",\" totalAmount\":\"0\"}]";
		try {
			summaryOriArray = new JSONArray(jsonData1);
		} catch (JSONException e) {
			e.printStackTrace();
		}

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

		for (int i = 0; i < summaryListArray.length(); i++) {
			JSONObject summaryData = summaryListArray.optJSONObject(i);
			int index = getJsonObjectIndex(summaryData.optString("transType"),summaryOriArray);
			try {
				summaryOriArray.put(index,summaryData);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			getSummaryPrintData(summaryData);
		}
		summaryDataList = UtilForJSON
				.JSONArrayOfJSONObjects2ListOfJSONObjects(summaryOriArray);
		summaryListAdapter = new SummaryListAdapter(this);
		summaryListAdapter.setList(summaryDataList);
		summaryList = (ListView)findViewById(R.id.summaryList);
		summaryList.setAdapter(summaryListAdapter);


		// recordDataList = UtilForJSON
		// .JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);
        //add currency mod by Teddy --start on 3th December
        TextView amountTitleTextView = (TextView) findViewById(R.id.amountTitleTextView);
        String formattingCurrency = getResources().getString(R.string.consumption_summary_transAmount);
        String currencyResource = Env.getCurrencyResource(this);
        amountTitleTextView.setText(String.format(formattingCurrency, currencyResource));
        //add currency mod by Teddy --end on 3th December
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
		return null;
	}

	@Override
	protected void setRemoveJSTag(boolean tag) {
		removeJSTag = tag;

	}

	private void getSummaryPrintData(JSONObject summaryData) {
		String transType = summaryData.optString("transType");
		String count = summaryData.optString("totalSize");
		String amount = UtilForMoney.fen2yuan(summaryData
				.optString("totalAmount"));
		if (transType.equals(APMP_TRAN_CONSUME)) {
			try {
				printData.put(this.APMP_TRAN_CONSUME, getString(R.string.consumption_summary_consume) + "-" + count + "-"
						+ amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_CONSUMECANCE)) {
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
			try {
				printData.put(this.APMP_TRAN_PRAUTHCOMPLETE, getString(R.string.consumption_summary_preAuthComplete) + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHSETTLEMENT)) {
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
