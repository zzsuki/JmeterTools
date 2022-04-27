package com.jmetertools.base.exceptions;

import com.jmetertools.base.Constant;

public class FailException extends RuntimeException{
    private static final long serialVersionUID = -7041169491254546905L;

    public FailException(){
        super(Constant.DEFAULT_MESS);
    }

    public FailException(String message){
        super(message);
    }

    public static void fail(String message){
        throw new FailException(message);
    }

    /**
     * 默认异常，debug用
     */
    public static void fail(){
        throw new FailException();
    }

    /**
     * 修改异常类型为运行时异常
     * @param e 通用异常
     */
    public static void fail(Exception e){
        throw new FailException(e.getMessage());
    }
}
