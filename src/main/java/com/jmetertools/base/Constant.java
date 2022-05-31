package com.jmetertools.base;

import org.apache.http.Consts;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.http.Consts.UTF_8;

public class Constant {

    /**
     * cipher信息，用于密码加解密和token加解密；不同项目的key可能不同，对应的加密后length也可能不同
     */
    public static final String RANGE_KEY = "Bl&2V046640V2&lB";
    public static final String UMP_KEY = "Bl666666666666lB";
    public static final int ENCRYPT_PASSWORD_LEN = 64;
    public static final int ENCRYPT_RANGE_TOKEN_LEN = 128;
    public static final int ENCRYPT_UMP_TOKEN_LEN = 96;

    /**
     * 校验IP+port的正确性
     */
    public static final String HOST_REGEX = "((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))):([0-9]|[1-9]\\d{1,3}|[1-5]\\d{4}|6[0-4]\\d{4}|65[0-4]\\d{2}|655[0-2]\\d|6553[0-5])";
    /**
     * 测试错误代码
     */
    public static final int TEST_ERROR_CODE = 404;

    /**
     * 获取宽字符集合，一般用于速记取样；本身是一个lambda函数，获取return值时需要调用Supplier.get()
     *
     * return: List<String>
     */
    public static Supplier<List<String>> BROAD_CHAR_SET = () -> {
        List<String> charSet = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    };

    /**
     * 获取数字字符集合，一般用于速记取样；本身是一个lambda函数，获取return值时需要调用Supplier.get()
     *
     * return:  List<String>
     */
    public static Supplier<List<String>> NUMBERS = () -> {
        List<String> charSet = new ArrayList<>();
        for (int i = 48; i < 58; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    };
    /**
     * 获取字母字符集合(包括大小写)，一般用于速记取样；本身是一个lambda函数，获取return值时需要调用Supplier.get()
     */
    public static Supplier<List<String>> ALPHABET = () -> {
        List<String> charSet = new ArrayList<>();
        for (int i = 65; i < 91; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        for (int i = 97; i < 123; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    };

    /**
     * 默认信息(仅用于测试)
     */
    public static String DEFAULT_MESS = "zzsuki";

    /*常用的常量*/
    public static final String LINE = "\r\n";

    public static final String TAB = "\t";

    public static final String EMPTY = "";

    public static final String COMMA = ",";

    public static final String UNKNOW = "?";

    public static final String OR = "/";

    public static final String PART = "|";

    public static final String EQUAL = "=";

    /**
     * 正则表达式中用到的{@link Constant#PART}
     */
    public static final String REG_PART = "\\|";

    public static final String SPACE_1 = " ";

    public static final String CONNECTOR = "_";

    private static final String[] PERCENT = {SPACE_1, "▁", "▂", "▃", "▄", "▅", "▅", "▇", "█"};

    /**
     * 统计性能数据的分桶数
     */
    public static final int BUCKET_SIZE = 32;

    /**
     * 动态模型中线程增长步长
     */
    public static int THREAD_STEP = 1;

    /**
     * 动态模型中,QPS增长步长
     */
    public static int QPS_STEP = 1;

    /**
     * 性能测试中统计输出间隔
     */
    public static int COUNT_INTERVAL = 5;

    /**
     * 线程池最大等待队列长度
     */
    public static final int MAX_WAIT_TASK = 10_0000;

    /**
     * 统计数据中的数量限制,小于该限制无法绘图
     */
    public static final int DRAW_LIMIT = BUCKET_SIZE * BUCKET_SIZE;

    /**
     * UTF-8字符编码格式
     */
    public static final Charset UTF_8 = Consts.UTF_8;

    /**
     * 默认字符集
     */
    public static Charset DEFAULT_CHARSET = UTF_8;

    /**
     * 默认响应内容的key
     */
    public static final String RESPONSE_CONTENT = "content";

    public static final long DEFAULT_LONG = 0L;

    public static final String DEFAULT_STATUS_STRING = "status";

}
