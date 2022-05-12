package com.jmetertools.utils;


import com.jmetertools.base.exceptions.FailException;
import com.jmetertools.base.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;



public class DecodeEncode extends Constant{
    private static Logger logger = LogManager.getLogger(DecodeEncode.class);

    /**
     * url进行转码，常用于网络请求，默认使用utf8
     *
     * @param text 需要转码的文本
     * @return 返回转码后的文本
     */
    public static String encodeUrl(String text) {
        return encodeUrl(text, UTF_8);
    }

    /**
     * url进行转码，常用于网络请求
     *
     * @param text 需要转码的文本
     * @return 返回转码后的文本
     */
    public static String encodeUrl(String text, Charset charset) {
        String result = EMPTY;
        try {
            result = StringUtils.isBlank(text) ? result : java.net.URLEncoder.encode(text, charset.toString());
        } catch (UnsupportedEncodingException e) {
            logger.warn("数据格式错误！", e);
        }
        return result;
    }

    /**
     * url进行解码，常用于解析响应，默认是UTF-8字符集
     *
     * @param text 需要解码的文本
     * @return 解码后的文本
     */
    public static String decodeUrl(String text, Charset charset) {
        String result = EMPTY;
        try {
            result = java.net.URLDecoder.decode(text, charset.toString());
        } catch (UnsupportedEncodingException e) {
            logger.warn("数据格式错误！", e);
        }
        return result;
    }

    /**
     * url进行解码，常用于解析响应，默认是UTF-8字符集
     *
     * @param text 需要解码的文本
     * @return 解码后的文本
     */
    public static String decodeUrl(String text) {
        return decodeUrl(text, UTF_8);
    }

    /**
     * 对本文进行base64解码，方法默认UTF_8
     *
     * @param text 待转码对象
     * @return 转码后的字符串对象
     */
    public static String decodeBase64String(String text) {
        return decodeBase64String(text, UTF_8);
    }

    /**
     * 对字符串进行解码,使用编码格式参数；会先对string转换为byte对象，然后进行转码
     *
     * @param text 待转码对象
     * @param charset 字符串字符集
     * @return 转码后的字符串
     */
    public static String decodeBase64String(String text, Charset charset) {
        return new String(decodeBase64Byte(text.getBytes(charset)));
    }

    /**
     * 使用base64 对byte对象进行转码。
     *
     * @param text 字节数组，待转码对象
     * @return 转码后的字节数组
     */
    public static byte[] decodeBase64Byte(byte[] text) {
        return Base64.getDecoder().decode(text);
    }

    /**
     * 使用base64 对byte对象进行转码。
     *
     * @param text 字节数组，待转码对象
     * @return 转码后的字节数组
     */
    public static byte[] decodeBase64StringToByte(String text) {
        return decodeBase64Byte(text.getBytes());
    }

    /**
     * 压缩字符串, 默认编码utf-8
     *
     * @param text 待压缩对象
     * @return 压缩后的的字符串
     */
    public static String zipBase64(String text) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            try (DeflaterOutputStream deflaterOutputStream = new DeflaterOutputStream(out)) {
                deflaterOutputStream.write(text.getBytes(Constant.UTF_8));
            }
            return DecodeEncode.encodeBase64Byte(out.toByteArray());
        } catch (IOException e) {
            logger.error("压缩文本失败:{}", text, e);
        }
        return EMPTY;
    }

    /**
     * 解压字符串,默认utf-8
     *
     * @param text 解压字符串
     * @return 解压后的字符串
     */
    public static String unzipBase64(String text) {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            try (OutputStream outputStream = new InflaterOutputStream(os)) {
                outputStream.write(DecodeEncode.decodeBase64StringToByte(text));
            }
            return new String(os.toByteArray(), Constant.UTF_8);
        } catch (IOException e) {
            logger.error("解压文本失败:{}", text, e);
        }
        return EMPTY;
    }

    /**
     * 将本文转码为base64，方法默认使用utf8字符集
     *
     * @param text 待转码文本
     * @return 转码后的字符串
     */
    public static String encodeBase64String(String text) {
        return encodeBase64String(text, UTF_8);
    }

    /**
     * 对本文进行base64转码，编码格式自定义
     *
     * @param text 待转码文本
     * @param charset 字符集
     * @return 转码后的字符串
     */
    public static String encodeBase64String(String text, Charset charset) {
        try {
            return new String(Base64.getEncoder().encode(text.getBytes(charset)));
        } catch (Exception e) {
            logger.warn("base64转码失败！", e);
            return EMPTY;
        }
    }

    /**
     * 对byte数组进行转码
     * @param data 待转码数组
     * @return 转码后的string
     */
    public static String encodeBase64Byte(byte[] data) {
        try {
            return new String(Base64.getEncoder().encode(data));
        } catch (Exception e) {
            logger.warn("base64转码失败！", e);
            return EMPTY;
        }
    }

    /**
     * 处理Unicode码转(\u6210\u529f)
     *
     * @param str 待转码unicode字符串
     * @return 转码后的字符串
     */
    public static String unicodeToString(String str) {
        Pattern pattern = Pattern.compile("(\\\\u(\\p{XDigit}{4}))");
        Matcher matcher = pattern.matcher(str);
        char ch;
        while (matcher.find()) {
            String group = matcher.group(2);
            ch = (char) Integer.parseInt(group, 16);
            String group1 = matcher.group(1);
            str = str.replace(group1, ch + EMPTY);
        }
        return str;
    }

}
