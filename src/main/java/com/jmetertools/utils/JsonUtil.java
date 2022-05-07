package com.jmetertools.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.jmetertools.base.exceptions.FailException;

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
     * 根据key获取对应的值，只会返回第一个匹配到的值；且只返回符合目标类型的值；例如：
     * 当存在{key: 123, key: "abc"}时，如果传入的targetClass是String,则只返回abc
     * @param object 待查找对象
     * @param key 待查找的key
     * @param targetClass key对应的类
     * @param <T> 泛型类型
     * @return targetClass的实例
     */
    public static <T> T getValueByKey(Object object, String key, Class<T> targetClass) {
        // object 状态判断，为空或为string类型则返回null
        if (object == null || object.getClass() == String.class) {
            return null;
        }
        // 两种情况，可能是Object 或 array对象
        // 1. 如果是Object对象
        if (object.getClass() == JSONObject.class) {
            JSONObject jb = (JSONObject) object;
            // 如果object包含key对应的类型
            if (jb.containsKey(key) && targetClass.isInstance(jb.getObject(key, targetClass))) {
                return jb.getObject(key, targetClass);
            }
            // object中不包含key,则需要遍历object中的元素
            for (Object o : jb.values()) {
                T tmp = getValueByKey(o, key, targetClass);
                if (tmp != null) return tmp;
            }
        // 2. 如果是array对象
        } else if (object.getClass() == JSONArray.class) {
            JSONArray ja = (JSONArray) object;
            for (Object o : ja) {
                T tmp = getValueByKey(o, key, targetClass);
                if (tmp != null) return tmp;
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * 根据key获取对应的值，只会返回第一个匹配到的值
     * @param object 待查找对象
     * @param key 待查找的key
     * @return 将值以字符串形式返回，object为空或null时，返回null
     */
    public static String getValueByKey(Object object, String key) {
        // object 状态判断，为空或为string类型则返回null
        if (object == null || object.getClass() == String.class) {
            return null;
        }
        // 两种情况，可能是Object 或 array对象
        // 1. 如果是Object对象
        if (object.getClass() == JSONObject.class) {
            JSONObject jb = (JSONObject) object;
            // 如果object包含key对应的类型
            if (jb.containsKey(key)) {
                return jb.getString(key);
            }
            // object中不包含key,则需要遍历object中的元素
            for (Object o : jb.values()) {
                String tmp = getValueByKey(o, key);
                if (tmp != null) return tmp;
            }
            // 2. 如果是array对象
        } else if (object.getClass() == JSONArray.class) {
            JSONArray ja = (JSONArray) object;
            for (Object o : ja) {
                String tmp = getValueByKey(o, key);
                if (tmp != null) return tmp;
            }
        } else {
            return null;
        }
        return null;
    }

    /**
     * 使用json path获取json对象中的指定值
     * @param object 待查找对象
     * @param jsonPath json path表达式
     * @return 查找结果
     */
    public static Object getValueByJsonPath(Object object, String jsonPath){
        return JSONPath.eval(object, jsonPath);
    }

    /**
     * 使用json path获取json字符串中的指定值
     * @param object 待查找的json 字符串
     * @param jsonPath json path表达式
     * @return 查找结果
     */
    public static Object getValueByJsonPath(String object, String jsonPath){
        return JSONPath.read(object, jsonPath);
    }

    /**
     * 从JsonArray中获取指定key的所有value集合
     * @param jsonArray 待查找的Json Array
     * @param key 需要查找的键
     * @return 字符串集合
     */
    public static Set<String> getStringSetWithKey(JSONArray jsonArray, String key){
        Set<String> setToReturn = new HashSet<>();
        jsonArray.forEach(o -> {
            JSONObject jo = (JSONObject) o;
            setToReturn.add(jo.getString(key));
        });
        return setToReturn;
    }

    /**
     * 从JsonArray中获取指定key的所有value集合
     * @param jsonArray 待查找的Json Array
     * @param key 需要查找的键
     * @return 整数集合
     */
    public static Set<Integer> getIntegerSetWithKey(JSONArray jsonArray, String key){
        Set<Integer> setToReturn = new HashSet<>();
        jsonArray.forEach(o -> {
            JSONObject jo = (JSONObject) o;
            setToReturn.add(jo.getInteger(key));
        });
        return setToReturn;
    }


    /**
     * 反序列化json
     * @param jsonString: json字符串
     * @return json object或json array
     */
    public static Object parse(String jsonString){
        return JSON.parse(jsonString);
    }

    /**
     * 将json字符串转换为json对象
     * @param jsonString： json字符串
     * @return json对象
     */
    public static JSONObject parseObject(String jsonString){
        return JSON.parseObject(jsonString);
    }

    /**
     * 反序列化数组
     * @param jsonString json字符串
     * @return json数组
     */
    public static JSONArray parseArray(String jsonString){
        return JSON.parseArray(jsonString);
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
    public <T> Object parseJavaBean(String jsonString, Class<T> className){
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
