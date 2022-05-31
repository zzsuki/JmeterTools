package com.jmetertools.base.exceptions;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpRequestBase;

public class RequestException extends FailException{
    private static final long serialVersionUID = 7916010541762451964L;

    private RequestException() {
        super();
    }

    private RequestException(HttpRequestBase request) {
        super(request.toString());
    }

    private RequestException(String message) {
        super(message);
    }

    public static void fail(HttpRequestBase base) {
        throw new RequestException(base);
    }

    public static void fail(String message) {
        throw new RequestException(message);
    }

}
