package com.beessoft.dyyd.utils;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by wongxl on 16/5/4.
 */
public class DateUtil {

    @SuppressLint("SimpleDateFormat")
    public static long getTimeInMillisSinceEpoch(String d) {
        SimpleDateFormat sdf  = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        //                    long timeInMinutesSinceEpoch = timeInMillisSinceEpoch / (60 * 1000);
        return date.getTime();
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDate() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy年MM月dd日 HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String Date() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String forwardWeekDate() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
//		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//		cal.setTime(curDate);
        cal.add(Calendar.DATE, -7); // 向前一周；如果需要向后一周，用正数即可
        //    cal.add(java.util.Calendar.MONTH, -1); // 向前一月；如果需要向后一月，用正数即可
        time = formatter.format(cal.getTime());
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String behindWeekDate() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
//		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
//		cal.setTime(curDate);
        cal.add(Calendar.DATE, 7); // 向前一周；如果需要向后一周，用正数即可
        //    cal.add(java.util.Calendar.MONTH, -1); // 向前一月；如果需要向后一月，用正数即可
        time = formatter.format(cal.getTime());
        return time;
    }


    @SuppressLint("SimpleDateFormat")
    public static String monthFirstDay() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH,0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        time = formatter.format(cal.getTime());
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String monthLastDay() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        time = formatter.format(cal.getTime());
        return time;
    }


    @SuppressLint("SimpleDateFormat")
    public static String Time() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat(
                "HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String TimeNoSecond() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat(
                "HH:mm");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }


    @SuppressLint("SimpleDateFormat")
    public static String YearMonth() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateLoca() {
        String time = "";
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
        time = formatter.format(curDate);
        return time;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date String2Date(String datestring) {
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = format.parse(datestring);
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public static String queryDate(Date nowtime) {
        String datestyle = "yyyyMMdd";
        SimpleDateFormat format1 = new SimpleDateFormat(datestyle);
        String strnowtime = format1.format(nowtime);
        return strnowtime;
    }


    @SuppressLint("SimpleDateFormat")
    public static String queryDate(Date nowtime,String datestyle) {
        SimpleDateFormat format1 = new SimpleDateFormat(datestyle);
        String strnowtime = format1.format(nowtime);
        return strnowtime;
    }


    public static Calendar trimToDate(long time) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.setTimeInMillis(time);
        Calendar result = Calendar.getInstance();
        result.clear();
        result.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH),
                0, 0, 0);
        return result;
    }
}
