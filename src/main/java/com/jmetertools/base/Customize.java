package com.jmetertools.base;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.exceptions.FailException;
import com.jmetertools.base.exceptions.ParamException;
import com.jmetertools.utils.RegUtil;
import com.jmetertools.utils.TimeUtil;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.Phaser;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.jmetertools.base.Constant.*;

public class Customize {
    private static Logger logger = LogManager.getLogger(Customize.class);

    private static Scanner scanner;

    /**
     * 获取当前时间戳10位int 类型的数据
     *
     * @return 当前时间戳，10位int
     */
    public static int getMark() {
        return (int) (TimeUtil.getTimeStamp() / 1000);
    }

    /**
     * 获取纳秒的时间标记
     *
     * @return 当前时间，单位位ns
     */
    public static long getNanoMark() {
        return System.nanoTime();
    }

    /**
     * 等待方法，用scanner类，控制台输出字符key时会跳出循环
     * <p>如何执行close方法，只能用一次</p>
     *
     * @param key 等待字符，需要等待到这个字符才能继续
     */
    public static void waitForKey(Object key) {
        logger.warn("请输入“{}”继续运行！", key.toString());
        long start = TimeUtil.getTimeStamp();
        scanner = scanner == null ? new Scanner(System.in, DEFAULT_CHARSET.name()) : scanner;
        while (scanner.hasNext()) {
            String next = scanner.next();
            if (next.equalsIgnoreCase(key.toString())) break;
            logger.warn("输入：{}错误！", next);
        }
        long end = TimeUtil.getTimeStamp();
        double timeDiffer = TimeUtil.getTimeDiffer(start, end);
        logger.info("本次共等待：" + timeDiffer + "秒！");
    }

    /**
     * 自定义等待,默认间隔0.5s
     *
     * @param f 判断条件
     */
    public static void waitFor(Supplier<Boolean> f) {
        waitFor(f, 0.2);
    }

    /**
     * 自定义等待功能,自定义时间
     *
     * @param f      判断条件， 返回值需要时bool
     * @param second 描述
     */
    public static void waitFor(Supplier<Boolean> f, double second) {
        while (!f.get()) {
            sleep(second);
        }
    }

    /**
     * 获取屏幕输入内容
     * <p>如何执行close方法，只能用一次</p>
     *
     * @return 获取到的输入内容
     */
    public static String getInput() {
        scanner = scanner == null ? new Scanner(System.in, DEFAULT_CHARSET.name()) : scanner;
        String next = scanner.next();
        logger.info("输入内容：{}", next);
        return next;
    }

    /**
     * 关闭scanner，解决无法多次使用wait的BUG
     */
    public static void closeScanner() {
        if (scanner != null) scanner.close();
    }

    /**
     * 将数组变成json对象，使用split方法
     * <p>
     * split方法默认limit=2
     * </p>
     * int和double使用数字类型,其他使用字符串类型
     *
     * @param objects 对象数组，待转换的数组
     * @param regex   分隔的regex表达式
     * @return 转换后的json对象
     */
    public static JSONObject changeArraysToJson(Object[] objects, String regex) {
        JSONObject args = new JSONObject();
        Arrays.stream(objects).forEach(x -> {
            String[] split = x.toString().split(regex, 2);
            args.put(split[0], isInteger(split[1]) ? changeStringToInt(split[1]) : isDouble(split[1]) ? changeStringToDouble(split[1]) : split[1]);
        });
        return args;
    }

    /**
     * 获取一个简单的json对象
     * <p>
     * 使用“=”号作为分隔符，limit=2
     * </p>
     *
     * @param content 转换源内容
     * @return 转换后的json对象
     */
    public static JSONObject getJson(String... content) {
        if (StringUtils.isAnyEmpty(content)) ParamException.fail("转换成json格式参数错误!");
        return changeArraysToJson(content, EQUAL);
    }

    /**
     * 获取一个简单的JSON对象
     *
     * @param key 对象的key
     * @param value 对象值(内容)
     * @return 转换好的json对象
     */
    public static JSONObject getSimpleJson(String key, Object value) {
        return StringUtils.isBlank(key) ? null : new JSONObject(1) {{
            put(key, value);
        }};
    }

    /**
     * 获取text复制拼接的string
     *
     * @param text 复制的text
     * @param times 次数
     * @return 处理后的字符串
     */
    public static String getManyString(String text, int times) {
        return IntStream.range(0, times).mapToObj(x -> text).collect(Collectors.joining());
    }

