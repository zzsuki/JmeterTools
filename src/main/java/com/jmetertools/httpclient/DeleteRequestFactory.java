package com.jmetertools.httpclient;

import org.apache.http.client.methods.HttpDelete;

public class DeleteRequestFactory extends EntityHandler{
    /**
     * 获取{@link HttpDelete}对象
     *
     * @param url url地址
     * @return delete对象
     */
    public static HttpDelete createRequest(String url) {
        return new HttpDelete(url);
    }
}
