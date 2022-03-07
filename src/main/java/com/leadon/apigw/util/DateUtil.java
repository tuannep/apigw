package com.leadon.apigw.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.TimeZone;

public class DateUtil {
	public static SimpleDateFormat FORMAT_TIMESTAMP_XXX = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	public static SimpleDateFormat FORMAT_TIMESTAMP_Z = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public static SimpleDateFormat FORMAT_DATE_YMD = new SimpleDateFormat("yyyy-MM-dd");
	public static SimpleDateFormat FORMAT_DATETIME_MMddHHmmss = new SimpleDateFormat("MMddHHmmss");
	public static SimpleDateFormat FORMAT_DATETIME_yyyyMMddHHmmss = new SimpleDateFormat("yyyyMMddHHmmss");
	public static SimpleDateFormat FORMAT_DATE_MMdd = new SimpleDateFormat("MMdd");
	public static SimpleDateFormat FORMAT_TIME_HHmmss = new SimpleDateFormat("HHmmss");
	public static SimpleDateFormat FORMAT_DATE_yyyy = new SimpleDateFormat("yyyy");
	private static final DateTimeFormatter oracleDTformatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
	public static SimpleDateFormat FORMAT_DATE_ddMMyyyy = new SimpleDateFormat("ddMMyyyy");
	public static SimpleDateFormat FORMAT_DATE_MMddyy = new SimpleDateFormat("MMddyy");

	// "Timestamp": "2020-09-30T11:22:34+07:00"
	public static Date parseTimestampXXX2Date(String timeStamp) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").parse(timeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static Date parseTimestampZ2Date(String timeStamp) {
		try {
			return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parse(timeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Date parseTimestampyyyyMMddHHmmss(String timeStamp) {
		try {
			String year = new SimpleDateFormat("yyyy").format(new Date());
//			FORMAT_DATETIME_yyyyMMddHHmmss.setTimeZone(TimeZone.getTimeZone("GMT+7:00"));
			return new SimpleDateFormat("yyyyMMddHHmmss").parse(year + timeStamp);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String formatTimeStampXXX(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(date);
	}

	public String formatTimeStampZ(Date date) {
		return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").format(date);
	}
	
	public static String formatTimeStampZGMT0(Date date) {
		FORMAT_TIMESTAMP_Z.setTimeZone(TimeZone.getTimeZone("GMT"));
		return FORMAT_TIMESTAMP_Z.format(date);
	}

	public static String formatDateYMD(Date date) {
		return FORMAT_DATE_YMD.format(date);
	}

	public static String formatMMddHHmmss(Date date) {
		return new SimpleDateFormat("MMddHHmmss").format(date);
	}

	public static String formatMMdd(Date date) {
		return new SimpleDateFormat("MMdd").format(date);
	}

	public static String formatHHmmss(Date date) {
		return new SimpleDateFormat("HHmmss").format(date);
	}

	public static String formatddMMyyyy(Date date) {
		return FORMAT_DATE_ddMMyyyy.format(date);
	}

	public static String formatMMddyy(Date date) {
		return FORMAT_DATE_MMddyy.format(date);
	}

	public static String formatDateTimeOracle(LocalDateTime date) {
		return oracleDTformatter.format(date);
	}

	public static void main(String[] args) {

	}
}
