package com.jmetertools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashSet;
import java.util.Set;

/**
 * 用于处理json的工具类。根据fastjson官方最佳实践文档，使用fastjson时有以下地方需要注意
 * 1. 应尽量遵循Java Beans规范与JSON规范(即序列化的对象)
 * 2. 使用正常的key，json中的key，应使用符合Java的class或property命名规范的key，这样会减少不必要的冲突
 * 3. 尽量使用标准的日期格式。或者序列化和反序列化里都是用同样的datePattern格式
 * 4. 对于新手来说，自定义序列化是一切罪恶的根源。
 * 5. JSONObject是JSON字符串与pojo对象转换过程中的中间表达类型，实现了Map接口;如果JSONObject与正常的POJO混用，出现问题的概率较高。
 * 6. json string中尽量不要嵌套json string
 * 7. json string转POJO（或POJO转json string），尽量使用直接转的方式，而不是先转成JSONObject过渡的方式。
 *      特别是对于Fastjson，由于性能优化的考虑，这两个执行的代码是不一样的，可能导致不一样的结果。
 * 8. 尽量不要在使用过多的层次嵌套的同时使用泛型（List、Map等），可能导致类型丢失，而且问题比较难查。
 */
public class JsonUtil {
    /**
     * 从JsonArray中获取指定key的所有value集合
     * @param jsonArray 待查找的Json Array
     * @param key 需要查找的键
     * @return 字符串集合
     */
    public static Set<String> getValueSetWithKey(JSONArray jsonArray, String key){
        Set<String> setToReturn = new HashSet<>();
        jsonArray.forEach(o -> {
            JSONObject jo = (JSONObject) o;
            setToReturn.add(jo.getString(key));
        });
        return setToReturn;
    }

    /**
     * 将json字符串转换为json对象
     * @param jsonString： json字符串
     * @return json对象
     */
    public static JSONObject parseJsonObject(String jsonString){
        return JSON.parseObject(jsonString);
    }

    /**
     * 将json对象转换为字符串
     * @param jsonObject json对象
     * @return 字符串
     */
    public static String toString(JSONObject jsonObject){
        return jsonObject.toString();
    }

    /**
     * 将json string映射为指定类的实例
     * @param jsonString json字符串
     * @param className 需要映射的类
     * @return 类的实例对象
     */
    public <T> Object parseJavaObject(String jsonString, Class<T> className){
        return JSON.parseObject(jsonString, className);
    }

    /**
     * 序列化指定对象
     * @param object 待序列化的对象
     * @return 字符串
     */
    public static <T> String toString(T object){
        return JSON.toJSONString(object);
    }

    /**
     * 序列化指定对象
     * @param object 待序列化的对象
     * @param feature 序列化特性，可以对序列化后的字符串进行自定义格式配置。eg: QuoteFieldNames可使key使用引号; UseSingleQuotes可使用单引号
     * @return 字符串
     */
    public static <T> String toString(T object, SerializerFeature feature){
        return JSON.toJSONString(object, feature);
    }

}
