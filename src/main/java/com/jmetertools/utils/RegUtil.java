package com.jmetertools.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.jmetertools.base.exceptions.ParamException;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apache.commons.lang3.StringUtils.EMPTY;


/**
 * 正则工具的封装
 */
public class RegUtil {
    private static Logger logger = LogManager.getLogger(RegUtil.class);

    /**
     * 检查文本是否命中正则
     *
     * @param text  需要匹配的文本
     * @param regex 正则表达式
     * @return boolean 是否匹配
     */
    public static boolean isMatch(String text, String regex) {
        return getMatcher(text, regex).find();
    }

    /**
     * 检查文本是否完全匹配，不包含其他杂项，相当于加上了^和$
     *
     * @param text  需要匹配的文本
     * @param regex 正则表达式
     * @return boolean 检查文本是否完全匹配
     */
    public static boolean isFullMatch(String text, String regex) {
        return getMatcher(text, regex).matches();
    }

    /**
     * 获取匹配对象
     *
     * @param text 需要匹配的文本
     * @param regex 正则表达式
     * @return Matcher 对象，等待进一步使用
     */
    private static Matcher getMatcher(String text, String regex) {
        if (StringUtils.isAnyBlank(text, regex)) ParamException.fail("正则参数错误!");
        return Pattern.compile(regex).matcher(text);
    }

    /**
     * 返回所有匹配的内容
     *
     * @param text  需要匹配的文本
     * @param regex 正则表达式
     * @return List<String>
     */
    public static List<String> findAll(String text, String regex) {
        Matcher matcher = getMatcher(text, regex);
        List<String> result = new ArrayList<>();
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * 获取第一个匹配内容
     *
     * @param text 需要匹配的文本
     * @param regex 正则表达式
     * @return String 匹配到的第一个对象
     */
    public static String findFirst(String text, String regex) {
        Matcher matcher = getMatcher(text, regex);
        if (matcher.find()) return matcher.group();
        return EMPTY;
    }

//    /**
//     * 获取匹配项，不包含文字信息，会删除regex的内容
//     * <p>不保证完全正确</p>
//     *
//     * @param text
//     * @param regex
//     * @return
//     */
//    @Deprecated
//    public static String getRegex(String text, String regex) {
//        if (StringUtils.isAnyBlank(text, regex)) ParamException.fail("正则参数错误!");
//        String result = EMPTY;
//        try {
//            result = findAll(text, regex).get(0);
//            String[] split = regex.split("(\\.|\\+|\\*|\\?)");
//            for (int i = 0; i < split.length; i++) {
//                String s1 = split[i];
//                if (!s1.isEmpty())
//                    result = result.replaceAll(s1, EMPTY);
//            }
//        } catch (Exception e) {
//            logger.warn("获取匹配对象失败！", e);
//        } finally {
//            return result;
//        }
//    }



    public static String getRegStringWithIndex(String reg, String sourceString, int index){
        // .*(\d-\d+)$
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(sourceString);

        String result;
        if(matcher.find()){
            result = matcher.group(index);
        }else {
            result = "";
        }
        return result;
    }
}


