package com.devin.app.store.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author Devin
 */
public class TimeUtils {

    public String changeDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");

        Date date = new Date(time);
        String format = sdf.format(date);
        return format;
    }

    public static String long2String(long time, String formatType) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(formatType);
            Date date = new Date(time);
            String format = sdf.format(date);
            return format;
        } catch (Exception e) {
        }
        return null;
    }

    public static Date string2Date(String strTime, String formatType)
            throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(formatType);
        return formatter.parse(strTime);
    }

    /**
     * strTime 和 formatType的格式要一致
     *
     * @param strTime
     * @param formatType
     * @return
     * @throws ParseException
     */
    public static long string2Long(String strTime, String formatType)
            throws ParseException {
        Date date = string2Date(strTime, formatType); // String类型转成date类型
        if (date == null) {
            return 0;
        } else {
            return date.getTime();
        }
    }

    /**
     * @param date
     * @param formatType
     * @return
     */
    public static String date2String(Date date, String formatType) {
        return new SimpleDateFormat(formatType).format(date);
    }

    /**
     * 获取周几
     *
     * @param d
     * @return
     */
    public static String getWeekDay(Date d) {
        String[] week = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int w = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return week[w];
    }

}
