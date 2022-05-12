package com.jmetertools.httpclient;

import com.jmetertools.base.Constant;
import com.jmetertools.base.exceptions.FailException;
import com.jmetertools.utils.RandomUtil;
import com.jmetertools.utils.RegUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.MessageConstraints;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.DnsResolver;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.impl.conn.SystemDefaultDnsResolver;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.nio.reactor.ConnectingIOReactor;
import org.apache.http.nio.reactor.IOReactorException;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.charset.CodingErrorAction;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 *  连接池管理类
 */
public class ClientManager {
    final private static Logger logger = LogManager.getLogger(ClientManager.class);

    /**
     * 需要解析自定义域名的IP集合
     */
    public static List<InetAddress> ips = getAddress();

    /**
     * ssl验证
     */
    private static SSLContext sslContext = createIgnoreVerifySSL();

    /**
     * 本地DNS解析
     */
    private static DnsResolver dnsResolver = getDnsResolver();

    /**
     * 请求超时控制器
     */
    private static RequestConfig requestConfig = getRequestConfig();

    /**
     * 请求重试管理器
     */
    private static HttpRequestRetryHandler httpRequestRetryHandler = getHttpRequestRetryHandler();

    /**
     * 连接池
     */
    private static PoolingHttpClientConnectionManager connManager = getPool();

    /**
     * 异步连接池
     */
    private static PoolingNHttpClientConnectionManager connNManager = getNPool();

    /**
     * httpclient对象
     */
    public static CloseableHttpClient httpClient = getCloseableHttpClients();

    /**
     * 异步客户端对象
     */
    public static CloseableHttpAsyncClient httpAsyncClient = getCloseableHttpAsyncClient();

