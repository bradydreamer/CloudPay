package cn.koolcloud.pos;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import cn.koolcloud.pos.util.UtilForDataStorage;
import cn.koolcloud.control.ISO8583Controller;

public class ISO8583Engine {
	
	private static int transId = 0;
	private static int batchId = 0;
	
	private static ISO8583Engine instance = null;
	public static ISO8583Engine getInstance() {
		if (instance ==null) instance = new ISO8583Engine();
		return instance;
		
	}
	public ISO8583Controller generateISO8583Controller() {
			Map<String, ?> map = UtilForDataStorage.readPropertyBySharedPreferences(MyApplication.getContext(), "merchant");
			String mId = (String) map.get("merchId");
			String tID = (String) map.get("machineId");
			if (null == map.get("transId")) {
				transId = 0;
			} else {
				transId = ((Integer) map.get("transId")).intValue();
			}
			
			if (null == map.get("batchId")) {
				batchId = 0;
			} else {
				batchId = ((Integer) map.get("batchId")).intValue();
			}
			
			if(transId > 999999) {
				transId = 0;
			}

			Map<String, Object> newMerchantMap = new HashMap<String, Object>();
			newMerchantMap.put("mId", mId);
			newMerchantMap.put("tID", tID);
			newMerchantMap.put("batchId", Integer.valueOf(batchId));
			newMerchantMap.put("transId", Integer.valueOf(transId + 1));
			UtilForDataStorage.savePropertyBySharedPreferences(MyApplication.getContext(), "merchant", newMerchantMap);
			
			Log.d("ISO8583Engine", "ISO8583Engine mId : " + mId);
			Log.d("ISO8583Engine", "ISO8583Engine tID : " + tID);
			Log.d("ISO8583Engine", "ISO8583Engine batchId : " + batchId);
			Log.d("ISO8583Engine", "ISO8583Engine transId : " + transId);
			return new ISO8583Controller(mId, tID, transId, batchId);

	}
	
	public void updateLocalBatchNumber(){
		setLocalBatchNumber(batchId + 1);
	}
	
	public void setLocalBatchNumber(int batch){
		transId = 0;
		batchId = batch;
		Map<String, Object> newMerchantMap = new HashMap<String, Object>();
		newMerchantMap.put("transId", Integer.valueOf(transId + 1));
		newMerchantMap.put("batchId", Integer.valueOf(batchId));
		UtilForDataStorage.savePropertyBySharedPreferences(MyApplication.getContext(), "merchant", newMerchantMap);
	}
}
