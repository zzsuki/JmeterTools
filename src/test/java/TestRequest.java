import com.alibaba.fastjson.JSONObject;
import com.jmetertools.httpclient.PostRequestFactory;
import com.jmetertools.utils.JsonUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.Test;
import org.testng.annotations.Ignore;
import org.testng.Assert;
import com.jmetertools.httpclient.ClientManager;

import java.io.IOException;


@Ignore("no env")
public class TestRequest {
    private static final CloseableHttpClient client = ClientManager.httpClient;

    @Test
    void testPostRequest(){
        JSONObject params = new JSONObject();
        params.put("captcha", "1234");
        HttpPost httpPost = PostRequestFactory.createRequest("https://10.20.1.219/api/v1/user/captcha/", params);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            if (response != null) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject contentJson = JsonUtil.parseObject(content);
                Assert.assertEquals("1234", contentJson.getString("captcha"));
                Assert.assertEquals(200, response.getStatusLine().getStatusCode());
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
