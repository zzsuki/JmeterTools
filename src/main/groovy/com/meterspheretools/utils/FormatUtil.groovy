package com.meterspheretools.utils

import java.text.DecimalFormat

class FormatUtil {
    /**
     * 返回格式化输出的字符串
     * @param number: 待格式化的数字
     * @param format：格式化表达式；使用0或#进行站位，使用0时位数不足则补0
     * @return 格式化后的string
     */
    static <T extends Number> String getFormatNumber(T number, String expression){
        DecimalFormat format = new DecimalFormat(expression)
        return format.format(number)
    }
}
