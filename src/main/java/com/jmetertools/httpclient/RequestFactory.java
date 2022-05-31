package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import com.jmetertools.base.Constant;
import com.jmetertools.cipher.AbCipher;
import com.jmetertools.cipher.PasswordCipher;
import com.jmetertools.cipher.RangeTokenCipher;
import com.jmetertools.cipher.UmpTokenCipher;
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
import org.apache.http.message.BasicHttpRequest;
import com.jmetertools.httpclient.HttpClientConstant;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

import static com.jmetertools.base.Constant.DEFAULT_CHARSET;

public class RequestFactory {
    private static final Map<String, Object> cachedFactoryMap = new HashMap<>();

    static {
        cachedFactoryMap.put("get", new GetRequestFactory());
        cachedFactoryMap.put("put", new PutRequestFactory());
        cachedFactoryMap.put("post", new PostRequestFactory());
        cachedFactoryMap.put("delete", new PostRequestFactory());
    }

    public static Object createFactory(String method){
        if (method == null || method.isEmpty()){
            return null;
        }
        return cachedFactoryMap.get(method.toLowerCase());
    }


}
