package cn.koolcloud.pos.util;

import java.math.BigDecimal;

import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

public class UtilForCurrencyEditText {
	public static void limitCurrencyInput(EditText editText, final int maxLength) {
    	editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength),new InputFilter() {
			
			@Override
			public CharSequence filter(CharSequence source, int start, int end,
					Spanned dest, int dstart, int dend) {
				StringBuilder accountBuilder = new StringBuilder(dest.toString());
				accountBuilder.append(source);
				String currencyStr;
				try {
					currencyStr = getCurrencyFormatString(accountBuilder.toString());
				} catch (Exception e) {
					return "";
				}
				if (currencyStr.length() > maxLength) {
					return "";
				}
				return null;
			}
		}});
    }
	
	public static String getCurrencyFormatString(String str) {
		if (null == str || str.length() == 0) {
			return str;
		}
    	BigDecimal bd = new BigDecimal(str);
    	bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP); 
    	return bd.toString();
    }
}
