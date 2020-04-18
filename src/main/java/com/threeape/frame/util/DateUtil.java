package com.threeape.frame.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>
 * 时间处理工具类
 * </p>
 */
public class DateUtil {

	private DateUtil() {

	}

	private static final Logger log = LoggerFactory.getLogger(DateUtil.class);

	public static final long ONE_DAY_SECONDS = 86400;

	public static final String SHORT_FORMAT = "yyyyMMdd";
	public static final String LONG_FORMAT = "yyyyMMddHHmmss";
	public static final String WEB_FORMAT = "yyyy-MM-dd";
	public static final String OLD_FORMAT = "yyyy/MM/dd";
	public static final String TIME_FORMAT = "HHmmss";
	public static final String MONTH_FORMAT = "yyyyMM";
	public static final String MONTH_FORMAT_HLINE = "yyyy-MM";
	public static final String CHINESE_DT_FORMAT = "yyyy年MM月dd日";
	public static final String NEW_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String OLD_LONG_FORMAT = "yyyy/MM/dd HH:mm:ss";
	public static final String NO_SECOND_FORMAT = "yyyy-MM-dd HH:mm";
	public static final long ONE_DAY_MILL_SECONDS = 86400000;
	public static final String NEW_TIME_FORMAT = "HH:mm";
	public static final String START_HOUR_MIN_SEC = "00:00:00";
	public static final String END_HOUR_MIN_SEC = "23:59:59";

	private static final String PARSE_MSG = "length too little";

	private static final long validDate = -28800000L;
	private static final long invalidDate = 2524579200000L;

	public static DateFormat getNewDateFormat(String pattern) {
		DateFormat df = new SimpleDateFormat(pattern);

		df.setLenient(false);
		return df;
	}

	public static String format(Date date, String format) {
		if (date == null) {
			return null;
		}

		return new SimpleDateFormat(format).format(date);
	}

	public static Date parseDateNoTime(String sDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(SHORT_FORMAT);

		if ((sDate == null) || (sDate.length() < SHORT_FORMAT.length())) {
			throw new ParseException(PARSE_MSG, 0);
		}

		if (!StringUtils.isNumeric(sDate)) {
			throw new ParseException("not all digit", 0);
		}

		return dateFormat.parse(sDate);
	}

	public static Date parseDateNoTime(String sDate, String format) throws ParseException {
		if (StringUtils.isBlank(format)) {
			throw new ParseException("Null format. ", 0);
		}

		DateFormat dateFormat = new SimpleDateFormat(format);

		if ((sDate == null) || (sDate.length() < format.length())) {
			throw new ParseException(PARSE_MSG, 0);
		}

		return dateFormat.parse(sDate);
	}

	public static Date parseDate(String sDate, String format) throws ParseException {
		if (StringUtils.isBlank(format)) {
			throw new ParseException("Null format. ", 0);
		}
		sDate = sDate.replaceAll("\"", "");
		DateFormat dateFormat = new SimpleDateFormat(format);

		if (sDate == null) {
			throw new ParseException("sDate is null", 0);
		}

		return dateFormat.parse(sDate);
	}

	public static Date parseDateNoTimeWithDelimit(String sDate, String delimit) throws ParseException {
		sDate = sDate.replaceAll(delimit, "");

		DateFormat dateFormat = new SimpleDateFormat(SHORT_FORMAT);

		if ((sDate == null) || (sDate.length() != SHORT_FORMAT.length())) {
			throw new ParseException("length not match", 0);
		}

		return dateFormat.parse(sDate);
	}

	public static Date parseDateLongFormat(String sDate) {
		DateFormat dateFormat = new SimpleDateFormat(LONG_FORMAT);
		Date d = null;

		if ((sDate != null) && (sDate.length() == LONG_FORMAT.length())) {
			try {
				d = dateFormat.parse(sDate);
			} catch (ParseException ex) {
				return null;
			}
		}

		return d;
	}

	public static Date parseDateNewFormat(String sDate) {
		DateFormat dateFormat = new SimpleDateFormat(NEW_FORMAT);
		Date d = null;
		if ((sDate != null) && (sDate.length() == NEW_FORMAT.length())) {
			try {
				d = dateFormat.parse(sDate);
			} catch (ParseException ex) {
				return null;
			}
		}
		return d;
	}

