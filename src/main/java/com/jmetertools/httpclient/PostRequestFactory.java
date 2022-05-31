package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.Constant;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import java.io.File;
import java.security.cert.PolicyNode;

import static com.jmetertools.base.Constant.DEFAULT_CHARSET;


public class PostRequestFactory extends EntityHandler {

    /**
     * 获取{@link HttpPost}对象，没有参数设置
     * <p>方法重载，文字信息form表单提交，文件信息二进制流提交，具体参照文件上传的方法，post请求可以不需要参数，暂时不支持其他参数类型，如果是公参需要在url里面展示，需要传一个json对象，一般默认args为get公参，params为post请求参数</p>
     *
     * @param url url地址
     * @return HttpPost对象
     */
    public static HttpPost createRequest(String url) {
        return new HttpPost(url);
    }

    /**
     * 获取{@link HttpPost}对象，以form表单提交数据
     * <p>方法重载，文字信息form表单提交，文件信息二进制流提交，具体参照文件上传的方法主食，post请求可以不需要参数，暂时不支持其他参数类型，如果是公参需要在url里面展示，需要传一个json对象，一般默认args为get公参，params为post请求参数</p>
     * 请求header参数类型为{@link HttpClientConstant#ContentType_FORM}
     *
     * @param url    请求地址
     * @param params 请求数据，form表单形式设置请求实体
     * @return 返回post对象
     */
    public static HttpPost createRequest(String url, JSONObject params) {
        HttpPost httpPost = createRequest(url);
        if (params != null && !params.isEmpty()) setFormHttpEntity(httpPost, params);
        httpPost.addHeader(HttpClientConstant.ContentType_FORM);
        return httpPost;
    }

    /**
     * 获取{@link HttpPost}对象，{@link JSONObject}格式对象，传参时手动{@link JSONObject#toString()}方法,现在大多数情况下由 Ibase项目基础类完成
     * <p>新重载方法，适应{@link HttpPost}请求{@link JSONObject}传参，默认{@link Constant#DEFAULT_CHARSET}编码格式</p>
     * 请求header参数类型为{@link HttpClientConstant#ContentType_JSON}
     *
     * @param url 请求url
     * @param params 请求参数
     * @return post对象
     */
    public static HttpPost createRequest(String url, String params) {
        HttpPost httpPost = createRequest(url);
        if (StringUtils.isNotBlank(params))
            httpPost.setEntity(new StringEntity(params, DEFAULT_CHARSET.toString()));
        httpPost.addHeader(HttpClientConstant.ContentType_JSON);
        return httpPost;
    }

    /**
     * 获取 {@link HttpPost} 对象
     * <p>方法重载，文字信息{@link HttpClientConstant#ContentType_FORM}表单提交，文件信息二进制流提交，具体参照文件上传的方法主食，post请求可以不需要参数，暂时不支持其他参数类型，如果是公参需要在url里面展示，需要传一个{@link JSONObject}对象，一般默认args为{@link HttpGet}公参，params为{@link HttpPost}请求参数</p>
     *
     * @param url    请求地址
     * @param params 请求参数，其中二进制流必须是 file
     * @param file   文件
     * @return post对象
     */
    public static HttpPost createRequest(String url, JSONObject params, File file) {
        HttpPost httpPost = createRequest(url);
        if (params != null && !params.isEmpty()) setMultipartEntityEntity(httpPost, params, file);
        httpPost.addHeader(HttpClientConstant.ContentType_FORM);
        return httpPost;
    }


}
