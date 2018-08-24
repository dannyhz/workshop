package cn.evun.sweet.common.util;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

/**
 * 常用的日期时间操作工具<br/>
 * 如果需要获得更多的其他功能，可以使用appache的DateUtil
 * 
 * @author yangw
 * @since 1.0.0
 * 
 */
public class DateUtils {

	/** yyyy-MM */
	public static final int FORMAT_SHORDATE = 0;

	/** yyyy-MM-dd */
	public static final int FORMAT_DATE = 1;

	/** yyyy-MM-dd HH:mm:ss */
	public static final int FORMAT_DATETIME = 2;

	/** yyyy-MM-dd HH:mm:ss.sssz */
	public static final int FORMAT_DATETIMEMILLISECOND = 3;

	/** yyyy */
	public static final int FORMAT_YYMMDD = 4;

	/** yyyyMMdd */
	public static final int FORMAT_YYYYMMDD = 5;

	/** yyyy年MM月dd日 */
	public static final int FORMAT_DATESTRING = 6;

	/** 一天有多少毫秒 */
	public static final int ONE_DAY_MILLISECOND = 86400000;

	private final static String DATE_FORMAT = "yyyy-MM-dd";

	private final static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

	public static final int MSEC_TYPE= 5;//毫秒
	public static final int MINITE_TYPE = 0;
	public static final int HOUR_TYPE = 1;
	public static final int DAY_TYPE = 2;
	public static final int MONTH_TYPE = 3;
	public static final int YEAR_TYPE = 4;
	public static final String LONG_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String CHINA_MINITE_DATE_PATTERN = "yyyy年MM月dd日  HH:mm";

	public static final String MAIL_CHINA_MINITE_DATE_PATTERN = "yyyy年MM月dd日 HH:mm (EEE)";

	public static final String SHORT_DATE_PATTERN = "yyyy-MM-dd";

	public static final String CHINA_MONTH_DAY_DATE_PATTERN = "MM月dd日";

	public static final String CHINA_MONTH_DATE_PATTERN = "MM月dd日  HH:mm";

	public static final String HOUR_MINITE_PATTERN = "HH:mm";
	
	public static final String SECOND_PATTERN = "HH:mm:ss";

	public static final String MINITE_DATE_PATTERN = "yyyy-MM-dd HH:mm";
	
	/**
	 * 获得指定日期的年份
	 * 默认为：yyyy-MM-dd
	 */
	public static int getYear(String strDate) {
		return Integer.parseInt(strDate.substring(0, 4));
	}

	/**
	 * 获得指定日期的月份
	 * 默认为：yyyy-MM-dd
	 */
	public static int getMonth(String strDate) {
		return Integer.parseInt(strDate.substring(5, strDate.indexOf("-", 5)));
	}

	/**
	 * 获得指定日期在当月的天数
	 * 默认为：yyyy-MM-dd
	 */
	public static int getDay(String strDate) {
		strDate = strDate.substring(strDate.lastIndexOf("-") + 1);
		if (strDate.indexOf(" ") != -1) {
			strDate = strDate.substring(0, strDate.indexOf(" "));
		}
		return Integer.parseInt(strDate);
	}