    /**
     * 获取一个百分比，两位小数
     *
     * @param total 总数
     * @param piece 成功数
     * @return 百分比
     */
    public static double getPercent(int total, int piece) {
        if (total == 0) return 0.00;
        int s = (int) (piece * 1.0 / total * 10000);
        return s * 1.0 / 100;
    }

    /**
     * 获取百分比,string类型,拼接%符合,两位小数
     *
     * @param percent 这里传的需要计算好的百分比,实际比例*100,而不是比例
     * @return 格式化后的字符串
     */
    public static String getPercent(double percent) {
        return formatDouble(percent) + "%";
    }

    /**
     * 格式化数字格式，使用千分号
     *
     * @param number 待转字符串
     * @return 格式化后的数字
     */
    public static String formatLong(Number number) {
        return formatNumber(number, "#,###");
    }

    /**
     * 格式化数字格式,保留两位有效数字,使用去尾法
     *
     * @param number 待转字符串
     * @return 格式化后的数字
     */
    public static String formatDouble(Number number) {
        return formatNumber(number, "#.##");
    }

    /**
     * 格式化数字格式
     *
     * @param number 待格式化数字
     * @param pattern 格式表达式，和DecimalFormat支持的一致
     * @return 格式化的结果
     */
    public static String formatNumber(Number number, String pattern) {
        DecimalFormat format = new DecimalFormat(pattern);
        return format.format(number);
    }

    /**
     * 格式化int数字,用于补充0的场景
     *
     * @param number 待格式化数字
     * @param length 数字总长
     * @return 填充后的内容
     */
    public static String formatInt(int number, int length) {
        String s = number + EMPTY;
        return s.length() >= length ? s : getManyString("0", length - s.length()) + s;
    }