    /**
     * 获取连接池管理器
     *
     * @return  连接池管理器
     */
    private static PoolingHttpClientConnectionManager getPool() {
        // 采用绕过验证的方式处理https请求
        // 设置协议http和https对应的处理socket连接工厂的对象
        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create().register("http", PlainConnectionSocketFactory.INSTANCE).register("https", new SSLConnectionSocketFactory(sslContext, NoopHostnameVerifier.INSTANCE)).build();
        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry, dnsResolver);
        // 消息约束
        MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(HttpClientConstant.MAX_HEADER_COUNT).setMaxLineLength(HttpClientConstant.MAX_LINE_LENGTH).build();
        // 连接设置,一般不推荐自自定义，除非了解运行机制
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Constant.DEFAULT_CHARSET).setMessageConstraints(messageConstraints).build();
        connManager.setDefaultConnectionConfig(connectionConfig);
        // 设置最大连接数
        connManager.setMaxTotal(HttpClientConstant.MAX_TOTAL_CONNECTION);
        // 设置单路由上最大的连接数
        connManager.setDefaultMaxPerRoute(HttpClientConstant.MAX_PER_ROUTE_CONNECTION);
        return connManager;
    }

    /**
     * 获取异步连接池
     *
     * @return 连接池
     */
    private static PoolingNHttpClientConnectionManager getNPool() {
        IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(Runtime.getRuntime().availableProcessors()).setConnectTimeout(HttpClientConstant.CONNECT_REQUEST_TIMEOUT).setSoTimeout(HttpClientConstant.SOCKET_TIMEOUT).build();
        ConnectingIOReactor ioReactor = null;
        try {
            ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
        } catch (IOReactorException e) {
            logger.error("创建连接响应器失败!", e);
        }
        MessageConstraints messageConstraints = MessageConstraints.custom().setMaxHeaderCount(HttpClientConstant.MAX_HEADER_COUNT).setMaxLineLength(HttpClientConstant.MAX_LINE_LENGTH).build();
        PoolingNHttpClientConnectionManager connManager = new PoolingNHttpClientConnectionManager(ioReactor);
        ConnectionConfig connectionConfig = ConnectionConfig.custom().setMalformedInputAction(CodingErrorAction.IGNORE).setUnmappableInputAction(CodingErrorAction.IGNORE).setCharset(Constant.DEFAULT_CHARSET).setMessageConstraints(messageConstraints).build();
        connManager.setDefaultConnectionConfig(connectionConfig);
        connManager.setMaxTotal(HttpClientConstant.MAX_TOTAL_CONNECTION);
        connManager.setDefaultMaxPerRoute(HttpClientConstant.MAX_PER_ROUTE_CONNECTION);
        return connManager;
    }

    /**
     * 初始化DNS配置IP
     *
     * @return 地址
     */
    private static List<InetAddress> getAddress() {
        try {

            return Arrays.asList(
                    InetAddress.getByName("127.0.0.1"),
                    InetAddress.getByName("0.0.0.0")
            );
        } catch (Exception e) {
            FailException.fail("DNS IP解析失败!");
        }
        return null;
    }

    /**
     * 重写Java自定义DNS解析器,负载均衡，可以加入对指定host的单独处理
     *
     * @return 解析器对象
     */
    private static DnsResolver getDnsResolver() {
        return new SystemDefaultDnsResolver() {
            @Override
            public InetAddress[] resolve(final String host) throws UnknownHostException {
                if (host.equalsIgnoreCase("zzsuki")) {
                    InetAddress random = RandomUtil.getSample(ips);
                    logger.info(random);
                    return new InetAddress[]{random};
                } else {
                    return super.resolve(host);
                }
            }
        };
    }

    /**
     * 获取SSL套接字对象 重点重点：设置tls协议的版本
     *
     * @return ssl上下文对象
     */
    private static SSLContext createIgnoreVerifySSL() {
        SSLContext sslContext = null;// 创建套接字对象
        try {
            sslContext = SSLContext.getInstance(HttpClientConstant.SSL_VERSION);// 指定TLS版本
        } catch (NoSuchAlgorithmException e) {
            FailException.fail("创建套接字失败！" + e.getMessage());
        }
        // 实现X509TrustManager接口，用于绕过验证
        X509TrustManager trustManager = new X509TrustManager() {
            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
                                           String paramString) throws CertificateException {
            }

            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        try {
            sslContext.init(null, new TrustManager[]{trustManager}, null);// 初始化sslContext对象
        } catch (KeyManagementException e) {
            logger.warn("初始化套接字失败！", e);
        }
        return sslContext;
    }

    /**
     * 获取重试控制器
     *
     * @return 重试控制器
     */
    private static HttpRequestRetryHandler getHttpRequestRetryHandler() {
        return new HttpRequestRetryHandler() {
            public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
                // 使用内部方法进行判断
                boolean log = log(exception, executionCount, context);
                if (log) logger.warn("请求发生重试! 次数: {}", executionCount);
                return log;
            }

            /**绕一圈,记录重试信息,避免错误日志影响观感
             * @param exception 一场类型
             * @param executionCount 执行次数
             * @param context 请求上下文
             * @return 布尔值，是否需要重试
             */
            private boolean log(IOException exception, int executionCount, HttpContext context) {
                if (executionCount + 1 > HttpClientConstant.TRY_TIMES) return false;
                logger.warn("请求发生错误:{}", exception.getMessage());
                HttpClientContext clientContext = HttpClientContext.adapt(context);
                final Object request = clientContext.getAttribute(HttpCoreContext.HTTP_REQUEST);
                if (request instanceof HttpUriRequest) {
                    HttpUriRequest uriRequest = (HttpUriRequest) request;
                    logger.warn("请求失败接口URI:{}", uriRequest.getURI().toString());
                }
                if (exception instanceof NoHttpResponseException) {
                    // 请求已发送但没收到响应。一般是服务器负载过高，导致收到请求了，但没有资源处理，于是会直接不给客户端回复了
                    return true;
                } else if (exception instanceof InterruptedIOException) {
                    // 传输异常，可能和网络有关，一般可以自恢复
                    return true;
                } else if (exception instanceof UnknownHostException) {
                    // 域名未正常解析，不需要重试，因为短期内解决不了
                    return false;
                } else if (exception instanceof SSLException) {
                    // 证书错误，短期内无法解决
                    return false;
                } else if (exception instanceof SocketException) {
                    // 一般是连接异常中断
                    return false;
                } else {
                    logger.warn("未记录的请求异常:{}", exception.getClass().getName());
                }
                // 如果请求是幂等的，则不重试,HttpEntityEnclosingRequest类以及子类都是非幂等性的
                return request instanceof HttpEntityEnclosingRequest;
            }
        };
    }

    /**
     * 通过连接池获取http协议的异步客户端对象
     * <p>
     * 增加默认的请求控制器，和请求配置，连接控制器，取消了cookie store，单独解析响应set-cookie和发送请求的header，适配多用户同时在线的情况
     * </p>
     *
     * @return 客户端
     */
    private static CloseableHttpAsyncClient getCloseableHttpAsyncClient() {
        return HttpAsyncClients.custom().setConnectionManager(connNManager).setSSLHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER).setSSLContext(sslContext).build();
    }

    /**
     * 获取httpclient对象，获取到同步对象
     * <p>
     * 增加默认的请求控制器，和请求配置，连接控制器，取消了cookie store，单独解析响应set-cookie和发送请求的header，适配多用户同时在线的情况
     * </p>
     *
     * @return 客户端对象
     */
    private static CloseableHttpClient getCloseableHttpClients() {
        return HttpClients.custom().setConnectionManager(connManager).setRetryHandler(httpRequestRetryHandler).setDefaultRequestConfig(requestConfig).build();
    }

    /**
     * 获取请求配置对象
     * <p>
     * cookieSpec:即cookie策略。参数为cookie specs的一些字段。作用：
     * 1、如果网站header中有set-cookie字段时，采用默认方式可能会被cookie reject，无法写入cookie。将此属性设置成CookieSpecs.STANDARD_STRICT可避免此情况。
     * 2、如果要想忽略cookie访问，则将此属性设置成CookieSpecs.IGNORE_COOKIES。
     * </p>
     *
     * @return 请求配置对象
     */
    private static RequestConfig getRequestConfig() {
        return RequestConfig.custom().setConnectionRequestTimeout(HttpClientConstant.CONNECT_REQUEST_TIMEOUT).setConnectTimeout(HttpClientConstant.CONNECT_TIMEOUT).setSocketTimeout(HttpClientConstant.SOCKET_TIMEOUT).setCookieSpec(CookieSpecs.STANDARD_STRICT).setRedirectsEnabled(false).build();
    }

    /**
     * 获取包含代理配置的请求配置
     *
     * @param ip 代理地址
     * @param port 端口
     * @return 特定配置对象
     */
    public static RequestConfig getProxyRequestConfig(String ip, int port) {
        return RequestConfig.custom().setConnectionRequestTimeout(HttpClientConstant.CONNECT_REQUEST_TIMEOUT).setConnectTimeout(HttpClientConstant.CONNECT_TIMEOUT).setSocketTimeout(HttpClientConstant.SOCKET_TIMEOUT).setCookieSpec(CookieSpecs.IGNORE_COOKIES).setRedirectsEnabled(false).setProxy(new HttpHost(ip, port)).build();
    }

    /**
     * 请求拦截器
     * 暂时只留一个日志，不做多余处理，算作埋点
     *
     * @return 请求拦截对象
     */
    public static HttpRequestInterceptor getHttpRequestInterceptor() {
        return new HttpRequestInterceptor() {
            @Override
            public void process(HttpRequest httpRequest, HttpContext httpContext) throws HttpException, IOException {
                logger.debug("请求拦截器成功!");
            }
        };
    }


    /**
     * 响应拦截器
     * 暂时只留一个日志，不做多余处理，算作埋点
     *
     * @return 响应拦截对象
     */
    public static HttpResponseInterceptor getHttpResponseInterceptor() {
        return new HttpResponseInterceptor() {
            @Override
            public void process(HttpResponse httpResponse, HttpContext httpContext) throws HttpException, IOException {
                logger.debug("响应拦截器成功!");
            }
        };
    }

    /**
     * 回收资源方法，关闭过期连接，关闭超时连接，用于另起线程回收连接池连接
     */
    public static void recyclingConnection() {
        connManager.closeExpiredConnections();
        connManager.closeIdleConnections(HttpClientConstant.IDLE_TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 启动异步请求客户端
     */
    public static synchronized void startAsync() {
        if (!httpAsyncClient.isRunning())
            httpAsyncClient.start();
    }

    /**
     * [重新] 初始化连接池,用于临时改变超时和超时标准线的重置
     * <p>
     * 会重置请求控制器,重置连接池和重试控制器
     * </p>
     * 时间单位s,默认配置单位ms,自动乘以1000
     *
     * @param timeout 最新的timeout，单位s
     * @param acceptTime tcp accept时间
     * @param retryTimes 默认重试次数
     * @param ip 路由ip
     * @param port 理由端口
     */
    public static void init(int timeout, int acceptTime, int retryTimes, String ip, int port) {
        HttpClientConstant.CONNECT_REQUEST_TIMEOUT = timeout * 1000;
        HttpClientConstant.CONNECT_TIMEOUT = timeout * 1000;
        HttpClientConstant.SOCKET_TIMEOUT = timeout * 1000;
        HttpClientConstant.MAX_ACCEPT_TIME = acceptTime * 1000;
        HttpClientConstant.TRY_TIMES = retryTimes < 1 ? Constant.TEST_ERROR_CODE : retryTimes;
        requestConfig = StringUtils.isNoneBlank(ip) && RegUtil.isFullMatch(ip + ":" + port, Constant.HOST_REGEX) ? getProxyRequestConfig(ip, port) : getRequestConfig();
        httpClient = getCloseableHttpClients();
        httpRequestRetryHandler =   getHttpRequestRetryHandler();
    }


}
