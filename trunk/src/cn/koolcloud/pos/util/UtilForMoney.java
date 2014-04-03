package cn.koolcloud.pos.util;

import java.math.BigDecimal;

public class UtilForMoney {
	public static String fen2yuan(String fen) {
		if (null == fen || fen.isEmpty()) {
			fen = "0";
		}
		double double_fen = Double.parseDouble(String.valueOf(Long.parseLong(fen)));
		BigDecimal bd = new BigDecimal(double_fen/100.0);
    	bd = bd.setScale(2,BigDecimal.ROUND_HALF_UP); 
		return bd.toString();
	}
	
	public static String yuan2fen(String yuan) {
		double double_yuan = Double.parseDouble(yuan);
		double double_fen = double_yuan * 100; 
		BigDecimal bd = new BigDecimal(double_fen);
    	bd = bd.setScale(0,BigDecimal.ROUND_HALF_UP); 
		return bd.toString();
	}
}
