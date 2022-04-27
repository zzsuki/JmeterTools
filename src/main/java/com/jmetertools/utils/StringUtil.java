package com.jmetertools.utils;

public class StringUtil {
    public static int parseStringIntoInt(String sourceString){
        return Integer.parseInt(sourceString);
    }

    public static double parseStringIntoFloat(String sourceString){
        double resultDouble = 0.0;
        try {
            resultDouble =  Double.parseDouble(sourceString);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return resultDouble;
    }
}
