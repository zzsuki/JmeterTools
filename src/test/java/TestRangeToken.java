import com.alibaba.fastjson.JSONObject;
import com.jmetertools.httpclient.GetRequestFactory;
import com.jmetertools.httpclient.PostRequestFactory;
import com.jmetertools.utils.JsonUtil;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import org.testng.Assert;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Ignore;

import com.jmetertools.cipher.AbCipher;
import com.jmetertools.cipher.CipherFactory;
import com.jmetertools.httpclient.ClientManager;
import com.jmetertools.httpclient.RequestFactory;

import java.io.IOException;


@Ignore("no env")
public class TestRangeToken {
    private static final Logger logger =  LogManager.getLogger(TestRangeToken.class);

    private static String decryptedToken = null;
    private static final CloseableHttpClient httpClient = ClientManager.httpClient;
    private static final AbCipher tokenCipher = CipherFactory.createCipher("range/token");

    @Test
    void testUserInfoList(){
        JSONObject params = new JSONObject();
        params.put("is_active", "");
        params.put("info_complete", "");
        params.put("role", "");
        params.put("role_is_null", "");
        params.put("search", "");
        params.put("ordering", "");
        params.put("page", "");
        params.put("page_size", "");
        HttpGet httpGet = GetRequestFactory.createRequest("https://ctf.bolean.com.cn/api/v1/system/auth/user/", params);
        String token = tokenCipher.encrypt(decryptedToken);
        httpGet.setHeader("Authorization", "Token " + token);
        CloseableHttpResponse response = null;
        try{
            response = httpClient.execute(httpGet);
            if (response != null){
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.info(String.format("content: %s\n", content));
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @BeforeMethod
    static void doLogin(){
        JSONObject params = new JSONObject();
        params.put("username", "zzsuki");
        params.put("password", CipherFactory.createCipher("range/password").encrypt("Admin@123"));
        formCaptchaCode();
        params.put("captcha", "1234");
        logger.info(String.format("params is : %s\n", params.toString()));
        HttpPost httpPost = PostRequestFactory.createRequest("https://ctf.bolean.com.cn/api/v1/user/login/", params.toString());
        CloseableHttpResponse response = null;
        try{
            response = httpClient.execute(httpPost);
            if (response != null){
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                logger.info(String.format("Response Code: %d\n", response.getStatusLine().getStatusCode()));
                logger.info(String.format("content is : %s\n", content));
                JSONObject jo = JSONObject.parseObject(content);
                String token = jo.getString("token");
                logger.info(String.format("First Token: %s\n", token));
                decryptedToken = tokenCipher.decrypt(token);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    static void formCaptchaCode(){
        JSONObject params = new JSONObject();
        params.put("captcha", "1234");
        HttpPost httpPost = PostRequestFactory.createRequest("https://ctf.bolean.com.cn/api/v1/user/captcha/", params.toString());
        CloseableHttpResponse response = null;
        try {
            response = httpClient.execute(httpPost);
            if (response != null) {
                String content = EntityUtils.toString(response.getEntity(), "UTF-8");
                JSONObject contentJson = JsonUtil.parseObject(content);
                response.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