    /**
     * 把string类型转化为int
     *
     * @param text 需要转化的文本
     * @return 转化后的数字
     */
    public static int changeStringToInt(String text) {
        logger.debug("需要转化成的文本：{}", text);
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException e) {
            logger.warn("转化int类型失败！", e);
            return TEST_ERROR_CODE;
        }
    }

    /**
     * 把string类型转化为long
     *
     * @param text 需要转化的文本
     * @return 转换后的数字
     */
    public static long changeStringToLong(String text) {
        logger.debug("需要转化成的文本：{}", text);
        try {
            return Long.parseLong(text);
        } catch (NumberFormatException e) {
            logger.warn("转化int类型失败！", e);
            return TEST_ERROR_CODE;
        }
    }

    /**
     * 将string转换成boolean，失败返回null，待修改
     *
     * @param text 需转数字的文本
     * @return 转换后的bolean
     */
    public static boolean changeStringToBoolean(String text) {
        logger.debug("需要转化成的文本：{}", text);
        if (text == null || !RegUtil.isMatch(text.toLowerCase(), "false|true")) return false;
        return text.equalsIgnoreCase("true");
    }

    /**
     * string转化为double
     *
     * @param text 需转数字的文本
     * @return 转换后的double
     */
    public static double changeStringToDouble(String text) {
        logger.debug("需要转化成的文本：{}", text);
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            logger.warn("转化double类型失败！", e);
            return TEST_ERROR_CODE * 1.0;
        }
    }

    /**
     * 是否是数字，000不算,0.0也算,-0和-0.0不算
     *
     * @param text 待检测字符
     * @return 检测结果
     */
    public static boolean isNumber(String text) {
        logger.debug("需要判断的文本：{}", text);
        if (StringUtils.isEmpty(text) || text.equals("-0")) return false;
        if (text.equals("0")) return true;
        return RegUtil.isMatch(text, "-{0,1}(([1-9][0-9]*)|0)(.\\d+){0,1}");
    }

    /**
     * 是否是整型， 超过10位长度返回false
     * @param str 待检测字符
     * @return 检测结果
     */
    public static boolean isInteger(String str) {
        return isNumber(str) && !str.contains(".") && str.length() < 11;
    }

    /**
     * 是否是double类型
     * @param str 待检测字符串
     * @return 检测结果
     */
    public static boolean isDouble(String str) {
        return isNumber(str) && str.contains(".");
    }

    /**
     * 线程休眠,单位是秒,groovy使用,不可靠,还是以ms为单位的
     *
     * @param second 秒，可以是小数
     */
    public static void sleep(int second) {
        if (second > 100) FailException.fail("休眠时间过长,请更换其他方式!");
        try {
            Thread.sleep(second * 1000L);
        } catch (InterruptedException e) {
            logger.warn("sleep发生错误！", e);
        }
    }

    /**
     * 睡眠,提供更精准的休眠功能
     *
     * @param time 单位s
     */
    public static void sleep(double time) {
        if (time > 100) FailException.fail("休眠时间过长,请更换其他方式!");
        try {
            Thread.sleep((long) (time * 1000));
        } catch (InterruptedException e) {
            logger.warn("sleep发生错误！", e);
        }
    }

    /**
     * 线程休眠,以纳秒为单位
     *
     * @param nanoSec 休眠时长，纳秒单位
     */
    public static void sleep(long nanoSec) {
        if (nanoSec < 1_000_000) return;
        try {
            TimeUnit.NANOSECONDS.sleep(nanoSec);
        } catch (InterruptedException e) {
            logger.warn("sleep发生错误！", e);
        }
    }

    /**
     * 获取随机数，获取1~num 的数字，包含 num
     *
     * @param num 随机数上限
     * @return 随机数
     */
    public static int getRandomInt(int num) {
        return ThreadLocalRandom.current().nextInt(num) + 1;
    }

    /**
     * 获取随机数，获取1~num 的数字，包含 num
     *
     * @param num 数字阈值
     * @return 随机数
     */
    public static long getRandomLong(long num) {
        return ThreadLocalRandom.current().nextLong(num) + 1;
    }

    /**
     * 随机范围int,取头不取尾
     *
     * @param start 左边界，包含
     * @param end 右边界，不包含
     * @return 随机int
     */
    public static int getRandomIntRange(int start, int end) {
        if (end <= start) return TEST_ERROR_CODE;
        return ThreadLocalRandom.current().nextInt(end - start) + start;
    }

    /**
     * 随机选择某一个值，相当于sample
     *
     * @param fs 待选择容器
     * @param <T> 待选类型
     * @return 抽样结果
     */
    public static <T> T random(T... fs) {
        return fs[getRandomInt(fs.length) - 1];
    }

    /**
     * 随机选择某一个字符串
     *
     * @param fs 待选择容器
     * @return 抽样结果
     */
    public static String random(String... fs) {
        if (ArrayUtils.isEmpty(fs)) ParamException.fail("数组不能为空!");
        return fs[getRandomInt(fs.length) - 1];
    }

    /**
     * 随机选择某一个对象
     *
     * @param list 待抽样列表
     * @param <T> 抽样类型
     * @return 抽样结果
     */
    public static <T> T random(List<T> list) {
        if (list == null || list.isEmpty()) ParamException.fail("数组不能为空!");
        return list.get(getRandomInt(list.size()) - 1);
    }

    /**
     * 根据不同的概率随机出一个对象集合
     * 消耗CPU多
     *
     * @param count 用于随机取样的map
     * @param <T> 抽样对象的类型
     * @return 根据概率抽样出的list结果
     */
    public static <T> List[] randomCpu(Map<T, Integer> count) {
        List<T> keys = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        count.forEach((key, value) -> {
            keys.add(key);
            values.add(value);
        });
        int t = 0;
        for (int i = 0; i < values.size(); i++) {
            t = t + values.get(i);
            values.set(i, t);
        }
        return new List[]{keys, values};
    }

    public static <T> T randomStage(List<T> fs, List<Integer> stages) {
        int randomInt = getRandomInt(stages.get(fs.size() - 1));
        for (int i = 0; i < stages.size(); i++) {
            if (randomInt <= stages.get(i)) return fs.get(i);
        }
        return null;
    }

    /**
     * 根据不同的概率随机出一个对象集合
     * 消耗内存多
     *
     * @param count 概率map
     * @param <F> 泛型类型
     * @return 随机集合
     */
    public static <F> List<F> randomMem(Map<F, Integer> count) {
        List<F> keys = new ArrayList<>();
        List<Integer> values = new ArrayList<>();
        count.entrySet().forEach(f -> {
            keys.add(f.getKey());
            values.add(f.getValue());
        });
        for (int i = 0; i < values.size(); i++) {
            for (int j = 0; j < values.get(i) - 1; j++) {
                keys.add(keys.get(i));
            }
        }
        return keys;
    }

    /**
     * 获取一定范围内的随机值
     *
     * @param start 初始值
     * @param range 随机范围
     * @return 随机结果
     */
    public static double getRandomRange(double start, double range) {
        return start - range + getRandomDouble() * range * 2;
    }

    /**
     * 获取随机数，获取0-1 的数字
     *
     * @return 随机数
     */
    public static double getRandomDouble() {
        return ThreadLocalRandom.current().nextDouble();
    }

    /**
     * 获取一个IntStream
     *
     * @param start 左边界
     * @param end 右边界
     * @return stream实例
     */
    public static IntStream range(int start, int end) {
        return IntStream.range(start, end);
    }

    /**
     * 获取一个intsteam，默认从0开始,num为止,不包含num
     *
     * @param num 边界值，不包含
     * @return int stream
     */
    public static IntStream range(int num) {
        return IntStream.range(0, num);
    }


    /**
     * 将对象转换成JSON
     *
     * @param o 待转对象
     * @return 转换后的json对象
     */
    public static JSONObject parse(Object o) {
        return parse(JSON.toJSONString(o));
    }

    public static JSONObject parse(String o) {
        return JSON.parseObject(o);
    }


