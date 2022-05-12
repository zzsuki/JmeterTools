package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.Constant;
import com.jmetertools.utils.DecodeEncode;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static com.jmetertools.base.Constant.*;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class Request {
    private static Logger logger = LogManager.getLogger(Request.class);

    /**
     * 方法已重载，获取{@link HttpGet}对象
     * <p>方法重载，主要区别参数，会自动进行urlencode操作</p>
     *
     * @param url  表示请求地址
     * @param args 表示传入数据
     * @return 返回get对象
     */
    public static HttpGet getHttpGet(String url, JSONObject args) {
        if (args == null || args.isEmpty()) return getHttpGet(url);
        String uri = url + convertJsonToArguments(args);
        return getHttpGet(uri);
    }

    /**
     * 方法已重载，获取{@link HttpGet}对象
     * <p>方法重载，主要区别参数，会自动进行url encode操作</p>
     *
     * @param url 表示请求地址
     * @return 返回get对象
     */
    public static HttpGet getHttpGet(String url){
        return new HttpGet(url);
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
    public static HttpPost getHttpPost(String url, JSONObject params) {
        HttpPost httpPost = getHttpPost(url);
        if (params != null && !params.isEmpty()) setFormHttpEntity(httpPost, params);
        httpPost.addHeader(HttpClientConstant.ContentType_FORM);
        return httpPost;
    }

    /**
     * 获取{@link HttpPost}对象，没有参数设置
     * <p>方法重载，文字信息form表单提交，文件信息二进制流提交，具体参照文件上传的方法，post请求可以不需要参数，暂时不支持其他参数类型，如果是公参需要在url里面展示，需要传一个json对象，一般默认args为get公参，params为post请求参数</p>
     *
     * @param url url地址
     * @return HttpPost对象
     */
    public static HttpPost getHttpPost(String url) {
        return new HttpPost(url);
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
    public static HttpPost getHttpPost(String url, String params) {
        HttpPost httpPost = getHttpPost(url);
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
    public static HttpPost getHttpPost(String url, JSONObject params, File file) {
        HttpPost httpPost = getHttpPost(url);
        if (params != null && !params.isEmpty()) setMultipartEntityEntity(httpPost, params, file);
        httpPost.addHeader(HttpClientConstant.ContentType_FORM);
        return httpPost;
    }

    /**
     * 设置二进制流实体，params 里面参数值为 {@link HttpClientConstant#FILE_UPLOAD_KEY}
     *
     * @param request 请求对象
     * @param params  请求参数
     * @param file    文件
     */
    private static void setMultipartEntityEntity(HttpEntityEnclosingRequestBase request, JSONObject params, File file) {
        logger.debug("上传文件名：{}", file.getAbsolutePath());
        String fileName = file.getName();
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            logger.warn("读取文件失败！", e);
        }
        Iterator<String> keys = params.keySet().iterator();// 遍历 params 参数和值
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();// 新建MultipartEntityBuilder对象
        while (keys.hasNext()) {
            String key = keys.next();
            String value = params.getString(key);
            if (value.equalsIgnoreCase(HttpClientConstant.FILE_UPLOAD_KEY)) {
                builder.addBinaryBody(key, inputStream, ContentType.create(HttpClientConstant.CONTENT_TYPE_MULTIPART_FORM), fileName);// 设置流参数
            } else {
                StringBody body = new StringBody(value, ContentType.create(HttpClientConstant.CONTENT_TYPE_TEXT, DEFAULT_CHARSET));// 设置普通参数
                builder.addPart(key, body);
            }
        }
        HttpEntity entity = builder.build();
        request.setEntity(entity);
    }

    /**
     * 设置{@link HttpPost}接口上传表单，默认的编码格式
     * 默认编码格式{@link Constant#DEFAULT_CHARSET}
     *
     * @param request 请求对象
     * @param params  参数
     */
    private static void setFormHttpEntity(HttpEntityEnclosingRequestBase request, JSONObject params) {
        List<NameValuePair> formedParams = new ArrayList<NameValuePair>();
        // 将params依次转化为value pair对象
        params.keySet().forEach(x -> formedParams.add(new BasicNameValuePair(x, params.getString(x))));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formedParams, DEFAULT_CHARSET);
        request.setEntity(entity);
    }

    /**
     * 把json数据转化为参数，为get请求和post请求string entity的时候使用
     *
     * @param argument 请求参数，json数据类型，map类型，可转化
     * @return 返回拼接参数后的地址
     */
    public static String convertJsonToArguments(JSONObject argument) {
        return argument == null || argument.isEmpty() ? EMPTY : argument.keySet().stream().filter(x -> argument.get(x) != null).map(x -> x.toString() + EQUAL + DecodeEncode.encodeUrl(argument.getString(x.toString()))).collect(Collectors.joining("&", UNKNOW, EMPTY)).toString();
    }

}
