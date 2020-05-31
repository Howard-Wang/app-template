package com.company.app.utils;

import com.company.app.enums.TrimFieldEnum;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具类
 * @author YunJ
 */
public class DateTimeUtil {

    private final static String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    private final static String DATE = "yyyy-MM-dd";

    /**
     * 对时间进行裁剪
     */
    public static Date trimAfter(TrimFieldEnum field, Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        switch (field) {
            case HOUR:
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                break;
            case MINUTE:
                calendar.set(Calendar.MINUTE, 0);
                break;
            case SECOND:
                calendar.set(Calendar.SECOND, 0);
                break;
            case MIL_SECOND:
                calendar.set(Calendar.MILLISECOND, 0);
                break;
            default:
                break;
        }
        return calendar.getTime();
    }

    public static Date parseDate(String date) {
        return parse(date, DATE);
    }

    public static Date parse(String date, String dateStyle) {
        try {
            DateFormat format = new SimpleDateFormat(dateStyle);
            return format.parse(date);
        } catch (Exception e) {
            return null;
        }
    }
}
