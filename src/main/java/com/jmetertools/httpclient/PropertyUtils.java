package com.jmetertools.httpclient;

import com.alibaba.fastjson.JSONObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class PropertyUtils {
    private static Logger logger = LogManager.getLogger(PropertyUtils.class);

    public static class Property{
        Map<String, String> properties = new HashMap<>();

        Property(ResourceBundle resourceBundle){
            Set<String> set = resourceBundle.keySet();
            for(String key: set){
                properties.put(key, resourceBundle.getString(key));
            }
        }

        public String getProperty(String key){
            if (properties.containsKey(key)) return properties.get(key);
            return null;
        }

    }
}
