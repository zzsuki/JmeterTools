package com.jmetertools.utils;

import java.util.List;

public class ListUtil {
    /**
     * list转字符串
     * @param list 字符list
     * @return 转换后的字符串
     */
    public static String convertToString(List<Character> list){
        StringBuilder result = new StringBuilder();
        for (Character ch: list){
            result.append(ch.toString());
        }
        return result.toString();
    }
}
