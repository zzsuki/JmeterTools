package com.jmetertools.base.exceptions;

import com.alibaba.fastjson.JSONObject;

public class VerifyException extends FailException{
    private static final long serialVersionUID = 7916010541762451964L;

    private VerifyException(){
        super();
    }

    private VerifyException(String message){
        super(message);
    }

    public static void fail(String message){
        throw new VerifyException(message);
    }

    public static void fail(JSONObject message){
        throw new VerifyException(message.toJSONString());
    }
}
