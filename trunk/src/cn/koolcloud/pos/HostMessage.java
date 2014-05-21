package cn.koolcloud.pos;
import java.util.Hashtable;

public class HostMessage {

	private static Hashtable<String, String> messageMap = new Hashtable<String, String>();

	public HostMessage() {

	}

	private static void init() {
		if (messageMap.size() <= 0) {
			System.out.println("init");
			messageMap.put("00", "交易成功");
			messageMap.put("01", "请持卡人与发卡银行联系");
			messageMap.put("02", "请持卡人与发卡银行联系");
			messageMap.put("03", "无效商户");
			messageMap.put("04", "此卡被没收");
			messageMap.put("05", "持卡人认证失败");
			messageMap.put("06", "交易失败，请联系发卡机构");
			messageMap.put("10", "交易成功，但为部分承兑");
			messageMap.put("11", "成功，VIP客户");
			messageMap.put("12", "无效交易");
			messageMap.put("13", "无效金额");
			messageMap.put("14", "无效卡号");
			messageMap.put("15", "此卡无对应发卡方");
			messageMap.put("21", "该卡未初始化或睡眠卡");
			messageMap.put("22", "请批结并重新登录和签到");
			messageMap.put("25", "无原始交易，请联系发卡行");
			messageMap.put("30", "请重试");
			messageMap.put("34", "作弊卡");
			messageMap.put("38", "密码错误次数超限，请与发卡方联系");
			messageMap.put("40", "交易失败，请联系发卡方");
			messageMap.put("41", "挂失卡，请没收");
			messageMap.put("43", "被窃卡，请没收");
			messageMap.put("51", "可用余额不足");
			messageMap.put("54", "该卡已过期");
			messageMap.put("55", "密码错");
			messageMap.put("57", "不允许此卡交易");
			messageMap.put("58", "发卡方不允许该卡在本终端进行此交易");
			messageMap.put("59", "卡片校验错");
			messageMap.put("61", "交易金额超限");
			messageMap.put("62", "受限制的卡");
			messageMap.put("64", "交易金额与原交易不匹配");
			messageMap.put("65", "超出消费次数限制");
			messageMap.put("68", "交易超时，请重试");
			messageMap.put("75", "密码错误次数超限");
			messageMap.put("90", "日期切换正在处理，请稍后重试");
			messageMap.put("91", "发卡方状态不正常，请稍后重试");
			messageMap.put("92", "发卡方线路异常，请稍后重试");
			messageMap.put("94", "拒绝，重复交易，请稍后重试");
			messageMap.put("96", "拒绝，交换中心异常,请稍后重试");
			messageMap.put("97", "终端未登记");
			messageMap.put("98", "发卡方超时");
			messageMap.put("99", "PIN格式错，请重新签到");
			messageMap.put("A0", "MAC校验错，请重新签到");
			messageMap.put("F0", "设置密码键盘失败，请重新签到");
		}
	}

	public static String getMessage(String key) {
		init();
		String result = messageMap.get(key);
		if (result == null) {
			result = "未知错误";
		}
		return result;
	}

}
