package cn.koolcloud.pos.controller.multipay;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import cn.koolcloud.pos.adapter.MultiPayRecordAdapter;
import cn.koolcloud.pos.controller.BaseController;
import cn.koolcloud.pos.util.UtilForJSON;
import cn.koolcloud.postest.R;

public class MultiPayRecord extends BaseController {

	private ListView lv_record;
	private MultiPayRecordAdapter adapter;
	private List<JSONObject> recordDataList;
	
	private TextView tv_paidAmount;
	private TextView tv_balance;
	private TextView tv_totalAmount;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (null == formData) {
			finish();
			return;
		}
		
		lv_record = (ListView) findViewById(R.id.multipay_record_lv_record);		
		adapter = new MultiPayRecordAdapter(this);		
		adapter.setHasMore(false);		
		lv_record.setAdapter(adapter);
		
		tv_paidAmount = (TextView) findViewById(R.id.multipay_record_tv_paidAmount);
		tv_balance = (TextView) findViewById(R.id.multipay_record_tv_balance);
		tv_totalAmount = (TextView) findViewById(R.id.multipay_record_tv_totalAmount);
		
		updateAmount();
		formData = null;
		
		startActivity(new Intent(this, MultiPayIndex.class));
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		Log.d(TAG, "onNewIntent");
		setIntent(intent);
		setFormData();
		updateAmount();
		updateListData();
		super.onNewIntent(intent);
	}
	
	private void updateListData(){
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		if(data == null){
			return;
		}
		JSONArray recordList = data.optJSONArray("orderList");
		recordDataList = UtilForJSON.JSONArrayOfJSONObjects2ListOfJSONObjects(recordList);
		adapter.setList(recordDataList);
	}
	
	private void updateAmount(){
		JSONObject data = formData.optJSONObject(getString(R.string.formData_key_data));
		if(data == null){
			return;
		}
		float totalAmount = Float.parseFloat(data.optString("totalAmount", "0"));
		float paidAmount = Float.parseFloat(data.optString("paidAmount", "0"));
		float balance = totalAmount - paidAmount;
		tv_paidAmount.setText("" + paidAmount + getString(R.string.transCurrency_text));
		tv_balance.setText("" + balance + getString(R.string.transCurrency_text));
		tv_totalAmount.setText("" + totalAmount + getString(R.string.transCurrency_text));
	}
	
	public void onPay(View view){
		onCall("MultiPay.onClickPay", null);
	}
	
	public void onComplete(View view){
		onCall("MultiPay.onClickComplete", null);
	}
	
	@Override
	public void onBackPressed() {
		onCall("MultiPay.onClickComplete", null);		
		super.onBackPressed();
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
		setContentView(R.layout.activity_multipay_record_controller);
	}

	@Override
	protected String getTitlebarTitle() {
		return getString(R.string.title_activity_multi_pay_controller);
	}

	@Override
	protected String getControllerName() {
		return getString(R.string.controllerName_MultiPayIndex);
	}

	@Override
	protected String getControllerJSName() {
		return null;
	}

}
