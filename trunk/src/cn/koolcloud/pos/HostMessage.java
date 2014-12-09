package cn.koolcloud.pos;

import android.content.Context;
import android.text.TextUtils;

import java.util.Hashtable;

public class HostMessage {

	private static Hashtable<String, Integer> messageMap = new Hashtable<String, Integer>();
	private static Hashtable<String, Integer> jsMsgMap = new Hashtable<String, Integer>();

	public HostMessage() {

	}

	private static void init() {
		if (messageMap.size() <= 0) {
			System.out.println("init");
			messageMap.put("00", R.string.response_msg_transaction_success_00);//交易成功
			messageMap.put("01", R.string.response_msg_contact_with_bank_01);//"请持卡人与发卡银行联系"
			messageMap.put("02", R.string.response_msg_contact_with_bank_01);//请持卡人与发卡银行联系");
			messageMap.put("03", R.string.response_msg_invalid_merchant_03);//无效商户");
			messageMap.put("04", R.string.response_msg_card_forfeited_04);//此卡被没收");
			messageMap.put("05", R.string.response_msg_card_holder_verify_failure_05);//持卡人认证失败");
			messageMap.put("06", R.string.response_msg_transaction_failure_contact_bank_06);//交易失败，请联系发卡机构");
			messageMap.put("10", R.string.response_msg_transaction_success_accepted_part_10);//交易成功，但为部分承兑");
			messageMap.put("11", R.string.response_msg_success_vip_11);//成功，VIP客户");
			messageMap.put("12", R.string.response_msg_invalid_transaction_12);//无效交易");
			messageMap.put("13", R.string.response_msg_invalid_amount_13);//无效金额");
			messageMap.put("14", R.string.response_msg_invalid_card_number_14);//无效卡号");
			messageMap.put("15", R.string.response_msg_no_bank_card_15);//此卡无对应发卡方");
			messageMap.put("19", R.string.response_msg_failure_contact_bank_19);//交易失败，请联系发卡机构");
			messageMap.put("21", R.string.response_msg_card_not_active_21);//该卡未初始化或睡眠卡");
			messageMap.put("22", R.string.response_msg_reoperation_after_batch_sign_out_22);//请在批结、签退之后重新操作");
			messageMap.put("25", R.string.response_msg_no_original_order_contact_bank_25);//无原始交易，请联系发卡行");
			messageMap.put("30", R.string.response_msg_try_again_30);//请重试");
			messageMap.put("34", R.string.response_msg_warning_fake_card_34);//作弊卡");
			messageMap.put("38", R.string.response_msg_pin_error_times_over_contact_bank_38);//密码错误次数超限，请与发卡方联系");
			messageMap.put("40", R.string.response_msg_transaction_failure_contact_bank_40);//交易失败，请联系发卡方");
			messageMap.put("41", R.string.response_msg_report_loss_card_forfeit_41);//挂失卡，请没收");
			messageMap.put("43", R.string.response_msg_lost_card_forfeit_43);//被窃卡，请没收");
			messageMap.put("51", R.string.response_msg_insufficient_balance_51);//可用余额不足");
			messageMap.put("54", R.string.response_msg_card_out_of_date_54);//该卡已过期");
			messageMap.put("55", R.string.response_msg_pin_error_55);//密码错");
			messageMap.put("57", R.string.response_msg_transaction_is_not_allowed_57);//不允许此卡交易");
			messageMap.put("58", R.string.response_msg_transaction_not_allowed_on_terminal_58);//发卡方不允许该卡在本终端进行此交易");
			messageMap.put("59", R.string.response_msg_card_verify_error_59);//卡片校验错");
			messageMap.put("61", R.string.response_msg_amount_over_limit_61);//交易金额超限");
			messageMap.put("62", R.string.response_msg_limited_card_62);//受限制的卡");
			messageMap.put("64", R.string.response_msg_trans_amount_not_match_order_64);//交易金额与原交易不匹配");
			messageMap.put("65", R.string.response_msg_out_of_transaction_times_65);//超出消费次数限制");
			messageMap.put("68", R.string.response_msg_timeout_try_again_68);//交易超时，请重试");
			messageMap.put("75", R.string.response_msg_pin_error_times_out_limit_75);//密码错误次数超限");
			messageMap.put("90", R.string.response_msg_date_changing_retry_90);//日期切换正在处理，请稍后重试");
			messageMap.put("91", R.string.response_msg_card_status_unnormal_wait_retry_91);//发卡方状态不正常，请稍后重试");
			messageMap.put("92", R.string.response_msg_bank_connection_error_wait_retry_92);//发卡方线路异常，请稍后重试");
			messageMap.put("94", R.string.response_msg_repeated_transaction_wait_retry_94);//拒绝，重复交易，请稍后重试");
			messageMap.put("96", R.string.response_msg_exchange_center_error_wait_retry_96);//拒绝，交换中心异常,请稍后重试");
			messageMap.put("97", R.string.response_msg_terminal_not_registered_97);//终端未登记");
			messageMap.put("98", R.string.response_msg_bank_respond_timeout_98);//发卡方超时");
			messageMap.put("99", R.string.response_msg_pin_formatted_error_sign_in_99);//PIN格式错，请重新签到");
			messageMap.put("A0", R.string.response_msg_verify_mac_error_sign_in_A0);//MAC校验错，请重新签到");
			messageMap.put("F0", R.string.response_msg_set_pinpad_error_sign_in_F0);//设置密码键盘失败，请重新签到");
			messageMap.put("B1", R.string.response_msg_transaction_refused_remove_card_B1);//交易拒绝，请取卡！");
			messageMap.put("B2", R.string.response_msg_service_not_allowd_remove_card_B2);//交易中止，请取卡！");
			messageMap.put("B3", R.string.response_msg_transaction_approved_reversal_order_B3);//不允许的服务，请取卡！");
			messageMap.put("B4", R.string.response_msg_transaction_refused_remove_card_B4);//交易批准，冲正！");
			messageMap.put("C1", R.string.response_msg_transaction_interupt_remove_card_C1);//交易拒绝，请取卡！");
			messageMap.put("C2", R.string.response_msg_service_not_allowed_remove_card_C2);//交易中止，请取卡！");
			messageMap.put("C3", R.string.response_msg_transaction_approved_reversal_order_B3);//不允许的服务，请取卡！");
			messageMap.put("C4", R.string.response_msg_transaction_refused_remove_card_B4);//交易批准，冲正！");
			messageMap.put("D1", R.string.response_msg_transaction_interupt_remove_card_C1);//交易拒绝，请取卡！");
			messageMap.put("D2", R.string.response_msg_service_not_allowed_remove_card_C2);//交易中止，请取卡！");
			messageMap.put("D3", R.string.response_msg_transaction_approved_reversal_order_B3);//不允许的服务，请取卡！");
			messageMap.put("D4", R.string.response_msg_transaction_approved_reversal_order_B3);//交易批准，冲正！");

		}
	}

