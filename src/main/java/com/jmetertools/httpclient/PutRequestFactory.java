package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;

import static com.jmetertools.base.Constant.DEFAULT_CHARSET;

public class PutRequestFactory extends EntityHandler{

    /**
     * 获取{@link HttpPut}请求对象
     *
     * @param url url地址
     * @return put对象
     */
    public static HttpPut createRequest(String url) {
        return new HttpPut(url);
    }

    /**
     * 获取{@link HttpPut}请求,{@link String}传参格式
     *
     * @param url url地址
     * @param params 请求参数
     * @return put对象
     */
    public static HttpPut createRequest(String url, String params) {
        HttpPut httpPut = createRequest(url);
        if (StringUtils.isNotBlank(params))
            httpPut.setEntity(new StringEntity(params, DEFAULT_CHARSET.toString()));
        httpPut.addHeader(HttpClientConstant.ContentType_JSON);
        return httpPut;
    }

    /**
     * 获取{@link HttpPut}请求,{@link JSONObject}表单格式
     *
     * @param url url地址
     * @param params 请求参数
     * @return put对象
     */
    public static HttpPut createRequest(String url, JSONObject params) {
        HttpPut httpPut = createRequest(url);
        if (params != null && !params.isEmpty())
            setFormHttpEntity(httpPut, params);
        httpPut.addHeader(HttpClientConstant.ContentType_FORM);
        return httpPut;
    }
}