	/**
	 * 计算当前时间几小时之后的时间
	 *
	 * @param date
	 * @param hours
	 * @return
	 */
	public static Date addHours(Date date, long hours) {
		return addMinutes(date, hours * 60);
	}

	/**
	 * 计算当前时间几分钟之后的时间
	 *
	 * @param date
	 * @param minutes
	 * @return
	 */
	public static Date addMinutes(Date date, long minutes) {
		return addSeconds(date, minutes * 60);
	}

	/**
	 * @param date1
	 * @param secs
	 * @return
	 */

	public static Date addSeconds(Date date1, long secs) {
		return new Date(date1.getTime() + (secs * 1000));
	}

	/**
	 * 判断输入的字符串是否为合法的小时
	 *
	 * @param hourStr
	 * @return true/false
	 */
	public static boolean isValidHour(String hourStr) {
		if (!StringUtils.isEmpty(hourStr) && StringUtils.isNumeric(hourStr)) {
			int hour = Integer.parseInt(hourStr);

			if ((hour >= 0) && (hour <= 23)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 判断输入的字符串是否为合法的分或秒
	 *
	 * @param
	 * @return true/false
	 */
	public static boolean isValidMinuteOrSecond(String str) {
		if (!StringUtils.isEmpty(str) && StringUtils.isNumeric(str)) {
			int hour = Integer.parseInt(str);

			if ((hour >= 0) && (hour <= 59)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 取得新的日期
	 *
	 * @param date1 日期
	 * @param days  天数
	 * @return 新的日期
	 */
	public static Date addDays(Date date1, long days) {
		return addSeconds(date1, days * ONE_DAY_SECONDS);
	}

	public static String getTomorrowDateString(String sDate) throws ParseException {
		Date aDate = parseDateNoTime(sDate);

		aDate = addSeconds(aDate, ONE_DAY_SECONDS);

		return getDateString(aDate);
	}

	public static String getLongDateString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(LONG_FORMAT);

		return getDateString(date, dateFormat);
	}

	public static String getNewFormatDateString(Date date) {
		DateFormat dateFormat = new SimpleDateFormat(NEW_FORMAT);
		return getDateString(date, dateFormat);
	}

	public static String getDateString(Date date, DateFormat dateFormat) {
		if (date == null || dateFormat == null) {
			return null;
		}

		return dateFormat.format(date);
	}

	public static String getYesterDayDateString(String sDate) throws ParseException {
		Date aDate = parseDateNoTime(sDate);

		aDate = addSeconds(aDate, -ONE_DAY_SECONDS);

		return getDateString(aDate);
	}

	/**
	 * @return 当天的时间格式化为"yyyyMMdd"
	 */
	public static String getDateString(Date date) {
		DateFormat df = getNewDateFormat(SHORT_FORMAT);

		return df.format(date);
	}

	public static String getWebDateString(Date date) {
		DateFormat dateFormat = getNewDateFormat(WEB_FORMAT);

		return getDateString(date, dateFormat);
	}

	public static String getNoSecondDateString(Date date) {
		DateFormat dateFormat = getNewDateFormat(NO_SECOND_FORMAT);

		return getDateString(date, dateFormat);
	}

	/**
	 * 取得“X年X月X日”的日期格式
	 *
	 * @param date
	 * @return
	 */
	public static String getChineseDateString(Date date) {
		DateFormat dateFormat = getNewDateFormat(CHINESE_DT_FORMAT);

		return getDateString(date, dateFormat);
	}

	public static String getTodayString() {
		DateFormat dateFormat = getNewDateFormat(SHORT_FORMAT);

		return getDateString(new Date(), dateFormat);
	}

	public static String getTimeString(Date date) {
		DateFormat dateFormat = getNewDateFormat(TIME_FORMAT);

		return getDateString(date, dateFormat);
	}

	public static String getBeforeDayString(int days) {
		Date date = new Date(System.currentTimeMillis() - (ONE_DAY_MILL_SECONDS * days));
		DateFormat dateFormat = getNewDateFormat(SHORT_FORMAT);

		return getDateString(date, dateFormat);
	}

	/**
	 * 取得两个日期间隔秒数（日期1-日期2）
	 *
	 * @param one 日期1
	 * @param two 日期2
	 * @return 间隔秒数
	 */
	public static long getDiffSeconds(Date one, Date two) {
		Calendar sysDate = new GregorianCalendar();

		sysDate.setTime(one);

		Calendar failDate = new GregorianCalendar();

		failDate.setTime(two);
		return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / 1000;
	}

	public static long getDiffMinutes(Date one, Date two) {
		Calendar sysDate = new GregorianCalendar();

		sysDate.setTime(one);

		Calendar failDate = new GregorianCalendar();

		failDate.setTime(two);
		return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (60 * 1000);
	}

	/**
	 * 取得两个日期的间隔天数
	 *
	 * @param one
	 * @param two
	 * @return 间隔天数
	 */
	public static long getDiffDays(Date one, Date two) {
		Calendar sysDate = new GregorianCalendar();

		sysDate.setTime(one);

		Calendar failDate = new GregorianCalendar();

		failDate.setTime(two);
		return (sysDate.getTimeInMillis() - failDate.getTimeInMillis()) / (24 * 60 * 60 * 1000);
	}

	public static String getBeforeDayString(String dateString, int days) {
		Date date;
		DateFormat df = getNewDateFormat(SHORT_FORMAT);

		try {
			date = df.parse(dateString);
		} catch (ParseException e) {
			date = new Date();
		}

		date = new Date(date.getTime() - (ONE_DAY_MILL_SECONDS * days));

		return df.format(date);
	}

	public static boolean isValidShortDateFormat(String strDate) {
		if (strDate.length() != SHORT_FORMAT.length()) {
			return false;
		}

		try {
			Integer.parseInt(strDate); // ---- 避免日期中输入非数字 ----
		} catch (Exception NumberFormatException) {
			return false;
		}

		DateFormat df = getNewDateFormat(SHORT_FORMAT);

		try {
			df.parse(strDate);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	public static boolean isValidDateFormat(String strDate, String format){
		if (strDate.length() != format.length()) {
			return false;
		}
		try {
			Integer.parseInt(strDate); // ---- 避免日期中输入非数字 ----
		} catch (Exception NumberFormatException) {
			return false;
		}
		DateFormat df = getNewDateFormat(format);
		try {
			df.parse(strDate);
		} catch (ParseException e) {
			return false;
		}
		return true;
	}

	public static boolean isValidShortDateFormat(String strDate, String delimiter) {
		String temp = strDate.replaceAll(delimiter, "");

		return isValidShortDateFormat(temp);
	}

	/**
	 * 判断表示时间的字符是否为符合yyyyMMddHHmmss格式
	 *
	 * @param strDate
	 * @return
	 */
	public static boolean isValidLongDateFormat(String strDate) {
		if (strDate.length() != LONG_FORMAT.length()) {
			return false;
		}

		try {
			Long.parseLong(strDate); // ---- 避免日期中输入非数字 ----
		} catch (Exception NumberFormatException) {
			return false;
		}

		DateFormat df = getNewDateFormat(LONG_FORMAT);

		try {
			df.parse(strDate);
		} catch (ParseException e) {
			return false;
		}

		return true;
	}

	/**
	 * 判断表示时间的字符是否为符合yyyyMMddHHmmss格式
	 *
	 * @param strDate
	 * @param delimiter
	 * @return
	 */
	public static boolean isValidLongDateFormat(String strDate, String delimiter) {
		String temp = strDate.replaceAll(delimiter, "");

		return isValidLongDateFormat(temp);
	}

	public static String getShortDateString(String strDate) {
		return getShortDateString(strDate, "-|/");
	}

	public static String getShortDateString(String strDate, String delimiter) {
		if (StringUtils.isBlank(strDate)) {
			return null;
		}

		String temp = strDate.replaceAll(delimiter, "");

		if (isValidShortDateFormat(temp)) {
			return temp;
		}

		return null;
	}

	public static String getShortFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		Date dt = new Date();

		cal.setTime(dt);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = getNewDateFormat(SHORT_FORMAT);

		return df.format(cal.getTime());
	}

	public static String getWebTodayString() {
		DateFormat df = getNewDateFormat(WEB_FORMAT);

		return df.format(new Date());
	}

	public static String getWebFirstDayOfMonth() {
		Calendar cal = Calendar.getInstance();
		Date dt = new Date();

		cal.setTime(dt);
		cal.set(Calendar.DAY_OF_MONTH, 1);

		DateFormat df = getNewDateFormat(WEB_FORMAT);

		return df.format(cal.getTime());
	}

	public static String convert(String dateString, DateFormat formatIn, DateFormat formatOut) {
		try {
			Date date = formatIn.parse(dateString);

			return formatOut.format(date);
		} catch (ParseException e) {
			log.warn("convert() --- orign date error:{} ", dateString);
			return "";
		}
	}

	public static String convertWeb2ShortFormat(String dateString) {
		DateFormat df1 = getNewDateFormat(WEB_FORMAT);
		DateFormat df2 = getNewDateFormat(SHORT_FORMAT);

		return convert(dateString, df1, df2);
	}

	public static String convert2ChineseDtFormat(String dateString) {
		DateFormat df1 = getNewDateFormat(SHORT_FORMAT);
		DateFormat df2 = getNewDateFormat(CHINESE_DT_FORMAT);

		return convert(dateString, df1, df2);
	}

	public static String convertFromWebFormat(String dateString) {
		DateFormat df1 = getNewDateFormat(SHORT_FORMAT);
		DateFormat df2 = getNewDateFormat(WEB_FORMAT);

		return convert(dateString, df2, df1);
	}

	public static boolean webDateNotLessThan(String date1, String date2) {
		DateFormat df = getNewDateFormat(WEB_FORMAT);

		return dateNotLessThan(date1, date2, df);
	}

	/**
	 * @param date1
	 * @param date2
	 * @param
	 * @return
	 */
	public static boolean dateNotLessThan(String date1, String date2, DateFormat format) {
		try {
			Date d1 = format.parse(date1);
			Date d2 = format.parse(date2);
			return !d1.before(d2);
		} catch (ParseException e) {
			log.debug("dateNotLessThan() --- ParseException({}, {})", date1, date2);
			return false;
		}
	}

	public static String getEmailDate(Date today) {
		String todayStr;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");

		todayStr = sdf.format(today);
		return todayStr;
	}

	public static String getSmsDate(Date today) {
		String todayStr;
		SimpleDateFormat sdf = new SimpleDateFormat("MM月dd日HH:mm");

		todayStr = sdf.format(today);
		return todayStr;
	}

	public static String formatTimeRange(Date startDate, Date endDate, String format) {
		if ((endDate == null) || (startDate == null)) {
			return null;
		}

		String rt = null;
		long range = endDate.getTime() - startDate.getTime();
		long day = range / DateUtils.MILLIS_PER_DAY;
		long hour = (range % DateUtils.MILLIS_PER_DAY) / DateUtils.MILLIS_PER_HOUR;
		long minute = (range % DateUtils.MILLIS_PER_HOUR) / DateUtils.MILLIS_PER_MINUTE;

		if (range < 0) {
			day = 0;
			hour = 0;
			minute = 0;
		}

		rt = format.replaceAll("dd", String.valueOf(day));
		rt = rt.replaceAll("hh", String.valueOf(hour));
		rt = rt.replaceAll("mm", String.valueOf(minute));

		return rt;
	}

	public static String formatMonth(Date date) {
		if (date == null) {
			return null;
		}

		return new SimpleDateFormat(MONTH_FORMAT).format(date);
	}

	/**
	 * 获取系统日期的前一天日期，返回Date
	 *
	 * @return
	 */
	public static Date getBeforeDate() {
		Date date = new Date();

		return new Date(date.getTime() - (ONE_DAY_MILL_SECONDS));
	}

	/**
	 * 获得指定时间当天起点时间
	 *
	 * @param date
	 * @return
	 */
	public static Date getDayBegin(Date date) {
		DateFormat df = new SimpleDateFormat(SHORT_FORMAT);
		df.setLenient(false);
		String dateString = df.format(date);
		try {
			return df.parse(dateString);
		} catch (ParseException e) {
			return date;
		}
	}

	/**
	 * 判断参date上min分钟后，是否小于当前时间
	 *
	 * @param date
	 * @param min
	 * @return
	 */
	public static boolean dateLessThanNowAddMin(Date date, long min) {
		return addMinutes(date, min).before(new Date());

	}

	/**
	 * 是否在现在时间之前
	 *
	 * @param date
	 * @return
	 */
	public static boolean isBeforeNow(Date date) {
		if (date == null)
			return false;
		return date.compareTo(new Date()) < 0;
	}

	/**
	 * 是否有效
	 *
	 * @param requestTime 请求时间
	 * @param effectTime  生效时间
	 * @param expiredTime 失效时间
	 * @return
	 */
	public static boolean isValidate(Date requestTime, Date effectTime, Date expiredTime) {
		if (requestTime == null || effectTime == null || expiredTime == null) {
			return false;
		}
		return effectTime.compareTo(requestTime) <= 0 && expiredTime.compareTo(requestTime) >= 0;
	}

	public static Date parseNoSecondFormat(String sDate) throws ParseException {
		DateFormat dateFormat = new SimpleDateFormat(NO_SECOND_FORMAT);

		if ((sDate == null) || (sDate.length() < NO_SECOND_FORMAT.length())) {
			throw new ParseException(PARSE_MSG, 0);
		}

		if (!StringUtils.isNumeric(sDate)) {
			throw new ParseException("not all digit", 0);
		}

		return dateFormat.parse(sDate);
	}

	/**
	 * 判断两个日期是否是同一天
	 *
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static boolean isSameDay(Date date1, Date date2) {
		SimpleDateFormat sdf = new SimpleDateFormat(WEB_FORMAT);
		return sdf.format(date1).equals(sdf.format(date2));
	}

	/**
	 * 获取系统当前时间
	 *
	 * @return
	 */
	public static Date getCurrentTS() {
		Calendar calendar = Calendar.getInstance();
		return calendar.getTime();
	}

	/**
	 * 获取当前时间的前几天或者后几天的日期
	 *
	 * @param days
	 * @return
	 */
	public static Date getDateNearCurrent(int days) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DATE, days);
		return calendar.getTime();
	}

	/**
	 * 计算两个时间的差（相差的天、小时、分钟、秒）
	 *
	 * @param start
	 * @param end
	 * @param type  (可以是：day、hour、min、sec)
	 * @return
	 */
	public static Long getDiffSecondByTwoDate(Date start, Date end, String type) {
		long ns = 1000L;// 一秒钟的毫秒数
		long nd = ns * 24 * 60 * 60;// 一天的毫秒数
		long nh = ns * 60 * 60;// 一小时的毫秒数
		long nm = ns * 60;// 一分钟的毫秒数
		long diff;
		diff = end.getTime() - start.getTime();
		long day = diff / nd;// 计算差多少天
		long hour = diff % nd / nh;// 计算差多少小时
		long min = diff % nd % nh / nm;// 计算差多少分钟
		long sec = diff % nd % nh % nm / ns;// 计算差多少秒

		if (type.equals("day")) {
			return diff / day;
		}
		if (type.equals("hour")) {
			return diff / hour;
		}
		if (type.equals("min")) {
			return diff / min;
		}
		if (type.equals("sec")) {
			return diff / sec;
		} else {
			return null;
		}
	}

	/**
	 * 指定时间推移前(后)几天
	 *
	 * @param date   时间
	 * @param amount 几天
	 * @return
	 */
	public static Date computeWithDay(Date date, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_YEAR, amount);
		return cal.getTime();
	}

	/**
	 * 指定时间推移前(后)几月
	 *
	 * @param date   时间
	 * @param amount 几月
	 * @return
	 */
	public static Date computeWithMonth(Date date, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MONTH, amount);
		return cal.getTime();
	}

	/**
	 * 指定时间推移前(后)几年
	 *
	 * @param date   时间
	 * @param amount 几年
	 * @return
	 */
	public static Date computeWithYear(Date date, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.YEAR, amount);
		return cal.getTime();
	}

	/**
	 * 指定时间推移前(后)几小时
	 *
	 * @param date   时间
	 * @param amount 几小时
	 * @return
	 */
	public static Date computeWithHour(Date date, int amount) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR_OF_DAY, amount);
		return cal.getTime();
	}

	public static Date getCurrYearLastDate() {
		Calendar calendar = Calendar.getInstance();
		int currentYear = calendar.get(Calendar.YEAR);
		calendar.clear();
		calendar.set(Calendar.YEAR, currentYear);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		return calendar.getTime();
	}

	public static Timestamp getTimeStrFromDate(Timestamp time) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(WEB_FORMAT);
		String strTime = sdf.format(new Date(time.getTime()));
		Date date = sdf.parse(strTime);
		return new Timestamp(date.getTime());
	}

	public static String getTimeStr(Timestamp time) {
		SimpleDateFormat sdf = new SimpleDateFormat(WEB_FORMAT);
		return sdf.format(new Date(time.getTime()));
	}

	public static int getCurrentDayOfMonth() {
		return Calendar.getInstance().get(Calendar.DATE);
	}

	/**
	 * 获取某日期的年份
	 *
	 * @param date
	 * @return
	 */
	public static Integer getYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 获取某日期的月份
	 *
	 * @param date
	 * @return
	 */
	public static Integer getMonth(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取某日期的日数
	 *
	 * @param date
	 * @return
	 */
	public static Integer getDay(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DATE);//获取日
	}

	public static Date getAppointedMonthAndDayDate(Date sourceDate, int newMonth, int newDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sourceDate);
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		gc.set(Calendar.MONTH, newMonth - 1);
		gc.set(Calendar.DAY_OF_MONTH, newDay);
		return gc.getTime();
	}

	/**
	 * 获得指定日的日期
	 *
	 * @param sourceDate
	 * @param newDay
	 * @return
	 */
	public static Date getAppointedDayDate(Date sourceDate, int newDay) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(sourceDate);
		GregorianCalendar gc = new GregorianCalendar();
		gc.set(Calendar.YEAR, cal.get(Calendar.YEAR));
		gc.set(Calendar.MONTH, cal.get(Calendar.MONTH));
		gc.set(Calendar.DAY_OF_MONTH, newDay);
		return gc.getTime();
	}

	private static final int SECONDS_PER_MINUTE = 60;
	private static final int MINUTES_PER_HOUR = 60;
	private static final int HOURS_PER_DAY = 24;
	private static final int SECONDS_PER_DAY = (HOURS_PER_DAY * MINUTES_PER_HOUR * SECONDS_PER_MINUTE);
	/**
	 一天的毫秒数
	 **/
	private static final long DAY_MILLISECONDS = SECONDS_PER_DAY * 1000L;
	/**
	 转换方法
	 @parma numberString 要转换的浮点数
	 @parma format 要获得的格式 例如"hh:mm:ss"
	 **/
	public static Date toDate(double numberString) {
		int wholeDays = (int)Math.floor(numberString);
		int millisecondsInday = (int)((numberString - wholeDays) * DAY_MILLISECONDS + 0.5);
		Calendar calendar = new GregorianCalendar();
		setCalendar(calendar, wholeDays, millisecondsInday, false);
		return calendar.getTime();

	}
	private static void setCalendar(Calendar calendar, int wholeDays,
									int millisecondsInDay, boolean use1904windowing) {
		int startYear = 1900;
		int dayAdjust = -1; // Excel thinks 2/29/1900 is a valid date, which it isn't
		if (use1904windowing) {
			startYear = 1904;
			dayAdjust = 1; // 1904 date windowing uses 1/2/1904 as the first day
		}
		else if (wholeDays < 61) {
			// Date is prior to 3/1/1900, so adjust because Excel thinks 2/29/1900 exists
			// If Excel date == 2/29/1900, will become 3/1/1900 in Java representation
			dayAdjust = 0;
		}
		calendar.set(startYear,0, wholeDays + dayAdjust, 0, 0, 0);
		calendar.set(GregorianCalendar.MILLISECOND, millisecondsInDay);
	}

	/**
	 * 获取下一个月
	 * @return
	 */
	public static String getPerMonth() {
		SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, 1);
		return dft.format(calendar.getTime());
	}

	public static String getPerMonth(String currMonth) throws ParseException{
		SimpleDateFormat dft = new SimpleDateFormat("yyyyMM");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(dft.parse(currMonth));
		calendar.add(Calendar.MONTH, 1);
		return dft.format(calendar.getTime());
	}

	/**
	 * 获取指定年月的最后一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getLastDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR, year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int lastDay = cal.getActualMaximum(Calendar.DATE);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, lastDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(cal.getTime());
	}


	/**
	 * 获取指定年月的第一天
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getFirstDayOfMonth(int year, int month) {
		Calendar cal = Calendar.getInstance();
		//设置年份
		cal.set(Calendar.YEAR, year);
		//设置月份
		cal.set(Calendar.MONTH, month-1);
		//获取某月最大天数
		int firstDay = cal.getActualMinimum(Calendar.DATE);
		//设置日历中月份的最大天数
		cal.set(Calendar.DAY_OF_MONTH, firstDay);
		//格式化日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(cal.getTime());
	}
}
