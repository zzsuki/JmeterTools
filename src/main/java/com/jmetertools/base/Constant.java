package com.jmetertools.base;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Constant {

    // cipher相关配置
    public static final String RANGE_KEY = "Bl&2V046640V2&lB";
    public static final String UMP_KEY = "Bl666666666666lB";
    public static final int ENCRYPT_PASSWORD_LEN = 64;
    public static final int ENCRYPT_RANGE_TOKEN_LEN = 128;
    public static final int ENCRYPT_UMP_TOKEN_LEN = 96;


    private static List<String> getBroadCharSet(){
        List<String> charSet = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    }


    // 字符集，一般用于随机取样,类型使用List<String>
    public static Supplier<List<String>> BROAD_CHAR_SET = () -> {
        List<String> charSet = new ArrayList<>();
        for (int i = 0; i < 256; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    };

    //
    public static Supplier<List<String>> NUMBERS = () -> {
        List<String> charSet = new ArrayList<>();
        for (int i = 48; i < 58; i++) {
            charSet.add(i, Character.toString((char) i));
        }
        return charSet;
    };

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

    // 默认信息
    public static String DEFAULT_MESS = "zzsuki";



}
