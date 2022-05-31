package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.entity.StringEntity;

import static com.jmetertools.base.Constant.DEFAULT_CHARSET;

public class PatchRequestFactory extends EntityHandler{

    /**
     * 获取{@link HttpPatch}对象
     *
     * @param url url地址
     * @return patch对象
     */
    public static HttpPatch createRequest(String url) {
        return new HttpPatch(url);
    }

    /**
     * 获取{@link HttpPatch}对象
     *
     * @param url url地址
     * @param params 请求参数
     * @return path对象
     */
    public static HttpPatch createRequest(String url, JSONObject params) {
        HttpPatch httpPatch = createRequest(url);
        if (params != null && !params.isEmpty())
            httpPatch.setEntity(new StringEntity(params.toString(), DEFAULT_CHARSET.toString()));
        httpPatch.addHeader(HttpClientConstant.ContentType_JSON);
        return httpPatch;
    }
}
