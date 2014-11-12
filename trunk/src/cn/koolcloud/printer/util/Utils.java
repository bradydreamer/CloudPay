package cn.koolcloud.printer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.widget.TextView;
import cn.koolcloud.constant.ConstantUtils;
import cn.koolcloud.pos.util.UtilForMoney;

public class Utils {

	/**
	 * remove item from array by index
	 * @param array
	 * @param index
	 * @return
	 */
	public static String[] deleteAt(String[] array, int index) { 
		int length = array.length - 1;
		String[] ret = new String[length];
		System.arraycopy(array, 0, ret, 0, index); 
		System.arraycopy(array, index + 1, ret, index, length - index);
		return ret;
	}
	
	/**
	 * check url
	 * http://www.sysrage.net | https://64.81.85.161/site/file.php?cow=moo's | ftp://user:pass@host.com:123
	 * @param url
	 * @return
	 */
	public static boolean isHttpUrl(String url) {
		boolean result = false;  
        String regEx = "^(http|www|ftp|)?(://)?(//w+(-//w+)*)(//.(//w+(-//w+)*))*((://d+)?)(/(//w+(-//w+)*))*(//.?(//w)*)(//?)?(((//w*%)*(//w*//?)*(//w*:)*(//w*//+)*(//w*//.)*(//w*&)*(//w*-)*(//w*=)*(//w*%)*(//w*//?)*(//w*:)*(//w*//+)*(//w*//.)*(//w*&)*(//w*-)*(//w*=)*)*(//w*)*)$";
        Pattern pattern = Pattern.compile(regEx);  
        Matcher matcher = pattern.matcher(url);  
          
        result = matcher.matches();  
        return result;  
	}
	
	public static String getCurrentDateTime(String format) {
		
		   return new SimpleDateFormat(format).format(Calendar.getInstance().getTime());
	}
	
	public static String getCurrentDateTime() {
		   return getCurrentDateTime("yyyy-MM-dd HH:mm:ss");
	}
	
	public static String getCurrentDate() {
		return getCurrentDateTime("yyyy/MM/dd");
	}
	
	public static String getCurrentTime() {
		return getCurrentDateTime("HH:mm:ss");
	}
	
	public static JSONObject initSummaryData(JSONObject summaryData) {
		JSONObject printData = new JSONObject();
		
		JSONArray summaryList = summaryData.optJSONArray("statistic");
		if (summaryList != null) {
			
			for (int i = 0; i < summaryList.length(); i++) {
				JSONObject itemData = summaryList.optJSONObject(i);
				String transType = itemData.optString("transType");
				String count = itemData.optString("totalSize");
				String amount = UtilForMoney.fen2yuan(itemData.optString("totalAmount"));
				if (transType.equals(ConstantUtils.APMP_TRAN_TYPE_CONSUME)) {
					try {
						printData.put(ConstantUtils.APMP_TRAN_TYPE_CONSUME, "消费" + "-" + count + "-"
								+ amount);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else if (transType.equals(ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE)) {
					try {
						printData.put(ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE, "消费撤销" + "-" + count
								+ "-" + amount);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			if (summaryList.length() == 1) {
				try {
					printData.put(ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE, "消费撤销" + "-" + "0"
							+ "-" + "0.00");
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		} else {
			try {
				printData.put(ConstantUtils.APMP_TRAN_TYPE_CONSUME, "消费" + "-" + 0 + "-" + 0.00);
				printData.put(ConstantUtils.APMP_TRAN_TYPE_CONSUMECANCE, "消费撤销" + "-" + 0 + "-" + 0.00);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return printData;
	}
	
}
