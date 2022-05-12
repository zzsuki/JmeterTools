package com.jmetertools.httpclient;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import static com.jmetertools.base.Constant.DEFAULT_CHARSET;

public class HttpClientConstant {
    /**
     * 空闲连接定义时间，超过该值会认为是空闲连接
     */
    public static long IDLE_TIMEOUT = 15;
    /**
     * 错误重试次数，不推荐设置为过高或过低的，影响都很大，过高的话会导致资源池被无效请求占用，过低的话对一些网络问题就比较难自恢复
     */
    public static int TRY_TIMES = 3;
    /**
     * 从连接目标url最大超时 单位：毫秒
     */
    public static int CONNECT_REQUEST_TIMEOUT = 10 * 1000;

    /**
     * 连接池中获取可用连接最大超时时间 单位：毫秒
     */
    public static int CONNECT_TIMEOUT = CONNECT_REQUEST_TIMEOUT;

    /**
     * 等待响应（读数据）最大超时 单位：毫秒
     */
    public static int SOCKET_TIMEOUT = CONNECT_REQUEST_TIMEOUT;

    /**
     * 记录
     */
    public static int MAX_ACCEPT_TIME = 5 * 1000;

    /**
     * 连接池最大连接数
     */
    public static int MAX_TOTAL_CONNECTION = 20000;

    /**
     * 每个路由最大连接数
     */
    public static int MAX_PER_ROUTE_CONNECTION = 5000;

    /**
     * 最大header数
     */
    public static int MAX_HEADER_COUNT = 100;

    /**
     * 消息最大长度
     */
    public static int MAX_LINE_LENGTH = 10000;

    /**
     * 处理文件参数时候的关键字
     */
    public static final String FILE_UPLOAD_KEY = "file";

    /**
     * 连接header设置,这个会默认传输
     */
//    public static Header CONNECTION = getHeader("Connection", getProperty("Connection"));

    public static Header ContentType_JSON = getHeader("Content-Type", "application/json; charset=" + DEFAULT_CHARSET.toString());

    public static Header ContentType_FORM = getHeader("Content-Type", "application/x-www-form-urlencoded; charset=" + DEFAULT_CHARSET.toString());

    public static Header ContentType_TEXT = getHeader("Content-Type", "text/plain; charset=" + DEFAULT_CHARSET.toString());

    public static Header X_Requested_KWith = getHeader("X-Requested-With", "XMLHttpRequest");

    /**
     * 在设置请求content type参数，表示请求以io流发送数据
     */
    public static String CONTENT_TYPE_MULTIPART_FORM = "multipart/form-data";

    /**
     * 在设置请求content type参数，表示请求以文本发送数据
     */
    public static String CONTENT_TYPE_TEXT = "text/plain";

    /**
     * 生成header
     *
     * @param name key
     * @param value value
     * @return header
     */
    public static Header getHeader(String name, String value) {
        return new BasicHeader(name, value);
    }

    public static String SSL_VERSION = "TLSv1.2";

    public static String CONNECTION = "keep-alive";

    /**
     * 请求头，cookie
     */
    public static String COOKIE = "cookie";

    public static String HEADERS = "Headers";

    public static String SET_COOKIE = "Set-Cookie";

}
