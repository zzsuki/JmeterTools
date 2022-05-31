package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.message.BasicHttpRequest;

public class GetRequestFactory extends EntityHandler{
    /**
     * 方法已重载，获取{@link HttpGet}对象
     * <p>方法重载，主要区别参数，会自动进行url encode操作</p>
     *
     * @param url 表示请求地址
     * @return 返回get对象
     */
    public static HttpGet createRequest(String url){
        return new HttpGet(url);
    }


    /**
     * 方法已重载，获取{@link HttpGet}对象
     * <p>方法重载，主要区别参数，会自动进行url encode操作</p>
     *
     * @param url  表示请求地址
     * @param args 表示传入数据
     * @return 返回get对象
     */
    public static HttpGet createRequest(String url, JSONObject args) {
        if (args == null || args.isEmpty()) return createRequest(url);
        String uri = url + convertJsonToArguments(args);
        return createRequest(uri);
    }

}
