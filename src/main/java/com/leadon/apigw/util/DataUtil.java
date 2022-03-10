package com.leadon.apigw.util;

import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Clob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

public class DataUtil {

	private DataUtil() {
		// Not call
	}

	private static final String FORMAT_DATE = "dd/MM/yyyy";

	public static boolean isValidEmail(String email) {
		String emailRegex = "[A-Z0-9a-z\\._%+-]+@([A-Za-z0-9-]+\\.)+[A-Za-z]{2,4}";
		return email.matches(emailRegex);
	}

	public static boolean isValidPhoneNumber(String phoneNumber) {
		return phoneNumber
				.matches("^\\s*(?:\\+?(\\d{1,3}))?[- (]*(\\d{3})[- )]*(\\d{3})[- ]*(\\d{4})(?: *[x/#]{1}(\\d+))?\\s*$");
	}

	// check nhieu so dien thoai
	public static boolean isValidMultiPhoneNumber(String phoneNumber) {
		if (DataUtil.isNullOrEmpty(phoneNumber)) {
			return false;
		}
		String[] input = phoneNumber.split(";");
		if (!DataUtil.isNullOrEmpty(input) && input.length > 0) {
			for (int i = 0; i < input.length; i++) {
				if (!isValidPhoneNumber(input[i]) || input[i].length() > 12) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Khong dung ham nay nua ma chuyen sang check thang == null
	 *
	 * @param obj1
	 * @return
	 */
	public static boolean isNullObject(Object obj1) {
		if (obj1 == null) {
			return true;
		}
		if (obj1 instanceof String) {
			return isNullOrEmpty(obj1.toString());
		}
		return false;
	}

	/**
	 * check null or empty Su dung ma nguon cua thu vien StringUtils trong apache
	 * common lang
	 *
	 * @param cs
	 *            String
	 * @return boolean
	 */
	public static boolean isNullOrEmpty(CharSequence cs) {
		int strLen;
		if (cs == null || (strLen = cs.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if (!Character.isWhitespace(cs.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	public static boolean isNullOrEmpty(final Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNullOrEmpty(final Object[] collection) {
		return collection == null || collection.length == 0;
	}

	/*
	 * Kiem tra Long bi null hoac zero
	 *
	 * @param value
	 *
	 * @return
	 */
	public static boolean isNullOrZero(Long value) {
		return (value == null || value.equals(0L));
	}

	/**
	 * @param obj1
	 *            Object
	 * @return Long
	 */
	public static Long safeToLong(Object obj1) {
		return safeToLong(obj1, 0L);
	}

	public static Long safeToLong(Object obj1, Long defaultValue) {
		if (obj1 == null) {
			return defaultValue;
		}
		if (obj1 instanceof BigDecimal) {
			return ((BigDecimal) obj1).longValue();
		}
		if (obj1 instanceof BigInteger) {
			return ((BigInteger) obj1).longValue();
		}

		try {
			return Long.parseLong(obj1.toString());
		} catch (final NumberFormatException nfe) {
			return defaultValue;
		}
	}

	/**
	 * safe equal
	 *
	 * @param obj1
	 *            Long
	 * @param obj2
	 *            Long
	 * @return boolean
	 */
	public static boolean safeEqual(Long obj1, Long obj2) {
		return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
	}

	/**
	 * safe equal
	 *
	 * @param obj1
	 *            INT
	 * @param obj2
	 *            INT
	 * @return boolean
	 */
	public static boolean safeEqual(Integer obj1, Integer obj2) {
		return ((obj1 != null) && (obj2 != null) && (obj1.compareTo(obj2) == 0));
	}

	/**
	 * safe equal
	 *
	 * @param obj1
	 *            String
	 * @param obj2
	 *            String
	 * @return boolean
	 */
	public static boolean safeEqual(String obj1, String obj2) {
		return ((obj1 != null) && (obj2 != null) && obj1.equals(obj2));
	}

	/**
	 * add
	 *
	 * @param obj1
	 *            BigDecimal
	 * @param obj2
	 *            BigDecimal
	 * @return BigDecimal
	 */
	public static BigInteger add(BigInteger obj1, BigInteger obj2) {
		if (obj1 == null) {
			return obj2;
		} else if (obj2 == null) {
			return obj1;
		}

		return obj1.add(obj2);
	}

	public static Date convertStringToDate(String s) {
		try {
			if (s != null) {
				return new SimpleDateFormat(FORMAT_DATE).parse(s);
			}
		} catch (ParseException e) {
			System.out.println(e.toString());
		}
		return null;
	}

	public static String convertDateToString(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static String convertNullToString(Object value) {
		if (isNullObject(value)) {
			return "";
		}
		return value.toString();
	}

	public static String convertClobtoString(Clob input) {
		try {
			if (StringUtils.isEmpty(input))
				return "";

			StringBuffer str = new StringBuffer();
			String strng;

			BufferedReader bufferRead = new BufferedReader(input.getCharacterStream());

			while ((strng = bufferRead.readLine()) != null)
				str.append(strng);

			return str.toString();
		} catch (Exception e) {
			System.out.println("error convert CLOB message to String-" + e.getMessage());
			return "";
		}
	}

	public static String valueStringOf(Object obj) {
		return (obj == null) ? "" : obj.toString();
	}

	public static void main(String[] args) {
		String x = null;
		BigDecimal b = BigDecimal.valueOf(100000000);
		String y = DataUtil.valueStringOf(b);
		System.out.println("AAAAAAAAAAAAA: " + y);
	}
}
