package com.scrcu.ebank.ebap.batch.utils;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils {
	public static final String HHMMSSSS = "HHmmssSS";
	public static final String YYYYMMDD = "yyyyMMdd";
	public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	public static final String HHMMSS = "HHmmss";
	public static final String DDHHMMSSSSSS = "ddHHmmssSSSS";
	public static final String YYMMDDHHMMSSSSS = "yyMMddHHmmssSSS";
	public static final String YYYYMMDDHHMMSSSSS = "yyyyMMddHHmmssSSS";
	public static final String YYMMDD = "yyMMdd";
	public static final String MMDD = "MMdd";
	public static final String MMDDHHMMSS = "MMddHHmmss";
	public static final String DDHHMMSSSSS = "ddHHmmssSSS";
	public static final String DDHHMMSS = "ddHHmmss";
	public static final String YYYYMMDD2 = "yyyy/MM/dd";
	public static final String YYYYMMDDHHMMSS2 = "yyyy-MM-dd HH:mm:ss";

	/**
	 * 获得系统当前时间
	 * @return
	 */
	public static String getCurrentTime() {
		return getDateStr(HHMMSS);
	}

	/**
	 * 获得系统当前日期
	 * @return
	 */
	public static String getCurrentDate() {
		return getDateStr(YYYYMMDD);
	}

	/**
	 * 获得系统当前日期
	 * @return
	 */
	public static String getCurrentDate17() {
		return getDateStr(YYYYMMDDHHMMSSSSS);
	}

	/**
	 * 获得系统当前日期
	 * @return
	 */
	public static String getCurrentDate14() {
		return getDateStr(YYYYMMDDHHMMSS);
	}

	/**
	 * 格式化日期
	 * @param date
	 * @return
	 */
	public static String formatDate(String date) {
		date = date.trim();
		if (date.length() == 8) {
			return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8);
		} else if (date.length() == 10) {
			return date.substring(0, 2) + "/" + date.substring(2, 4) + " " + date.substring(4, 6) + ":"
					+ date.substring(6, 8) + ":" + date.substring(8, 10);
		} else if (date.length() == 14) {
			return date.substring(0, 4) + "/" + date.substring(4, 6) + "/" + date.substring(6, 8) + " "
					+ date.substring(8, 10) + ":" + date.substring(10, 12) + ":" + date.substring(12, 14);
		} else {
			return date;
		}
	}
	
	public static String getDateStr(String formatPattern) {
		Date date = new Date();
		return getDateStr(date, formatPattern);
	}
	
	private static String getDateStr(Date date, String formatPattern) {
		String dateStr = "";
		if (date == null) {
			date = new Date();
		}
			
		try {
			SimpleDateFormat df = new SimpleDateFormat(formatPattern);
			dateStr = df.format(date);
		} catch (Exception e) {
			log.error(e.toString());
		}
		return dateStr;
	}
	
	public static String getYYMMDDHHMMSSSSS() {
		return getDateStr(YYMMDDHHMMSSSSS);
	}
	
	public static String getDateStr(Date date, int minutes) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, minutes);
		return sdf.format(cal.getTime());
	}

	public static String getTodayStr(String str) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(str);
		return sdf.format(date);
	}
	
	public static String get7DayStr(String str) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(str);
		long a = date.getTime() - 7 * 24 * 60 * 60 * 1000;
		System.out.println(a);
		date.setTime(a);
		return sdf.format(date);
	}
	
	public static String get30DayStr(String str) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		Date date = sdf.parse(str);
		long a = date.getTime() - 15 * 24 * 60 * 60 * 1000 - 15 * 24 * 60 * 60 * 1000;
		date.setTime(a);
		return sdf.format(date);
	}
	
	public static String getYYYYMMDDHHMMSSSSS() {
	    
	    return getDateStr(YYYYMMDDHHMMSSSSS);
	}
	/**
	 * 
	 * @Description: 获取下一天 格式 YYYYMMDD
	 * @return
	 * @time : 2017年11月7日 下午2:41:06
	 */
	public static String getNextDay() {
        SimpleDateFormat df = new SimpleDateFormat(YYYYMMDD);
        long time = System.currentTimeMillis() + 24 * 60 * 60 * 1000;
        Date date = new Date(time);
        return df.format(date);
	}
	/**
	 * 
	 * @Description: 获取头一天日期
	 * @return
	 * @time : 2017年11月11日 上午10:59:57
	 */
    public static String getYestoDay() {
        SimpleDateFormat df = new SimpleDateFormat(YYYYMMDD);
        long time = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        Date date = new Date(time);
        return df.format(date);
    }
    
    /**
     * 
     * @Description: 将YYMMDD字符串，改成当前日期
     * @param str 
     * @param date
     * @return
     * @time : 2017年11月11日 上午10:56:50
     */
    public static String changeDateMark(String str, String date) {
        return str.replaceFirst("YYYYMMDD", date);
    }
    
    /**
     * 
     * @Description: 将YYYYMMDDHHMMSSSSS型时间转换成毫秒级long型时间
     * @param strTime YYYYMMDDHHMMSSSSS
     * @return
     * @throws ParseException
     * @time : 2018年3月15日 上午11:53:24
     */
    public static long stringTimeToLongTime(String strTime) throws ParseException{

        SimpleDateFormat sdf= new SimpleDateFormat(YYYYMMDDHHMMSSSSS);
        Date dt2 = sdf.parse(strTime);
        return dt2.getTime() ;
    }    
    
    /**
     * 
     * @Description: 将yyyyMMdd型时间转换成毫秒级long型时间
     * @param strTime yyyyMMdd
     * @return
     * @throws ParseException
     * @time : 2018年3月15日 上午11:53:24
     */
    public static Long yyyyMMddToLongTime(String strTime) throws ParseException{

        SimpleDateFormat sdf= new SimpleDateFormat(YYYYMMDD);
        Date dt2 = sdf.parse(strTime);
        return dt2.getTime() ;
    }
    
}
