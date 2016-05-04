package com.beessoft.dyyd.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wongxl on 16/5/4.
 */
public class DateUtil {

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
}