	/**
	 * 判断是否为闰年
	 */
	private static boolean isLeap(int year) {
		if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 判断指定的日期是否为闰日
	 */
	public static boolean isLeapDay(int year, int month, int day) {
		if (month != 2)
			return false;
		else
			return isLeap(year) && day == 29 || !isLeap(year) && day == 28;
	}

	/**
	 * 得到当前年份每月的天数
	 */
	public static int getDays(int year, int month) {
		short[] days = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		if (isLeap(year) && month == 2)
			return 29;
		else
			return days[month - 1];
	}

	/**
	 * 得到当前的时间, 如 hh:mm:ss.sss
	 */
	public static String getTime() {
		return getCurrentTimeStamp().substring(getCurrentTimeStamp().indexOf(" ") + 1);
	}

	/**
	 * 得到大写年月日
	 * 
	 * @param "2005-05-20"
	 * return 2005 年 05 月 20 日
	 */
	public static String getPrintDate(String rpt_date) {
		String year = rpt_date.substring(0, 4);
		String month = rpt_date.substring(5, 7);
		String day = rpt_date.substring(8, 10);
		return year + " 年 " + month + " 月 " + day + " 日 ";
	}

	/**
	 * 得到今天的日期,yyyy-MM-dd 
	 */
	public static String getToday() {
		return getDateTimeString(System.currentTimeMillis(), FORMAT_DATE);
	}

	/**
	 * 得到当前的时间戳
	 * 
	 * @return yyyy-mm-dd hh:mm:ss.sss 格式的当前时间戳
	 */
	public static String getCurrentTimeStamp() {
		return getDateTimeString(System.currentTimeMillis(), FORMAT_DATETIMEMILLISECOND);
	}
	
	/**
	 * 按照日期格式fmt获取系统时间
	 */
	public static String getSystemTime(String fmt) {
		Calendar calendar = Calendar.getInstance();
		Date date = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat(fmt);
		return sdf.format(date);
	}

	/**
	 * 判断日期字符串是否是指定格式的字符串
	 * 默认要求日期格式为 yyyy-MM-dd
	 */
	public static boolean isDateTimeString(String strDate) {
		try {
			getFormatedDate(getDateFormat(), strDate);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	/**
	 * 判断一个字符串是否是日期
	 * 日期之间的分割符号为 "-"
	 * 
	 */
	public static boolean isDateTimeString2(String sDate) {
		String separator = "-";
		StringTokenizer token = new StringTokenizer(sDate, separator);
		String year = token.nextToken();
		String month = token.nextToken();
		String day = token.nextToken();

		if (day.indexOf(" ") != -1) {
			day = day.substring(0, day.indexOf(" "));
			boolean is_date = isDate(year, month, day);
			
			String time = day.substring(day.indexOf(" ") + 1);
			return is_date && isTime(time);
		} else {
			return isDate(year, month, day);
		}
	}

	/**
	 * 判断是否是合法日期
	 */
	private static boolean isDate(String year, String month, String day) {
		return isDate(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day));
	}
	
    /**
     * 判断是否闰年
     */
    private static boolean isLeapYear(int year){
        if(year % 4 != 0)
            return false;
        if((year % 100 == 0) && (year % 400 != 0))
            return false;
        return true;
    }

	/**
	 * 判断是否是合法日期
	 */
	private static boolean isDate(int year, int month, int day) {
		int[] day_of_months = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		try {
			if ((year + "").length() != 4)
				return false;
			if (isLeapYear(year))
				day_of_months[1] = 29;
			if (month < 1 || month > 12)
				return false;
			if (day < 1 || day > day_of_months[month - 1])
				return false;
			return true;
		} catch (Throwable e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 是否是合法的时间格式
	 */
	private static boolean isTime(String time) {
		if (time == null || time.equals(""))
			time = "00:00:00.000";
		String[] s = time.split(":");

		/*因为pattern中,是可以代表任意字符，需要转义*/
		String[] s1 = s[2].split("\\.");
		boolean ret = (Integer.parseInt(s[0]) < 24 && Integer.parseInt(s[1]) < 60);
		if (s1.length == 2) {
			ret = ret && Integer.parseInt(s1[0]) < 60
					&& Integer.parseInt(s1[1]) < 1000;
		} else if (s1.length == 0) {
			ret = ret && Integer.parseInt(s[2]) < 60;
		} else {
			ret = false;
		}
		return ret;
	}

	/**
	 * 解析时间戳
	 * 
	 * @param date_time_str 标准的时间格式
	 */
	public static long getMillisecond(String date_time_str) {
		if (date_time_str==null || date_time_str.indexOf(" ") == -1) {
			return getMillisecond(date_time_str, null);
		}
		String s[] = date_time_str.split(" ");
		return getMillisecond(s[0], s[1]);
	}

	/**
	 * 解析时间戳
	 * 
	 * @param date yyyy-m-d 或 yyyy-mm-dd 格式
	 * @param time hh:mm:ss.sss 格式
	 */
	private static long getMillisecond(String date, String time) {
		if (time == null || time.equals(""))
			time = "00:00:00.000";

		String[] s = date.split("-");
		String[] s2 = time.split(":");

		/*因为pattern中.是可以代表任意字符，需要转义*/
		String[] s3 = s2[2].split("\\.");
		Calendar cl = Calendar.getInstance();
		cl.set(Calendar.YEAR, Integer.parseInt(s[0]));
		cl.set(Calendar.MONTH, Integer.parseInt(s[1]) - 1);
		cl.set(Calendar.DAY_OF_MONTH, Integer.parseInt(s[2]));
		cl.set(Calendar.HOUR_OF_DAY, Integer.parseInt(s2[0]));
		cl.set(Calendar.MINUTE, Integer.parseInt(s2[1]));
		if (s3.length > 1) {
			cl.set(Calendar.SECOND, Integer.parseInt(s3[0]));
			cl.set(Calendar.MILLISECOND, Integer.parseInt(s3[1]));
		} else {
			cl.set(Calendar.SECOND, Integer.parseInt(s2[2]));
			cl.set(Calendar.MILLISECOND, 0);
		}
		return cl.getTimeInMillis();
	}

	/**
	 * 将毫秒数字转换为日期
	 * 
	 * @param mill 毫秒数
	 * @return yyyy/mm/dd 格式的日期字符串,mill是小于0时返回""
	 */
	public static String getDateString(long mill) {
		return getDateTimeString(mill, FORMAT_DATE);
	}
	
	public static String getDateTimeString(long mill) {
		return getDateTimeString(mill, FORMAT_DATETIME);
	}

	/**
	 * 得到YYMMDD格式的日期
	 */
	public static String getYYMMDD(String date_time_str) {
		return formatDateTimeString(date_time_str, FORMAT_YYMMDD);
	}

	/**
	 * 得到YYYYMMDD格式的日期
	 */
	public static String getYYYYMMDD(String date_time_str) {
		return formatDateTimeString(date_time_str, FORMAT_YYYYMMDD);
	}

	/**
	 * 得到下个月的这一天(每月只支持到28日!!) 如:输入 2006-12-01 输出 2007-01-01 输入 2004-01-28 输出
	 * 2004-02-28
	 */
	public static String getNextMonthDay(String bizDate) {
		int month = getMonth(bizDate) + 1;
		int year = getYear(bizDate);
		if (month > 12) {
			month = 1;
			year++;
		}
		String monthStr = "";
		if (month >= 10)
			monthStr = String.valueOf(month);
		else
			monthStr = "0" + String.valueOf(month);
		String day = bizDate.substring(8);
		String dateStr = String.valueOf(year) + "-" + monthStr + "-" + day;
		return dateStr;
	}

	/**
	 * 得到上个月的这一天(每月只支持到28日!!) 。
	 * 如:输入 2006-01-01 输出 2005-01-01 输入 2004-02-28 输出 2004-01-28
	 */
	public static String getUpMonthDay(String bizDate) {
		int month = getMonth(bizDate) - 1;
		int year = getYear(bizDate);
		if (month < 1) {
			month = 12;
			year--;
		}
		String monthStr = "";
		if (month >= 10)
			monthStr = String.valueOf(month);
		else
			monthStr = "0" + String.valueOf(month);
		String day = bizDate.substring(8);
		String dateStr = String.valueOf(year) + "-" + monthStr + "-" + day;
		return dateStr;
	}

	/**
	 * 得到YYYY年MM月DD日格式的日期
	 */
	public static String getFormatDateString(String date_time_str) {
		return formatDateTimeString(date_time_str, FORMAT_DATESTRING);
	}

	/**
	 * 将输入的时间日期串格式化为标准格式
	 */
	public static String formatDateTimeString(String date_time_str, int format) {
		return getDateTimeString(getMillisecond(date_time_str), format);
	}

	/**
	 * 将时间戳转换为日期字符串
	 * 
	 * @param mill 时间戳
	 * @param format 格式
	 * @return 返回格式化的日期字符串, mill是小于0时返回""
	 */
	public static String getDateTimeString(long mill, int format) {
		if (mill < 0)
			return "";
		Calendar cl = Calendar.getInstance();
		cl.setTimeInMillis(mill);
		int year = cl.get(Calendar.YEAR);
		int month = cl.get(Calendar.MONTH) + 1;
		int day = cl.get(Calendar.DAY_OF_MONTH);
		int hour = cl.get(Calendar.HOUR_OF_DAY);
		int mm = cl.get(Calendar.MINUTE);
		int ss = cl.get(Calendar.SECOND);
		int ms = cl.get(Calendar.MILLISECOND);
		String ret = "";
		switch (format) {
		case FORMAT_SHORDATE:
			ret = year + "-" + (month < 10 ? "0" + month : "" + month);
			break;
		case FORMAT_DATE:
			ret = year + "-" + (month < 10 ? "0" + month : "" + month) + "-"
					+ (day < 10 ? "0" + day : "" + day);
			break;
		case FORMAT_DATETIME:
			ret = year + "-" + (month < 10 ? "0" + month : "" + month) + "-"
					+ (day < 10 ? "0" + day : "" + day) + " "
					+ (hour < 10 ? "0" + hour : "" + hour) + ":"
					+ (mm < 10 ? "0" + mm : "" + mm) + ":"
					+ (ss < 10 ? "0" + ss : "" + ss);
			break;
		case FORMAT_DATETIMEMILLISECOND:
			String sMs;
			if (ms < 10) {
				sMs = "00" + ms;
			} else if (ms < 100) {
				sMs = "0" + ms;
			} else {
				sMs = "" + ms;
			}
			sMs = "." + sMs;
			ret = year + "-" + (month < 10 ? "0" + month : "" + month) + "-"
					+ (day < 10 ? "0" + day : "" + day) + " "
					+ (hour < 10 ? "0" + hour : "" + hour) + ":"
					+ (mm < 10 ? "0" + mm : "" + mm) + ":"
					+ (ss < 10 ? "0" + ss : "" + ss) + sMs;
			break;
		case FORMAT_YYMMDD:
			ret = (year + "").substring(2)
					+ (month < 10 ? "0" + month : "" + month)
					+ (day < 10 ? "0" + day : "" + day);
			break;
		case FORMAT_YYYYMMDD:
			ret = (year + "") + (month < 10 ? "0" + month : "" + month)
					+ (day < 10 ? "0" + day : "" + day);
			break;
		case FORMAT_DATESTRING:
			ret = (year + "") + "年" + (month < 10 ? "0" + month : "" + month)
					+ "月" + (day < 10 ? "0" + day : "" + day) + "日";
			break;
		}
		return ret;
	}

	/**
	 * 判断两个日期yyyy-mm-dd之间相差多少天（只按照日期计算，时间忽略) 算头不算尾，日期的直接相减
	 */
	public static int substract(String biger_date_time, String smaller_date_time) {
		return (int) ((getMillisecond(formatDateTimeString(biger_date_time,
				FORMAT_DATE)) - getMillisecond(formatDateTimeString(
				smaller_date_time, FORMAT_DATE))) / ONE_DAY_MILLISECOND);
	}

	/**
	 * 判断两个日期yyyy-mm-dd之间相差多少天（只按照日期计算，时间忽略) 算头又算尾，日期的直接相减
	 */
	public static int substract3(String biger_date_time, String smaller_date_time) {
		return (int) ((getMillisecond(formatDateTimeString(biger_date_time,
				FORMAT_DATE)) - getMillisecond(formatDateTimeString(
				smaller_date_time, FORMAT_DATE))) / ONE_DAY_MILLISECOND) + 1;
	}

	public static int[] substractForDeposit(String biger_date_time,
			String smaller_date_time) {
		int[] result = substract1(biger_date_time, smaller_date_time);
		result[1] = result[1] + 1;
		return result;
	}

	/**
	 * 用途：两个日期相减 ，得到整月月数和零头的天数,算头算尾
	 * 规则：以自然月为准，对年对月对日
	 * 例如：1月31-2月28
	 * 如果是平年，则为1个月 1月31-3月30 为2个月 1月31-3月31 为2个月零1天
	 * 说明：该方法不对输入参数的正确性进行校验，调用时应保证参数是正确的日期格式
	 * 
	 * @param biger_date_time 包含当天，即算头算尾。（3月2日-4月1日为1个月）
	 * @return 整月月数，零头的天数
	 */
	public static int[] substract1(String biger_date_time, String smaller_date_time) {
		int year_biger = getYear(biger_date_time);
		int month_biger = getMonth(biger_date_time);
		int day_biger = getDay(biger_date_time);

		int year_smaller = getYear(smaller_date_time);
		int month_smaller = getMonth(smaller_date_time);
		int day_smaller = getDay(smaller_date_time);

		/*得到年数*/
		int retYear = 0;
		/*如果结束日的月份大于起始日的月份*/
		if (month_biger - month_smaller > 0) {
			retYear = year_biger - year_smaller;
		} else if (month_biger - month_smaller < 0) {
			retYear = year_biger - year_smaller - 1;
		} else {
			/*如果二个日期的月份相同，则如果结束日的日期加1大于起始日的日期,说明超过一年（如3月3日-3月2日）*/
			if(day_biger - day_smaller > -1) {
				retYear = year_biger - year_smaller;
			} else {
				retYear = year_biger - year_smaller - 1;
			}
		}

		/*得到结束日对应月份的最后一天*/
		int bigerLastDay = getDayOfMonth(year_biger, month_biger);
		int retMonth = 0;
		int retDay = 0;
		/*如果起始日为1号,并且如果结束日为月未最后一天*/
		if (day_smaller == 1 && day_biger == bigerLastDay) {
			retMonth = (year_biger - year_smaller) * 12 + month_biger
					- month_smaller + 1;
			return new int[] {retYear ,retMonth, retDay};
		}

		/*得到起始日对应日期的前一天*/
		int tempSmallerDay = day_smaller - 1;
		/*如果起始日的前一天早于结束日对应月份的最后一天，则也视为1个月，如1月30-2月28日*/
		if (bigerLastDay - tempSmallerDay <= 0) {
			retMonth = (year_biger - year_smaller) * 12 + month_biger
					- month_smaller;
		}
		/*如果结束日晚于起始日，则说明必然大于一个月*/
		else if (day_biger - tempSmallerDay >= 0) {
			retMonth = (year_biger - year_smaller) * 12 + month_biger
					- month_smaller;
			retDay = day_biger - tempSmallerDay;
		}
		/*反之，则小于一个月*/
		else {
			retMonth = (year_biger - year_smaller) * 12 + month_biger
					- month_smaller - 1;
			retDay = getDayOfMonth(year_smaller, month_smaller)
					- tempSmallerDay + day_biger;
		}
		return new int[] {retYear,retMonth, retDay };
	}

	/**
	 * 两个日期相减 ，得到相差的带小数的月数
	 */
	public static BigDecimal substract2(String biger_date_time, String smaller_date_time) {
		int[] termArr = substract1(biger_date_time, smaller_date_time);
		BigDecimal mth = new BigDecimal(termArr[0]).add(new BigDecimal(
				termArr[1]).divide(new BigDecimal(30), 1, BigDecimal.ROUND_HALF_UP));
		return mth;
	}

	/**
	 * 加上几天后的日期时间串
	 */
	public static String add(String date_time_str, int days, int format) {
		if (format == FORMAT_DATE)
			return add(date_time_str, days);
		else
			return getDateTimeString(getMillisecond(date_time_str) + days * ONE_DAY_MILLISECOND, format);
	}

	/**
	 * 某个日期加上指定天数的日期。
	 * 默认要求日期格式为 yyyy-MM-dd 
	 * 例如： add("2005-1-20",3)="2005-01-23" add("2005-1-30",3)="2005-02-02"
	 */
	public static String add(String strDate, int days) {
		DateFormat df = getDateFormat();
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + days);
		return df.format(c.getTime());
	}

	/**
	 * 某个日期加上指定天数的日期。
	 * 例如： add("2005-1-20",3)="2005-01-23" add("2005-1-30",3)="2005-02-02”
	 */
	public static String add(String strDate, int days, String format) {
		DateFormat df = getDateFormat(format);
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) + days);
		return df.format(c.getTime());
	}

	/**
	 * 得到两个日期相差的天数。
	 * 只按照日期计算，时间忽略，算头不算尾。
	 * 默认要求日期格式为 yyyy-MM-dd
	 * 
	 * @param strEndDate
	 *            较大的日期
	 * @param strStartDate
	 *            较小的日期
	 * @return 较大的日期与较小的日期相差的天数
	 */
	public static int subtract(String strEndDate, String strStartDate) {
		return subtract(strEndDate, strStartDate, DATE_FORMAT);
	}

	/**
	 * 得到两个日期相差的天数。
	 * 只按照日期计算，时间忽略，算头不算尾。
	 * 
	 * @param strEndDate 较大的日期，要求与给定的日期格式一致
	 * @param strStartDate 较小的日期，要求与给定的日期格式一致
	 * @param format 日期格式
	 * @return 较大的日期与较小的日期相差的天数
	 */
	public static int subtract(String strEndDate, String strStartDate,
			String format) {
		DateFormat df = getDateFormat(format);
		Date endDate = getFormatedDate(df, strEndDate);
		Date startDate = getFormatedDate(df, strStartDate);
		long msEndDate = endDate.getTime();
		long msStartDate = startDate.getTime();
		return (int) ((msEndDate - msStartDate) / ONE_DAY_MILLISECOND);
	}

	/**
	 * 某个日期加上指定月数的日期。
	 * 默认要求日期格式为 yyyy-MM-dd
	 * 例如： addMonth("2005-1-20",1)="2005-02-20" 
	 * addMonth("2005-1-30",1)="2005-03-02"
	 */
	public static String addMonth(String strDate, int months) {
		DateFormat df = getDateFormat();
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + months);
		return df.format(c.getTime());
	}

