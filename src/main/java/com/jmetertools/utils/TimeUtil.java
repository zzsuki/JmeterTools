package com.jmetertools.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.jmetertools.base.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 时间相关功能工具类
 */
public class TimeUtil {
    private static final Logger logger = LogManager.getLogger(TimeUtil.class);

    /**
     * 默认的日志显示格式
     */
    private static final ThreadLocal<SimpleDateFormat> DEFAULT_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

    /**
     * 纯数字的日期格式
     */
    private static final ThreadLocal<SimpleDateFormat> NUM_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

    /**
     * 标记日期格式,选用ddHHmm
     */
    private static final ThreadLocal<SimpleDateFormat> MARK_FORMAT = ThreadLocal.withInitial(() -> new SimpleDateFormat("ddHHmm"));

    /**
     * 获取calendar类对象，默认UTC时间
     *
     * @return calendar 对象
     */
    public static Calendar calendarInit() {
        Calendar instance = Calendar.getInstance();
        instance.setTime(new Date());
        return instance;
    }

    /**
     * 获取时间戳，13位long类型
     *
     * @return 时间戳
     */
    public static long getTimeStamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取UTC时间戳
     *
     * @param time 纯数字日期
     * @return utc时间戳
     */
    public static long getUtcTimestamp(long time) {
        return getUtcTimestamp(time + Constant.EMPTY);
    }

    /**
     * 获取一天开始，utc
     *
     * @return 一天的起始时间
     */
    public static String getStartOfDay() {
        return getUtcDate() + " 00:00:00";
    }

    /**
     * 获取一天结束，utc
     *
     * @return 一天的结束时间，使用23:55:55
     */
    public static String getEndOfDay() {
        return getUtcDate() + " 23:55:55";
    }

    /**
     * 获取当天日期，utc
     *
     * @return 当日日期
     */
    public static String getUtcDate() {
        int month = getMonth();
        int day = getDay();
        return getYear() + "-" + (month < 10 ? "0" + month : month) + "-" + (day < 10 ? "0" + day : day);
    }

    /**
     * 获取时间戳
     *
     * @param time 传入时间，纯数字
     * @return 返回时间戳，毫秒
     */
    public static long getUtcTimestamp(String time) {
        long timestamp = getTimeStamp(time);
        return timestamp + Calendar.getInstance().getTimeZone().getRawOffset();
    }

    /**
     * 获取当前星期数（按年）
     *
     * @return 当前星期数
     */
    public static int getWeeksNum() {
        return calendarInit().get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取月份,获取值+1,索引从0开始的
     *
     * @return 当前月份
     */
    public static int getMonth() {
        return calendarInit().get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前是当月的第几天
     *
     * @return 当前天数(某月的)
     */
    public static int getDay() {
        return calendarInit().get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取年份
     *
     * @return 当前年份
     */
    public static int getYear() {
        return calendarInit().get(Calendar.YEAR);
    }

    /**
     * 获取小时数
     * @return 当前小时
     */
    public static int getHour() {
        return calendarInit().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取分钟数
     * @return 当前分钟数
     */
    public static int getMinute() {
        return calendarInit().get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒钟数
     * @return 当前秒数
     */
    public static int getSecond() {
        return calendarInit().get(Calendar.SECOND);
    }

    /**
     * 获取当前时间
     *
     * @return 返回当前时间
     */
    public static String getNow() {
        return getNow(NUM_FORMAT.get());
    }

    /**
     * 获取当前日期
     * @return 当前日期
     */
    public static String markDate() {
        return getNow(MARK_FORMAT.get());
    }

    /**
     * 获取当前格式化时间
     * @param format 时间格式，String格式
     * @return 格式化后的时间
     */
    public static String getNow(String format) {
        return getNow(new SimpleDateFormat(format));
    }

    /**
     * 获取当前格式化时间
     * @param now 时间格式, SimpleDateFormat类
     * @return 格式化后的时间
     */
    public static String getNow(SimpleDateFormat now) {
        return now.format(new Date());
    }

    /**
     * 获取时间戳,会替换掉所有非数字的字符
     * 默认返回{@link Constant#DEFAULT_LONG}
     *
     * @param time 传入时间，纯数字组成的时间
     * @return 返回时间戳，毫秒
     */
    public static long getTimeStamp(String time) {
        time = time.replaceAll("\\D*", Constant.EMPTY).substring(0, 14);
        try {
            return NUM_FORMAT.get().parse(time).getTime();
        } catch (ParseException e) {
            logger.warn("时间格式错误！", e);
        }
        return Constant.DEFAULT_LONG;
    }

    /**
     * 根据时间戳返回对应的时间，并且输出
     *
     * @param time long 时间戳
     * @return 返回时间
     */
    public static String getTimeByTimestamp(long time) {
        Date now = new Date(time);
        return DEFAULT_FORMAT.get().format(now);
    }

    /**
     * 获取时间差，以秒为单位
     *
     * @param start 开始时间
     * @param end   结束时间
     * @return 时间差
     */
    @Deprecated
    public static double getTimeDiffer(Date start, Date end) {
        return getTimeDiffer(start.getTime(), end.getTime());
    }

    /**
     * 重载，用long类型取代date
     *
     * @param start 开始时间
     * @param end 结束时间
     * @return 时间差
     */
    public static double getTimeDiffer(long start, long end) {
        return (end - start) / 1000.0;
    }

    /**
     * 获取当前时间,返回默认格式时间
     *
     * @return 当前时间(格式)
     */
    public static String getDate() {
        return getNow(DEFAULT_FORMAT.get());
    }
}
