package com.jmetertools.httpclient;

import com.jmetertools.base.exceptions.ParamException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


/**
 * 请求枚举类
 */
enum RequestType {
    GET(HttpGet.METHOD_NAME), POST(HttpPost.METHOD_NAME), PUT(HttpPut.METHOD_NAME), DELETE(HttpDelete.METHOD_NAME), PATCH(HttpPatch.METHOD_NAME);

    static Logger logger = LogManager.getLogger(RequestType.class);
    String name;

    private RequestType(String name) {
        this.name = name;
    }

    /**
     * 获取请求类型
     * @param name 请求类型
     * @return 请求对象
     */
    static RequestType getInstance(String name) {
        logger.debug("验证请求方式：{}", name);
        if (StringUtils.isEmpty(name)) ParamException.fail("参数不能为空!");
        name = name.toUpperCase();
        switch (name) {
            case HttpGet.METHOD_NAME:
                return GET;
            case HttpPost.METHOD_NAME:
                return POST;
            case HttpPut.METHOD_NAME:
                return PUT;
            case HttpDelete.METHOD_NAME:
                return DELETE;
            case HttpPatch.METHOD_NAME:
                return PATCH;
            default:
                ParamException.fail("所选类型不支持!" + name);
        }
        return null;
    }
}
