package util;

import constant.Constant;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created on 2016/05/30.
 */
public class DateUtil {

	/**
	 * String型の日付文字列,指定された形式からDATEを返す.
	 * @param dateStr
	 * @param format
	 * @return
	 */
	public static Date getDate(String dateStr, String format) throws ParseException {
		DateFormat df = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		return df.parse(dateStr);
	}


	/**
	 * Date型の日付から指定された形式のStringを返す.
	 * @param date
	 * @param format
	 * @return
	 */
	public static String getDateStr(Date date, String format) {
		if (date == null) {
			return null;
		} else {
			return new SimpleDateFormat(format).format(date);
		}
	}

	/**
	 * String型の日付文字列を元の形式から別の形式に変換する.
	 * @param strFrom
	 * @param formatFrom
	 * @param formatTo
	 * @return
	 */
	public static String getDateStrFromStr(String strFrom, String formatFrom, String formatTo) throws ParseException {
		return getDateStr(getDate(strFrom, formatFrom), formatTo);
	}

	/**
	 * String型の日付文字列のN日後を元と同じ形式で返却する.
	 */
	public static String getThatDateStr(String dateStr, String format, int N) throws ParseException {
		DateFormat df = new SimpleDateFormat(format);
		Calendar cal = Calendar.getInstance();
		Date date = df.parse(dateStr);
		cal.setTime(date);
		cal.add(Calendar.DATE, N);
		return df.format(cal.getTime());
	}

	/**
	 * Dateを年月日部分のみのDateにして返却する.
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public static Date getDateWithoutTime(Date date) throws ParseException {
		// yyyyMMdd形式にparseして再度Dateに戻す
		// TODO もっと良いやり方あるはず
		return getDate(getDateStr(date, Constant.DATE_FORMAT_yMd), Constant.DATE_FORMAT_yMd);
	}

	/**
	 * Date型の日付のN日後を返却する.
	 * @param date
	 * @param N
	 * @return
	 */
	public static Date getThatDate(Date date, int N) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, N);
		return cal.getTime();
	}
}
