package com.jmetertools.utils;

import java.math.BigInteger;

public class ByteUtil {

    /**
     * 字节数组切片， 从起始位置截取至指定位置
     * @param srcByte 源字节数组
     * @param beginIndex 其实下标
     * @param endIndex 结束位置
     * @return 截取后的字节数组，截取范围为[beginIndex, endIndex)
     */
    public static byte[] subByteArray(byte[] srcByte, int beginIndex, int endIndex){
        byte[] newByte = new byte[endIndex - beginIndex];
        System.arraycopy(srcByte, beginIndex, newByte, 0, endIndex - beginIndex);
        return newByte;
    }

    /**
     * 字节数组切片，从起始位置截取其后所有元素
     * @param srcByte byte[] 源字节数组
     * @param beginIndex 起始位置
     * @return 截取后的字节数组，截取范围为[beginIndex, srcByte.length)
     */
    public static byte[] subByteArray(byte[] srcByte, int beginIndex){
        byte[] newByte = new byte[srcByte.length - beginIndex];
        System.arraycopy(srcByte, beginIndex, newByte, 0, srcByte.length - beginIndex);
        return newByte;
    }

    /**
     * 将byte[]转为各种进制的字符串
     * @param bytes byte[]
     * @param radix 可以转换进制的范围，从Character.MIN_RADIX到Character.MAX_RADIX，超出范围后变为10进制
     * @return 转换后的字符串
     */
    public static String binary(byte[] bytes, int radix){
        return new BigInteger(1, bytes).toString(radix);  // 这里的1代表正数
    }

    /**
     * 16进制的字符串表示转成字节数组
     *
     * @param hexString 16进制格式的字符串
     * @return 转换后的字节数组
     **/
    public static byte[] toByteArray(String hexString) {
        if (hexString.isEmpty())
            throw new IllegalArgumentException("this hexString must not be empty");

        hexString = hexString.toLowerCase();
        final byte[] byteArray = new byte[hexString.length() / 2];
        int k = 0;
        for (int i = 0; i < byteArray.length; i++) { //因为是16进制，最多只会占用4位，转换成字节需要两个16进制的字符，高位在先
            byte high = (byte) (Character.digit(hexString.charAt(k), 16) & 0xff);
            byte low = (byte) (Character.digit(hexString.charAt(k + 1), 16) & 0xff);
            byteArray[i] = (byte) (high << 4 | low);
            k += 2;
        }
        return byteArray;
    }

    public static String toString(byte[] array){
        return new String(array);
    }
}