	public static String addMonth(Date d, int months) {
		DateFormat df = getDateFormat();
		return addMonth(df.format(d), months);
	}

	/**
	 * 某个日期加上指定年数的日期。
	 * 默认要求日期格式为 <b>yyyy-MM-dd </b>
	 * 例如： addYear("2005-1-20",1)="2006-01-20"
	 * addMonth("2005-1-30",1)="2006-01-30"
	 */
	public static String addYear(String strDate, int years) {
		DateFormat df = getDateFormat();
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.YEAR, c.get(Calendar.YEAR) + years);
		return df.format(c.getTime());
	}

	public static String addYear(Date d, int years) {
		DateFormat df = getDateFormat();
		return addYear(df.format(d), years);
	}

	/**
	 * 某个日期加上指定月数的日期。
	 * 例如： addMonth("2005-1-20",1)="2005-02-20"
	 * addMonth("2005-1-30",1)="2005-03-02"
	 * @return
	 */
	public static String addMonth(String strDate, int months, String format) {
		DateFormat df = getDateFormat(format);
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.MONTH, c.get(Calendar.MONTH) + months);
		return df.format(c.getTime());
	}

	/**
	 * 返回两个日期中较早的那个日期。
	 * 默认要求日期格式为 yyyy-MM-dd
	 * 例如： min("2005-1-20","2005-1-19")="2005-1-19"
	 */
	public static String min(String firstDate, String secondDate) {
		int days = subtract(firstDate, secondDate);
		if (days > 0) {
			return secondDate;
		} else {
			return firstDate;
		}
	}

	/**
	 * 将指定格式的业务日期按照默认的格式 yyyy-MM-dd 返回。
	 * 例如： formatDate("2005-1-20")="2005-01-20
	 */
	public static String formatDate(String strDate) {
		return getDateFormat().format(getFormatedDate(strDate));
	}

	public static String formatDate(Date d) {
		return getDateFormat().format(d);
	}
	
	public static String formatDate(Date d,String strDate) {
		return getDateFormat(strDate).format(d);
	}

	/**
	 * 将指定格式df1的业务日期按照格式df2返回。
	 * 
	 * @param strDate
	 * @param df1 当前业务日期的格式
	 * @param df2 改变后的业务日期格式
	 */
	public static String formatDate(String strDate, DateFormat df1, DateFormat df2) {
		return df2.format(getFormatedDate(df1, strDate));
	}

	/**
	 * 将指定格式df1的业务日期按照格式df2返回。
	 * 
	 * @param strDate
	 * @param formatFrom
	 *            当前业务日期的格式
	 * @param formatTo
	 *            改变后的业务日期格式
	 * @return
	 */
	public static String formatDate(String strDate, String formatFrom,
			String formatTo) {
		DateFormat df1 = getDateFormat(formatFrom);
		DateFormat df2 = getDateFormat(formatTo);
		return df2.format(getFormatedDate(df1, strDate));
	}

	/**
	 * 判断指定业务日期格式是否是 yyy-MM-dd
	 * 
	 * @param strDate
	 * @return 如果是返回true，否则返回false
	 */
	public static boolean isFormatedDate(String strDate) {
		try {
			getFormatedDate(strDate);
		} catch (Exception ex) {
			return false;
		}
		return true;
	}

	/**
	 * 判断指定业务日期格式是否是合法业务日期。 包括判断格式是否是默认的yyyy-MM-dd，及其合法性。如2005-02-30是不合法的。
	 * 
	 * @param strDate
	 * @return 如果是返回true，否则返回false
	 */
	public static boolean isValidDate(String strDate) {
		if (!isFormatedDate(strDate)) {
			return false;
		}
		return isDate(getYear(strDate), getMonth(strDate), getDay(strDate));
	}

	/**
	 * 获得系统当前的日期时间。就是将System.currentTimeMillis()用yyyy-MM-dd hh:mm:ss
	 * 格式显示，小时按12小时计。
	 */
	public static String getCurrentDateTime() {
		long millis = System.currentTimeMillis();
		return getDateTimeFormat().format(new Date(millis));
	}

	/**
	 * 获得系统当前的日期时间。就是将System.currentTimeMillis()用yyyy-MM-dd hh:mm:ss
	 * 格式显示，小时按24小时计。
	 */
	public static String getCurrentDateTime24() {
		return getDateTimeString(System.currentTimeMillis(), FORMAT_DATETIME);
	}

	private static DateFormat getDateFormat() {
		return getDateFormat(DATE_FORMAT);
	}

	private static DateFormat getDateTimeFormat() {
		return getDateFormat(DATE_TIME_FORMAT);
	}

	private static DateFormat getDateFormat(String format) {
		return new SimpleDateFormat(format);
	}
	
	public static String getDateString(String format){
		if(format == null){
			format = "yyyy-MM-dd HH:mm";
		}
		return getDateFormat(format).format(new Date());
	}

	public static Date getFormatedDate(DateFormat df, String strDate) {
		try {
			return df.parse(strDate);
		} catch (Exception ex) {
			throw new RuntimeException("日期格式不对，无法解析。", ex);
		}
	}

	/**
	 * 将字符串表示的日期转换为Date类型。
	 * 默认要求日期格式为 yyyy-MM-dd
	 */
	public static Date getFormatedDate(String strDate) {
		return getFormatedDate(getDateFormat(), strDate);
	}

	public static Date getFormatedDateTime(String strDate) {
		return getFormatedDate(new SimpleDateFormat(DATE_TIME_FORMAT), strDate);
	}

	public static Date getFormatedDate(String strDate, String formate) {
		return getFormatedDate(getDateFormat(formate), strDate);
	}

	/**
	 * 转换为格林尼治格式
	 * @param strDate
	 * @param formate
	 * @return
	 */
	public static Date getFormatedIsoDate(String strDate, String formate) {
		Date date = getFormatedDate(getDateFormat(formate), strDate);
		Calendar ca = Calendar.getInstance();
		ca.setTime(date);
		ca.add(Calendar.HOUR_OF_DAY, 8);
		return ca.getTime();
	}

	/**
	 * 获取某个月份的天数
	 */
	public static int getDayOfMonth(int year, int month) {
		int[] day_of_months = { 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
		try {
			if (isLeapYear(year))
				day_of_months[1] = 29;
			return day_of_months[month - 1];
		} catch (Throwable e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 断某天是不是在一个时间区间内，含端点时间
	 */
	public static boolean isInDatePeriod(String someDate, String begin_date,
			String end_date) {
		return (substract(someDate, begin_date) >= 0)
				&& (substract(someDate, end_date) <= 0);
	}

	/**
	 * 为日期的不足位补零。 如“2005-2-5”的字符串改为“2005-02-05”的类型，忽略空格
	 * 
	 * @throws ParseException 如果字符串不是“2005-2-5”的格式，则抛出该异常
	 */
	public static String addZero(String srcDate) throws ParseException {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(df.parse(srcDate));
	}

	/**
	 */
	public static String getDateStr(String str) {
		if (str == null || str.trim().equals("")) {
			return "";
		}
		str = str.replaceAll("年", "-");
		str = str.replaceAll("月", "-");
		str = str.replaceAll("日", "");
		str = formatDate(str);
		return str;
	}

	/**
	 * 根据传入的字符串日期入2006-04-17返回当前所处的星期
	 * 
	 * @return 周一是1 一次类推
	 */
	public static int getWeekByDate(String datestr) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(getYear(datestr), getMonth(datestr) - 1, getDay(datestr) - 1);
		return calendar.get(Calendar.DAY_OF_WEEK);

	}

	/**
	 * 获得某月的月尾 如输入“2005-12”，输出为"2005-12-31"
	 */
	public static String getEndOfMonth(String month) {
		String strDate = month + "-01";
		String nextMonth = addMonth(strDate, 1);
		return add(nextMonth, -1);
	}

	/**
	 * 获得某月的月初 如输入“2005-12”，输出为"2005-12-01"
	 */
	public static String getBeginOfMonth(String month, String year) {
		if (month.length() == 1)
			month = "0" + month;
		String strDate = year + "-" + month + "-01";
		return strDate;
	}

	/**
	 * 获得某年某月的月初
	 */
	public static String getEndOfMonth(String month, String year) {
		return getEndOfMonth(year + "-" + month);
	}

	/**
	 * 取上月最后一天
	 */
	public static String getUpMonthLastDay(String date) {
		date = getUpMonthDay(date);// 取得上月同期
		int year = Integer.parseInt(date.substring(0, 4));
		int month = Integer.parseInt(date.substring(5, 7));
		int day = getDayOfMonth(year, month);
		if (month < 10) {
			date = year + "-0" + month + "-" + day;
		} else {
			date = year + "-" + month + "-" + day;
		}
		return date;
	}

	/**
	 * 某个日期减去指定天数的日期。
	 * 默认要求日期格式为 yyyy-MM-dd
	 * 例如： add("2005-1-20",3)="2005-01-17" add("2005-1-30",3)="2005-01-27"
	 */
	public static String sub(String strDate, int days) {
		DateFormat df = getDateFormat();
		Date d = getFormatedDate(df, strDate);
		Calendar c = new GregorianCalendar();
		c.setTime(d);
		c.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH) - days);
		return df.format(c.getTime());
	}
	
	public static int getDiffDays(Date date1,Date date2)  
    {  
        Calendar cal = Calendar.getInstance();  
        cal.setTime(date1);  
        long time1 = cal.getTimeInMillis();               
        cal.setTime(date2);  
        long time2 = cal.getTimeInMillis();       
        long between_days=(time2-time1)/(1000*3600*24);  
          
       return Integer.parseInt(String.valueOf(between_days));         
    } 
	
	public static Calendar parseDateTime(Date d) {

		Calendar cal = Calendar.getInstance();
		int yy = 0, mm = 0, dd = 0, hh = 0, mi = 0, ss = 0;
		cal.setTime(d);

		yy = cal.get(Calendar.YEAR);
		mm = cal.get(Calendar.MONTH);
		dd = cal.get(Calendar.DAY_OF_MONTH);
		hh = cal.get(Calendar.HOUR_OF_DAY);
		mi = cal.get(Calendar.MINUTE);
		ss = cal.get(Calendar.SECOND);

		cal.set(yy, mm, dd, hh, mi, ss);
		return cal;
	}
	
	public static double dateCal3(Date startdate, Date enddate, int iType) {
		Calendar calBegin = parseDateTime(startdate);
		Calendar calEnd = parseDateTime(enddate);
		long lBegin = calBegin.getTimeInMillis();
		long lEnd = calEnd.getTimeInMillis();
		long ss = (long) ((lEnd - lBegin) / 1000L);
		if (iType == MINITE_TYPE)
			return (double)ss/(60);
		if (iType == HOUR_TYPE)
			return (double)ss/(60*60);
		if (iType == DAY_TYPE)
			return (double)ss/(24*60*60);
		else
			return -1;
	}
	
	 /**
     * 
     * 
     * <p>     
     * Description: 
     * </p>  
     * @author  ljh
     * @created 2015-5-21 下午2:49:11
     * @since   v1.3.1 
     * @param args
     * @return  void
     */
    /**
	 * 处理日期
	 * 如果是 周末 或者工作日的早上8:30以前，则返回当前日期减去相应天数(1到3天)之后的17:00
	 * 如果是 工作日 并且在工作时间8:30 ~17:00范围内 则直接返回时间，
	 * 如果在范围外17:30以后 则返回当天的17:00
	 */


    //待减去的天数
  	private static String[][] weekday_gb = {{"星期日","1"},{"星期一","3"},{"星期二","1"},
  		{"星期三","1"},{"星期四","1"},{"星期五","3"},{"星期六","2"}}; 
	public static Date dealWorkTime(Date paramDate) throws Exception{   
  		Calendar paramCal = Calendar.getInstance();     
  		paramCal.setTime(paramDate); 
  		Date returnDate = paramDate;
  		if(paramDate!=null){
  			//将工作开始时间设定为日期当天的8:00
  			Calendar workBegin= Calendar.getInstance();
  			workBegin.set(Calendar.YEAR,paramCal.get(Calendar.YEAR));
  			workBegin.set(Calendar.MONTH, paramCal.get(Calendar.MONTH));
  			workBegin.set(Calendar.DATE, paramCal.get(Calendar.DATE));
  			workBegin.set(Calendar.HOUR_OF_DAY, 8);
  			workBegin.set(Calendar.MINUTE, 00);
  			workBegin.set(Calendar.SECOND, 0);
  			workBegin.set(Calendar.MILLISECOND, 0);
  		    Calendar workEnd= Calendar.getInstance();
  		    workEnd.set(Calendar.YEAR,paramCal.get(Calendar.YEAR));
  		    workEnd.set(Calendar.MONTH, paramCal.get(Calendar.MONTH));
  		    workEnd.set(Calendar.DATE, paramCal.get(Calendar.DATE));
  		    workEnd.set(Calendar.HOUR_OF_DAY, 17);
  		    workEnd.set(Calendar.MINUTE, 30);
  		    workEnd.set(Calendar.SECOND, 0);
  		    workEnd.set(Calendar.MILLISECOND, 0);
  		    
  		   if(paramCal.get(Calendar.DAY_OF_WEEK)==Calendar.SATURDAY ||  
  			paramCal.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY
  			){    
  			   //如果是 周末 ，则返回下周1的上班时间
  				Integer dday=0;
  				try{
  					dday = Integer.valueOf((weekday_gb[paramCal.get(Calendar.DAY_OF_WEEK) -1][1]));
  				}catch (NumberFormatException e){
  		//			log.error("dday numberFormat error", e);
  				}
  				paramCal.add(Calendar.DAY_OF_MONTH, dday);
  				paramCal.set(Calendar.HOUR_OF_DAY, 8);
  				paramCal.set(Calendar.MINUTE, 00);
  				paramCal.set(Calendar.SECOND, 0);
  				paramCal.set(Calendar.MILLISECOND, 0);
  				returnDate = paramCal.getTime();																
  			}else{
  				if(paramCal.compareTo(workEnd)<=0&&paramCal.compareTo(workBegin)>=0){
  					//工作时间8:00 ~17:30范围内 ，
  					Calendar workBegin1 = workBegin ;
  					workBegin1.add(Calendar.MINUTE, 30);
  					//8点到8点半之间
  					if(paramCal.compareTo(workBegin1)<0 && paramCal.compareTo(workBegin)>0){
  						returnDate = workEnd.getTime() ;
  					}else {
  					    returnDate = paramCal.getTime(); 
  					}
  				}else if(paramCal.compareTo(workEnd)>0){
  					//如果是周5下午5点半之后，就返回下周1的上班时间
  					if(paramCal.get(Calendar.DAY_OF_WEEK)==Calendar.FRIDAY){
  						workEnd.add(Calendar.DATE, 3);
  						returnDate = workBegin.getTime();
  					} else {
  					//如果是周1-4  则返回 第二天的上班时间
  						workBegin.add(Calendar.DATE, 1);
  						returnDate = workBegin.getTime();
  					}
  					
  				}else if(paramCal.compareTo(workBegin)<0){
  					//如果是在8点之前则返回当天的17:30
  					returnDate = workEnd.getTime();
  				}
  		  }
  		}
  		return returnDate;
  	}
	
	/**
	 * 获取最后提醒时间（ 根据传入的提醒天数与 日期）
	 * <p>     
	 * Description: 
	 * </p>  
	 * @author  ljh
	 * @created 2015-5-21 下午3:01:15
	 * @since   v1.3.1 
	 * @param args
	 * @return  void
	 */
	public static Date getNoticeActuralTime(Date paramDate ,Integer days){
		//paramDate 类型转换 Date=>Calendar
		Calendar paramClender=Calendar.getInstance();
		paramClender.setTime(paramDate);
//		paramClender.add(Calendar.HOUR,0);
		paramClender.add(Calendar.DATE, days);
		return paramClender.getTime();
	}
	
	/**
	 * 比较d1和d2日期的大小(天)
	 * @param d1
	 * @param d2
	 * @return 1 d1 大于 d2 -1 d1小于d2 0相等
	 */
	public static int compareDay(Date d1, Date d2){
		d1 = DateUtils.getFormatedDate(DateUtils.formatDate(d1));
		d2 = DateUtils.getFormatedDate(DateUtils.formatDate(d2));
		long diff = d1.getTime() - d2.getTime();
		if(diff == 0){
			return 0;
		} else if(diff > 0){
			return 1;
		} else{
			return -1;
		}
	}
	
	/**
	 * d1-d2的日期差(比较时分秒)
	 * @param d1
	 * @param d2
	 * @return 
	 */
	public static int diffOfTwo(Date d1, Date d2){
		long diff = d1.getTime() - d2.getTime();
		return (int)(diff / (1000 * 3600 * 24));
	}
	
	/**
	 * d1-d2的日期差(只比较日期)
	 * @param d1
	 * @param d2
	 * @return
	 */
	public static int daysOfTwo(Date d1, Date d2){
		d1 = DateUtils.getFormatedDate(DateUtils.formatDate(d1));
		d2 = DateUtils.getFormatedDate(DateUtils.formatDate(d2));
		return diffOfTwo(d1, d2);
	}
	
	
	/**
	 * 把传入的日期对象，转换成指定格式的日期字符串
	 * 
	 * @param date
	 *            日期对象
	 * @param pattern
	 *            指定转换格式
	 * @return String 格式化后的日期字符串
	 */
	public static final String getDate(Date date, String pattern) {
		SimpleDateFormat df = null;
		String returnValue = "";
		if (date != null) {
			df = new SimpleDateFormat(pattern);
			returnValue = df.format(date);
		}
		return (returnValue);
	}

	/**
	 * 把传入的日期字符串，转换成指定格式的日期对象
	 * 
	 * @param dateString
	 *            日期字符串
	 * @param pattern
	 *            指定转换格式
	 * @return Date 日期对象
	 */
	public static Date getDate(String dateString, String pattern)
			throws Exception {
		SimpleDateFormat df = null;
		Date date = null;
		if (dateString != null) {
			df = new SimpleDateFormat(pattern);
			date = df.parse(dateString);
		}
		return date;
	}
	
	/**
	 * 当前日期加减n天
	 * @param date
	 * @param day
	 * @return
	 * @date 2017年2月14日 下午3:44:54
	 */
	public static Date addDateDay(Date date, int day) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DAY_OF_MONTH,day);
		return c.getTime();
	}
	
	/**
	 * 当前日期加减n月
	 * @param date
	 * @param month
	 * @return
	 * @date 2017年2月14日 下午3:44:57
	 */
	public static Date addDateMonth(Date date, int month) {
		java.util.Calendar c = java.util.Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, month);
		return c.getTime();
	}
	
}
