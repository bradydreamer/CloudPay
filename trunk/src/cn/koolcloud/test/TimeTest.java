package cn.koolcloud.test;

import cn.koolcloud.parameter.UtilFor8583;
import cn.koolcloud.util.AppUtil;

public class TimeTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UtilFor8583 iso =  UtilFor8583.getInstance();
		iso.getCurrentDateTime();
		System.out.println(iso.currentDay);
		System.out.println(iso.currentMonth);
		int day = iso.currentDay;
		int month = iso.currentMonth;
		System.out.println(String.format("%02", day));

	}

}
