package cn.koolcloud.util;

import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Gaolou.liu on 2014/11/27.
 */
public class DateUtil {

	private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 获取时区信息
	 * @return
	 */
	public static TimeZone getTimeZone(){
		return TimeZone.getDefault();
	}

	/**
	 * 将日期字符串转换为Date对象
	 * @param date 日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @return 日期字符串的Date对象表达形式
	 * */
	public static Date parseDate(String date){
		return parseDate(date, FORMAT);
	}

	/**
	 * 将日期字符串转换为Date对象
	 * @param date 日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @param format 格式化字符串
	 * @return 日期字符串的Date对象表达形式
	 * */
	public static Date parseDate(String date, String format){
		Date dt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		try{
			dt = dateFormat.parse(date);
		}catch(ParseException e){
			e.printStackTrace();
		}

		return dt;
	}

	public static Date parseData(String date,String format,TimeZone timeZone){
		Date dt = null;
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timeZone);
		try{
			dt = dateFormat.parse(date);
		}catch(ParseException e){
			e.printStackTrace();
		}

		return dt;
	}

	/**
	 * 将Date对象转换为指定格式的字符串
	 * @param date Date对象
	 * @return Date对象的字符串表达形式"yyyy-MM-dd HH:mm:ss"
	 * */
	public static String formatDate(Date date){
		return formatDate(date, FORMAT);
	}

	/**
	 * 将Date对象转换为指定格式的字符串
	 * @param date Date对象
	 * @param String format 格式化字符串
	 * @return Date对象的字符串表达形式
	 * */
	public static String formatDate(Date date, String format){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(date);
	}

	/**
	 * 将Date对象转换成指定时区的指定格式的字符串日期
	 * @param date Date对象
	 * @param format 指定的日期字符串格式
	 * @param timeZone 指定的时区
	 * @return 返回指定格式的日期
	 */
	public static String formatDate(Date date,String format,TimeZone timeZone){
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		dateFormat.setTimeZone(timeZone);
		return dateFormat.format(date);
	}

	/**
	 * 将字符串日期转换成指定时区的特定格式的字符串日期
	 * @param date 日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @param timeZone 指定的时区
	 * @return 字符串表达形式"yyyy-MM-dd HH:mm:ss"
	 */
	public static String convertTimeZone(String date,TimeZone timeZone)	{
		return convertTimeZone(date,timeZone,FORMAT);
	}

	/**
	 * 将字符串日期转换成指定时区的指定格式的字符串日期
	 * @param date 日期字符串，必须为"yyyy-MM-dd HH:mm:ss"
	 * @param timeZone 指定的时区
	 * @param format 指定的表达形式
	 * @return 指定字符串的表达形式
	 */
	public static String convertTimeZone(String date, TimeZone timeZone, String format) {
		Date dt = parseDate(date);
		return formatDate(dt,format,timeZone);
	}
}
