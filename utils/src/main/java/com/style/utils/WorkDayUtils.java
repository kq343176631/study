package com.style.utils;

import java.util.Calendar;

/**
 * 工作日计算工具类
 */
public class WorkDayUtils {

    private static final String dayNames[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    /**
     * 获取日期之间的天数
     */
    public static int getDaysBetween(Calendar start, Calendar end) {
        if (start.after(end)) {
            Calendar swap = start;
            start = end;
            end = swap;
        }
        int days = end.get(Calendar.DAY_OF_YEAR)
                - start.get(Calendar.DAY_OF_YEAR);
        int y2 = end.get(Calendar.YEAR);
        if (start.get(Calendar.YEAR) != y2) {
            start = (Calendar) start.clone();
            do {
                days += start.getActualMaximum(Calendar.DAY_OF_YEAR);
                start.add(Calendar.YEAR, 1);
            } while (start.get(Calendar.YEAR) != y2);
        }
        return days;
    }

    /**
     * 获取工作日
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 返回工作天数
     */
    public static int getWorkDay(Calendar start, Calendar end) {
        int result;
        if (start.after(end)) {
            Calendar swap = start;
            start = end;
            end = swap;
        }
        // 开始日期的日期偏移量
        int charge_start_date = 0;
        // 结束日期的日期偏移量
        int charge_end_date = 0;
        // 日期不在同一个日期内
        int startTemp;
        int endTemp;
        startTemp = 7 - start.get(Calendar.DAY_OF_WEEK);
        endTemp = 7 - end.get(Calendar.DAY_OF_WEEK);
        // 开始日期为星期六和星期日时偏移量为0
        if (startTemp != 0 && startTemp != 6) {
            charge_start_date = startTemp - 1;
        }
        // 结束日期为星期六和星期日时偏移量为0
        if (endTemp != 0 && endTemp != 6) {
            charge_end_date = endTemp - 1;
        }
        result = (getDaysBetween(getNextMonday(start), getNextMonday(end)) / 7) * 5 + charge_start_date - charge_end_date;
        return result;
    }

    /**
     * 获取休息日
     */
    public static int getHolidays(Calendar start, Calendar end) {
        return getDaysBetween(start, end) - getWorkDay(start, end);
    }

    /**
     * 获得日期的下一个星期一的日期
     */
    public static Calendar getNextMonday(Calendar date) {
        Calendar result;
        result = date;
        do {
            result = (Calendar) result.clone();
            result.add(Calendar.DATE, 1);
        } while (result.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY);
        return result;
    }

    /**
     * 获取中文日期
     */
    public static String getChineseWeek(Calendar date) {
        return dayNames[date.get(Calendar.DAY_OF_WEEK) - 1];
    }
}
