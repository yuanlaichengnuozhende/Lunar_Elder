package com.lunar.common.core.utils;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;

/**
 * @author szx
 * @date 2022/11/24 14:54
 */
@Slf4j
public class DateUtils {

    public static final String yyyyMMddHHmmss = "yyyyMMddHHmmss";

    public static String formatyyyyMMddHHmmss(Date date) {
        return DateUtil.format(date, yyyyMMddHHmmss);
    }

    /**
     * 时间转换
     *
     * @param dateText
     * @return
     */
    public static Date getStartDate(String dateText) {
        return getDate(dateText, false);
    }

    /**
     * 时间转换
     *
     * @param dateText
     * @return
     */
    public static Date getEndDate(String dateText) {
        return getDate(dateText, true);
    }

    /**
     * 时间转换。一天的开始或结束
     *
     * @param dateText
     * @param endFlag
     * @return
     */
    public static Date getDate(String dateText, boolean endFlag) {
        if (StringUtils.isBlank(dateText)) {
            return null;
        }

        Date date = null;
        try {
            if (endFlag) {
                date = DateUtil.endOfDay(DateUtil.parse(dateText)).toJdkDate();
            } else {
                date = DateUtil.beginOfDay(DateUtil.parse(dateText))
                    .toJdkDate();
            }

        } catch (Exception e) {
            log.error("时间转换失败", e);
        }

        return date;
    }

    /**
     * 时间转换
     *
     * @param dateText
     * @return
     */
    public static Date parseStringDate(String dateText) {

        if (StringUtils.isBlank(dateText)) {
            return null;
        }

        Date date = DateUtil.parse(dateText, "yyyy-MM-dd HH:mm:ss");

        return date;
    }

    /**
     * 需要统计的时间
     *
     * @param year
     * @return
     */
    public static List<Date> dateList(int year) {
        Date start = DateUtil.parseDate(year + "-01-01").toJdkDate();
        Date end = null;
        if (DateUtil.thisYear() == year) {
            end = DateUtil.beginOfMonth(new Date());
        } else {
            end = DateUtil.parseDate(year + "-12-01").toJdkDate();
        }
        return betweenData(start, end);
    }

    /**
     * 返回start-end之间所有月份
     *
     * @param start
     * @param end
     * @return
     */
    public static List<Date> betweenData(Date start, Date end) {
        List<Date> list = Lists.newArrayList();
        int month = (int) DateUtil.betweenMonth(start, end, true) + 1;
        for (int i = 0; i < month; i++) {
            Date date = DateUtil.offsetMonth(start, i).toJdkDate();
            list.add(date);
        }
        return list;
    }

}
