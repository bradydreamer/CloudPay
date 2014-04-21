package cn.koolcloud.printer.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	
}