//    /**
//     * 异步执行某个代码块
//     * Java调用需要return,Groovy也不需要,语法兼容
//     *
//     * @param f
//     */
//    public static void fun(Supplier f) {
//        go(f, null);
//    }

//    /**
//     * 异步执行代码块,使用{@link Phaser}进行多线程同步
//     *
//     * @param supplier
//     * @param phaser
//     */
//    public static void go(Supplier supplier, Phaser phaser) {
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    if (phaser != null) phaser.register();
//                    supplier.get();
//                } catch (Exception e) {
//                    logger.warn("执行异步方法时发生错误!", e);
//                } finally {
//                    if (phaser != null) {
//                        logger.info("异步任务完成 {}", phaser.getArrivedParties());
//                        phaser.arrive();
//                    }
//
//                }
//
//            }
//        };
//        ThreadPoolUtil.executeSync(runnable);
//    }

    /**
     * 获取方法的执行时间
     *
     * @param supplier 方法容器
     */
    public static void time(Supplier supplier) {
        time(supplier, 1);
    }

    /**
     * 获取方法的执行时间
     *
     * @param f     执行方法
     * @param times 执行次数
     */
    public static void time(Supplier f, int times) {
        long start = TimeUtil.getTimeStamp();
        for (int i = 0; i < times; i++) {
            f.get();
        }
        long end = TimeUtil.getTimeStamp();
        logger.info("执行{}次耗时:{}", times, formatLong(end - start) + " ms");
    }

    /**
     * 获取方法的执行时间
     *
     * @param f     执行方法
     * @param name 名称
     * @param times 执行次数
     */
    public static void time(Supplier f, int times, String name) {
        long start = TimeUtil.getTimeStamp();
        for (int i = 0; i < times; i++) {
            f.get();
        }
        long end = TimeUtil.getTimeStamp();
        logger.info("{}执行{}次耗时:{}", name, times, formatLong(end - start) + " ms");
    }

    public static void time(Supplier f, String name) {
        long start = TimeUtil.getTimeStamp();
        f.get();
        long end = TimeUtil.getTimeStamp();
        logger.info("{}执行耗时:{}", name, formatLong(end - start) + " ms");
    }

    /**
     * 取消方法执行过程中的异常显示
     *
     * @param supplier 待处理内容
     */
    public static void noError(Supplier supplier) {
        try {
            supplier.get();
        } catch (Exception e) {

        }
    }

    /**
     * 通用的终止运行的方法,用于脚本调试等场景
     */
    public static void fail() {
        throw new FailException();
    }

    /**
     * 通过将对象序列化成数据流实现深层拷贝的方法
     * <p>
     * 将该对象序列化成流,因为写在流里的是对象的一个拷贝，而原对象仍然存在于JVM里面。所以利用这个特性可以实现对象的深拷贝
     * </p>
     *
     * @param t   需要被拷贝的对象,必需实现 Serializable接口,不然会报错
     * @param <T> 需要拷贝对象的类型
     * @return 克隆后的对象
     */
    public static <T> T deepClone(T t) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(t);
            // 将流序列化成对象
            ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
            ObjectInputStream ois = new ObjectInputStream(bais);
            return (T) ois.readObject();
        } catch (IOException e) {
            logger.error("线程任务拷贝失败!", e);
        } catch (ClassNotFoundException e) {
            logger.error("未找到对应类!", e);
        }
        return null;
    }

}
