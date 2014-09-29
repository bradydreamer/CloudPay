package cn.koolcloud.pos.controller.transaction_manage.consumption_record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import cn.koolcloud.pos.R;
import cn.koolcloud.pos.controller.BaseController;
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
	private final String CONSUME_ORIVALUE = "消费-0-0.00";
	private final String CONSUMECANCE_ORIVALUE = "消费撤销-0-0.00";
	private final String PREAUTH_ORIVALUE = "预授权-0-0.00";
	private final String PRAUTHCOMPLETE_ORIVALUE = "预授权完成联机-0-0.00";
	private final String PRAUTHSETTLEMENT_ORIVALUE = "预授权完成离线-0-0.00";
	private final String PRAUTHCANCEL_ORIVALUE = "预授权撤销-0-0.00";
	private final String PREAUTHCOMPLETECANCEL_ORIVALUE = "预授权完成撤销-0-0.00";

	private boolean removeJSTag = true;
	private JSONObject data, printData;

	private TextView merchantName, merchantID, mechineID, consume_count,
			consume_amount, consumeCancel_count, consumeCancel_amount,
			preAuth_count, preAuth_amount, preAuthComplete_count,
			preAuthComplete_amount, preAuthCompleteOffline_count,
			preAuthCompleteOffline_amount, preAuthCancel_count,
			preAuthCancel_amount, preAuthCompleteCancel_count,
			preAuthCompleteCancel_amount;
	private String merchantNameStr, merchantIDStr, mechineIDStr;
	private JSONArray summaryList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		merchantName = (TextView) findViewById(R.id.merchantName);
		merchantID = (TextView) findViewById(R.id.merchantID);
		mechineID = (TextView) findViewById(R.id.mechineID);

		data = formData.optJSONObject(getString(R.string.formData_key_data));
		if (data != null) {
			summaryList = data.optJSONArray("statistic");
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

		for (int i = 0; i < summaryList.length(); i++) {
			JSONObject summaryData = summaryList.optJSONObject(i);
			initSummaryData(summaryData);
		}

		// recordDataList = UtilForJSON
		// .JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);

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

	private void initSummaryData(JSONObject summaryData) {
		String transType = summaryData.optString("transType");
		String count = summaryData.optString("totalSize");
		String amount = UtilForMoney.fen2yuan(summaryData
				.optString("totalAmount"));
		if (transType.equals(APMP_TRAN_CONSUME)) {
			consume_count = (TextView) findViewById(R.id.consume_count);
			consume_amount = (TextView) findViewById(R.id.consume_amount);
			consume_count.setText(count);
			consume_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_CONSUME, "消费" + "-" + count + "-"
						+ amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_CONSUMECANCE)) {
			consumeCancel_count = (TextView) findViewById(R.id.consumeCancel_count);
			consumeCancel_amount = (TextView) findViewById(R.id.consumeCancel_amount);
			consumeCancel_count.setText(count);
			consumeCancel_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_CONSUMECANCE, "消费撤销" + "-" + count
						+ "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHCANCEL)) {
			preAuthCancel_count = (TextView) findViewById(R.id.preAuthCancel_count);
			preAuthCancel_amount = (TextView) findViewById(R.id.preAuthCancel_amount);
			preAuthCancel_count.setText(count);
			preAuthCancel_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_PRAUTHCANCEL, "预授权撤销" + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHCOMPLETE)) {
			preAuthComplete_count = (TextView) findViewById(R.id.preAuthComplete_count);
			preAuthComplete_amount = (TextView) findViewById(R.id.preAuthComplete_amount);
			preAuthComplete_count.setText(count);
			preAuthComplete_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_PRAUTHCOMPLETE, "预授权完成联机" + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PRAUTHSETTLEMENT)) {
			preAuthCompleteOffline_count = (TextView) findViewById(R.id.preAuthCompleteOffline_count);
			preAuthCompleteOffline_amount = (TextView) findViewById(R.id.preAuthCompleteOffline_amount);
			preAuthCompleteOffline_count.setText(count);
			preAuthCompleteOffline_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_PRAUTHSETTLEMENT, "预授权完成离线" + "-"
						+ count + "-" + amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PREAUTH)) {
			preAuth_count = (TextView) findViewById(R.id.preAuth_count);
			preAuth_amount = (TextView) findViewById(R.id.preAuth_amount);
			preAuth_count.setText(count);
			preAuth_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_PREAUTH, "预授权" + "-" + count + "-"
						+ amount);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (transType.equals(APMP_TRAN_PREAUTHCOMPLETECANCEL)) {
			preAuthCompleteCancel_count = (TextView) findViewById(R.id.preAuthCompleteCancel_count);
			preAuthCompleteCancel_amount = (TextView) findViewById(R.id.preAuthCompleteCancel_amount);
			preAuthCompleteCancel_count.setText(count);
			preAuthCompleteCancel_amount.setText(amount);
			try {
				printData.put(this.APMP_TRAN_PREAUTHCOMPLETECANCEL, "预授权完成撤销"
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
