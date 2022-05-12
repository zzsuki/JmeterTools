import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.jmetertools.httpclient.ClientManager;

import java.util.Timer;
import java.util.TimerTask;

public class TestHttpClientPooling extends BaseHttpClientTest {
    private static CloseableHttpClient client = ClientManager.httpClient;;

    @Before
    public void before() {
//        initHttpClient();
    }

    @Test
    public void test() throws Exception {
        startUpAllThreads(getRunThreads(new HttpThread()));
        // 等待线程运行
    }

    private class HttpThread implements Runnable {

        @Override
        public void run() {
            HttpGet httpGet = new HttpGet("https://www.google.com/");
            // 长连接标识，不加也没事，HTTP1.1默认都是Connection: keep-alive的
            httpGet.addHeader("Connection", "keep-alive");

            long startTime = System.currentTimeMillis();
            try {
                CloseableHttpResponse response = null;
                if (client != null) {
                    response = client.execute(httpGet);
                }
                if (response != null) {
                    response.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                addCost(System.currentTimeMillis() - startTime);

                if (NOW_COUNT.incrementAndGet() == REQUEST_COUNT) {
                    System.out.println(EVERY_REQ_COST.toString());
                }
            }
        }

    }


}