	private static void jsMsgInit() {
		if (jsMsgMap.size() <= 0) {
			jsMsgMap.put("100", R.string.alert_msg_100);
			jsMsgMap.put("101", R.string.alert_msg_101);
			jsMsgMap.put("102", R.string.alert_msg_102);
			jsMsgMap.put("103", R.string.alert_msg_103);
			jsMsgMap.put("104", R.string.alert_msg_104);
			jsMsgMap.put("105", R.string.alert_msg_105);
			jsMsgMap.put("106", R.string.alert_msg_106);
			jsMsgMap.put("107", R.string.alert_msg_107);
			jsMsgMap.put("108", R.string.alert_msg_108);
			jsMsgMap.put("109", R.string.alert_msg_109);
			jsMsgMap.put("110", R.string.alert_msg_110);
			jsMsgMap.put("111", R.string.alert_msg_111);
			jsMsgMap.put("112", R.string.alert_msg_112);
			jsMsgMap.put("113", R.string.alert_msg_113);
			jsMsgMap.put("114", R.string.alert_msg_114);
			jsMsgMap.put("115", R.string.alert_msg_115);
			jsMsgMap.put("116", R.string.alert_msg_116);
			jsMsgMap.put("117", R.string.alert_msg_117);
			jsMsgMap.put("118", R.string.alert_msg_118);
			jsMsgMap.put("119", R.string.alert_msg_119);
			jsMsgMap.put("120", R.string.alert_msg_120);
			jsMsgMap.put("121", R.string.alert_msg_121);
			jsMsgMap.put("122", R.string.alert_msg_122);
			jsMsgMap.put("123", R.string.alert_msg_123);
			jsMsgMap.put("124", R.string.alert_msg_124);
			jsMsgMap.put("125", R.string.alert_msg_125);
			jsMsgMap.put("126", R.string.alert_msg_126);
			jsMsgMap.put("127", R.string.alert_msg_127);
			jsMsgMap.put("128", R.string.alert_msg_128);
			jsMsgMap.put("129", R.string.alert_msg_129);
			jsMsgMap.put("130", R.string.alert_msg_130);
			jsMsgMap.put("131", R.string.alert_msg_131);
			jsMsgMap.put("132", R.string.alert_msg_132);
			jsMsgMap.put("133", R.string.alert_msg_133);
			jsMsgMap.put("134", R.string.alert_msg_134);
			jsMsgMap.put("135", R.string.alert_msg_135);
			jsMsgMap.put("136", R.string.alert_msg_136);
			jsMsgMap.put("137", R.string.alert_msg_137);
			jsMsgMap.put("138", R.string.alert_msg_138);
			jsMsgMap.put("139", R.string.alert_msg_139);
			jsMsgMap.put("140", R.string.alert_msg_140);
			jsMsgMap.put("141", R.string.alert_msg_141);
			jsMsgMap.put("142", R.string.alert_msg_142);
			jsMsgMap.put("143", R.string.alert_msg_143);
			jsMsgMap.put("144", R.string.alert_msg_144);
			jsMsgMap.put("145", R.string.alert_msg_145);
			jsMsgMap.put("146", R.string.alert_msg_146);
			jsMsgMap.put("147", R.string.alert_msg_147);
			jsMsgMap.put("148", R.string.alert_msg_148);
			jsMsgMap.put("149", R.string.alert_msg_149);
			jsMsgMap.put("150", R.string.alert_msg_150);
			jsMsgMap.put("151", R.string.alert_msg_151);
			jsMsgMap.put("152", R.string.alert_msg_152);
			jsMsgMap.put("153", R.string.alert_msg_153);
			jsMsgMap.put("154", R.string.alert_msg_154);
			jsMsgMap.put("155", R.string.alert_msg_155);
			jsMsgMap.put("156", R.string.alert_msg_156);
			jsMsgMap.put("157", R.string.alert_msg_157);
			jsMsgMap.put("158", R.string.alert_msg_158);
			jsMsgMap.put("159", R.string.alert_msg_159);
			jsMsgMap.put("160", R.string.alert_msg_160);
			jsMsgMap.put("161", R.string.alert_msg_161);
			jsMsgMap.put("162", R.string.alert_msg_162);
			jsMsgMap.put("163", R.string.alert_msg_163);
			jsMsgMap.put("164", R.string.alert_msg_164);
			jsMsgMap.put("165", R.string.alert_msg_165);
			jsMsgMap.put("166", R.string.alert_msg_166);
			jsMsgMap.put("167", R.string.alert_msg_167);
			jsMsgMap.put("169", R.string.alert_msg_169);
			jsMsgMap.put("170", R.string.alert_msg_170);
		}
	}

	public static String getMessage(String key) {
		init();
        Context context = MyApplication.getContext();
        String result = context.getString(messageMap.get(key));
		if (result == null) {
			result = context.getString(R.string.response_msg_unknown_error); //"未知错误";
		}
		return result;
	}

	public static String getJsMsg(String key){
		jsMsgInit();
		Context context = MyApplication.getContext();
        String result = "";
        if (jsMsgMap.get(key) == null) {
            if (!TextUtils.isDigitsOnly(key)) {
                result = key;
            } else {
                result = context.getString(R.string.alert_msg_168); //"未知信息!";
            }
        } else {

            result = context.getString(jsMsgMap.get(key));
            if(result == null){
                result = context.getString(R.string.alert_msg_168); //"未知信息!";
            }
        }
		return result;
	}

}
