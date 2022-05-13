import com.alibaba.fastjson.JSONObject;
import com.jmetertools.utils.JsonUtil;
import groovy.json.JsonOutput;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import com.jmetertools.httpclient.ClientManager;
import com.jmetertools.httpclient.Request;

import java.io.IOException;


public class TestRequest {
    private static CloseableHttpClient client = ClientManager.httpClient;

    @Test
    void testPostRequest(){
        JSONObject params = new JSONObject();
        params.put("captcha", "1234");
        HttpPost httpPost = Request.getHttpPost("https://10.20.1.219/api/v1/user/captcha/", params);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            if (response != null) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject contentJson = JsonUtil.parseObject(content);
                Assertions.assertEquals("1234", contentJson.getString("captcha"));
                Assertions.assertEquals(200, response.getStatusLine().getStatusCode());
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
