package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.exceptions.RequestException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.RequestBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static com.jmetertools.base.Constant.EMPTY;

public class Request implements Serializable, Cloneable {
    private static final long serialVersionUID = -4153600036943378727L;
    private static Logger logger = LogManager.getLogger(Request.class);

    /**
     * 请求类型，true为get，false为post
     */
    RequestType requestType;

    /**
     * 请求对象
     */
    HttpRequestBase request;

    /**
     * host地址
     */
    String host = EMPTY;

    /**
     * 接口地址
     */
    String path = EMPTY;

    /**
     * 请求地址,如果为空则由host和path拼接
     */
    String uri = EMPTY;

    /**
     * header集合
     */
    List<Header> headers = new ArrayList<>();

    /**
     * get参数
     */
    JSONObject args = new JSONObject();

    /**
     * post参数,表单
     */
    JSONObject params = new JSONObject();

    /**
     * json参数,用于POST和put
     */
    JSONObject json = new JSONObject();

    /**
     * 响应,若没有这个参数, request对象转换成json对象时会自动调用getResponse方法
     */
    JSONObject response = new JSONObject();

    /**
     * 构造方法
     *
     * @param requestType 请求类型
     */
    private Request(RequestType requestType) {
        this.requestType = requestType;
    }

    /**
     * 获取get对象
     *
     * @return get请求
     */
    static Request isGet() {
        return new Request(RequestType.GET);
    }

    /**
     * 获取post对象
     *
     * @return post请求
     */
    static Request isPost() {
        return new Request(RequestType.POST);
    }

    /**
     * 获取put请求对象
     * @return put请求
     */
    static Request isPut() {
        return new Request(RequestType.PUT);
    }

    /**
     * 获取delete请求对象
     * @return delete请求
     */
    static Request isDelete() {
        return new Request(RequestType.DELETE);
    }

    /**
     * 设置host
     *
     * @param host host地址
     * @return 更新后的实例
     */
    Request setHost(String host) {
        this.host = host;
        return this;
    }

    /**
     * 设置接口地址
     *
     * @param path 接口地址
     * @return 更新后的实例
     */
    Request setPath(String path) {
        this.path = path;
        return this;
    }

    /**
     * 设置uri
     *
     * @param uri uri地址
     * @return 更新后的实例
     */
    Request setUri(String uri) {
        this.uri = uri;
        return this;
    }

    /**
     * 添加get参数
     *
     * @param key 参数名
     * @param value 参数值
     * @return 更新后的实例
     */
    Request addArgs(String key, Object value) {
        args.put(key, value);
        return this;
    }

    /**
     * 添加post参数
     *
     * @param key 参数名
     * @param value 参数值
     * @return 更新后的实例
     */
    Request addParam(String key, Object value) {
        params.put(key, value);
        return this;
    }

    /**
     * 添加json参数
     *
     * @param key 参数名
     * @param value 参数值
     * @return 更新后的实例
     */
    Request addJson(String key, Object value) {
        json.put(key, value);
        return this;
    }

    /**
     * 添加header
     *
     * @param key 参数名
     * @param value 参数值
     * @return 更新后的实例
     */
    Request addHeader(Object key, Object value) {
        headers.add(EntityHandler.getHeader(key.toString(), value.toString()));
        return this;
    }

    /**
     * 添加header
     *
     * @param header header对象
     * @return 更新后的实例
     */
    Request addHeader(Header header) {
        headers.add(header);
        return this;
    }

    /**
     * 批量添加header
     *
     * @param header header对象
     * @return 更新后的实例
     */
    Request addHeader(List<Header> header) {
        Iterator<Header> iterator = header.iterator();
        headers.addAll(header);
        return this;
    }

    /**
     * 增加header中cookies
     *
     * @param cookies json对象的cookie
     * @return 更新后的实例
     */
    Request addCookies(JSONObject cookies) {
        headers.add(EntityHandler.getCookies(cookies));
        return this;
    }

    Request addArgs(JSONObject args) {
        this.args.putAll(args);
        return this;
    }

    Request addParams(JSONObject params) {
        this.params.putAll(params);
        return this;
    }

    Request addJson(JSONObject json) {
        this.json.putAll(json);
        return this;
    }

//    /**
//     * 获取请求响应，兼容相关参数方法，不包括file
//     *
//     * @return
//     */
//    JSONObject getResponse() {
//        response = response.isEmpty() ? EntityHandler.getHttpResponse(request == null ? getRequest() : request) : response;
//        return response;
//    }

//    /**
//     * 从request base对象从初始化request
//     * @param base
//     * @return
//     */
//    static Request initFromRequest(HttpRequestBase base) {
//        Request request = null;
//        String method = base.getMethod();
//        String uri = base.getURI().toString();
//        RequestType requestType = RequestType.getInstance(method);
//        List<Header> headers = Arrays.asList(base.getAllHeaders());
//        if (requestType == RequestType.GET) {
//            request = isGet().setUri(uri).addHeader(headers);
//        } else if (requestType == RequestType.POST) {
//            HttpPost post = (HttpPost) base;
//            HttpEntity entity = post.getEntity();
//            if (entity == null) {
//                request = isPost().setUri(uri).addHeader(headers);
//            } else {
//                Header type = entity.getContentType();
//                String value = type == null ? EMPTY : type.getValue();
//                String content = EntityHandler.getContent(entity);
//                if (value.equalsIgnoreCase(HttpClientConstant.ContentType_TEXT.getValue()) || value.equalsIgnoreCase(HttpClientConstant.ContentType_JSON.getValue())) {
//                    request = isPost().setUri(uri).addHeader(headers).addJson(JSONObject.parseObject(content));
//                } else if (value.equalsIgnoreCase(HttpClientConstant.ContentType_FORM.getValue())) {
//                    request = isPost().setUri(uri).addHeader(headers).addParams(getJson(content.split("&")));
//                }
//            }
//        } else if (requestType == RequestType.PUT) {
//            HttpPut put = (HttpPut) base;
//            String content = EntityHandler.getContent(put.getEntity());
//            request = isPut().setUri(uri).addHeader(headers).setJson(JSONObject.parseObject(content));
//        } else if (requestType == RequestType.DELETE) {
//            request = isDelete().setUri(uri);
//        } else {
//            RequestException.fail("不支持的请求类型!");
//        }
//        return request;
//    }
}
