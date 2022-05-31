package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.Constant;
import com.jmetertools.base.exceptions.FailException;
import com.jmetertools.utils.DecodeEncode;
import com.jmetertools.utils.TimeUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
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
import static com.jmetertools.base.exceptions.FailException.fail;
import static org.apache.commons.lang3.StringUtils.EMPTY;

public class EntityHandler {
    private static Logger logger = LogManager.getLogger(EntityHandler.class);

    /**
     * 是否需要处理响应头
     */
    public static boolean HEADER_HANDLE = false;

    /**
     * 打印日志的key
     */
    public static boolean LOG_KEY = true;

    /**
     * 把json数据转化为参数，为get请求和post请求string entity的时候使用
     *
     * @param argument 请求参数，json数据类型，map类型，可转化
     * @return 返回拼接参数后的地址
     */
    protected static String convertJsonToArguments(JSONObject argument) {
        return argument == null || argument.isEmpty() ? EMPTY : argument.keySet().stream().filter(x -> argument.get(x) != null).map(x -> x.toString() + EQUAL + DecodeEncode.encodeUrl(argument.getString(x.toString()))).collect(Collectors.joining("&", UNKNOW, EMPTY)).toString();
    }

    /**
     * 设置{@link HttpPost}接口上传表单，默认的编码格式
     * 默认编码格式{@link Constant#DEFAULT_CHARSET}
     *
     * @param request 请求对象
     * @param params  参数
     */
    protected static void setFormHttpEntity(HttpEntityEnclosingRequestBase request, JSONObject params) {
        List<NameValuePair> formedParams = new ArrayList<NameValuePair>();
        // 将params依次转化为value pair对象
        params.keySet().forEach(x -> formedParams.add(new BasicNameValuePair(x, params.getString(x))));
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formedParams, DEFAULT_CHARSET);
        request.setEntity(entity);
    }

    /**
     * 设置{@link HttpPost}接口json数据实体，默认的编码格式
     */
    protected static void setJsonHttpEntity(HttpEntityEnclosingRequestBase request, JSONObject params) {
        StringEntity stringEntity = new StringEntity(params.toString(), "UTF-8");
        stringEntity.setContentEncoding("UTF-8");
        request.setEntity(stringEntity);
    }

    /**
     * 设置二进制流实体，params 里面参数值为 {@link HttpClientConstant#FILE_UPLOAD_KEY}
     *
     * @param request 请求对象
     * @param params  请求参数
     * @param file    文件
     */
    protected static void setMultipartEntityEntity(HttpEntityEnclosingRequestBase request, JSONObject params, File file) {
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
     * 响应结束之后，处理响应头信息，如set-cookie内容
     *
     * @param response 响应内容
     * @return json对象
     */
    private static JSONObject afterResponse(CloseableHttpResponse response) {
        if (!HEADER_HANDLE) return null;
        Header[] allHeaders = response.getAllHeaders();
        JSONObject hs = new JSONObject();
        JSONObject cookie = new JSONObject();
        for (Header header : allHeaders) {
            // 如果发现set-cookie字段
            if (header.getName().equals(HttpClientConstant.SET_COOKIE)) {
                String[] split = header.getValue().split(EQUAL, 2);
                cookie.put(split[0], split[1]);
                continue;
            }
            hs.compute(header.getName(), (x, y) -> {
                if (y == null) {
                    return header.getValue();
                } else {
                    return hs.getString(header.getName()) + PART + header.getValue();
                }
            });
        }
        if (!cookie.isEmpty()) hs.put(HttpClientConstant.COOKIE, cookie);
        return hs;
    }

    /**
     * 根据解析好的content，转化{@link JSONObject}对象
     *
     * @param content 响应内容
     * @return 转化后的json响应对象
     */
    private static JSONObject getJsonResponse(String content, JSONObject headers) {
        JSONObject jo = new JSONObject();
        try {
            if (StringUtils.isBlank(content)) FailException.fail("响应为空!");
            jo = JSONObject.parseObject(content);
        } catch (JSONException e) {
            jo = new JSONObject() {{
                put(RESPONSE_CONTENT, content);
            }};
            logger.warn("响应体非json格式，已经自动转换成json格式！");
        }
        if (headers != null && !headers.isEmpty()) jo.put(HttpClientConstant.HEADERS, headers);
        return jo;
    }

    /**
     * 解析{@link HttpEntity},不区分请求还是响应
     *
     * @param entity 实体对象
     * @return content内容
     */
    public static String getContent(HttpEntity entity) {
        String content = EMPTY;
        try {
            if (entity != null) content = EntityUtils.toString(entity, DEFAULT_CHARSET);// 用string接收响应实体
            EntityUtils.consume(entity);// 消耗响应实体，并关闭相关资源占用
        } catch (Exception e) {
            logger.warn("解析响应实体异常！", e);
            fail();
        }
        return content;
    }

    /**
     * 生成header
     *
     * @param name header键名
     * @param value header内容
     * @return 返回header类型
     */
    public static Header getHeader(String name, String value) {
        return new BasicHeader(name, value);
    }

    /**
     * 通过json对象信息，生成cookie的header
     *
     * @param cookies cookie的json对象
     * @return header对象
     */
    public static Header getCookies(JSONObject cookies) {
        return getHeader(HttpClientConstant.COOKIE, cookies == null || cookies.isEmpty() ? EMPTY : cookies.keySet().stream().map(x -> x.toString() + EQUAL + cookies.get(x).toString()).collect(Collectors.joining(";")).toString());
    }

    /**
     * 获取响应状态，暂不处理{@link HttpStatus#SC_MOVED_TEMPORARILY}
     *
     * @param response
     * @param res
     * @return
     */
    public static int getStatus(CloseableHttpResponse response, JSONObject res) {
        int status = response.getStatusLine().getStatusCode();
//        if (status == HttpStatus.SC_MOVED_TEMPORARILY) {
//            res.put("location", response.getFirstHeader("Location").getValue());
//        }
        res.put(DEFAULT_STATUS_STRING, status);
        return status;
    }


//    /**
//     * 获取响应实体
//     * <p>会自动设置cookie，但是需要各个项目再自行实现cookie管理</p>
//     * <p>该方法只会处理文本信息，对于文件处理可以调用两个过期的方法解决</p>
//     *
//     * @param request 请求对象
//     * @return 返回json类型的对象
//     */
//    public static JSONObject getHttpResponse(HttpRequestBase request) {
//        JSONObject res = new JSONObject();
//        long start = 0L;
//        if (LOG_KEY) start = TimeUtil.getTimeStamp();
//        try (CloseableHttpResponse response = ClientManager.httpClient.execute(request)) {
//            res.putAll(getJsonResponse(getContent(response.getEntity()), afterResponse(response)));
//            int status = getStatus(response, res);
//            if (LOG_KEY)
//                logger.info("请求uri：{} , 耗时：{} ms , HTTP, code: {}", request.getURI(), TimeUtil.getTimeStamp() - start, status, res);
//        } catch (Exception e) {
//            Request request = Request.initFromRequest(request);
//            Request.setResponse(res);
//            logger.warn("请求失败 {} ,内容:{} ", e.getMessage(), Request.toString());
//        }
//        return res;
//    }



    /**
     * 获取一个百分比，两位小数
     *
     * @param total 总数
     * @param piece 成功数
     * @return 百分比
     */
    public static double getPercent(int total, int piece) {
        if (total == 0) return 0.00;
        int s = (int) (piece * 1.0 / total * 10000);
        return s * 1.0 / 100;
    }

}
