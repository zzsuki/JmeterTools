package com.meterspheretools.utils

import java.util.regex.Matcher
import java.util.regex.Pattern

class RegUtil {
    public static String getRegStringWithIndex(String reg, String sourceString, int index){
        // .*(\d-\d+)$
        Pattern pattern = Pattern.compile(reg)
        Matcher matcher = pattern.matcher(sourceString)

        String result
        if(matcher.find()){
            result = matcher.group(index)
        }else {
            result = ""
        }
        return result
    }
}
