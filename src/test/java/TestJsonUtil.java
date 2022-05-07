import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import com.jmetertools.utils.JsonUtil;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

public class TestJsonUtil {
    @ParameterizedTest
    @CsvSource(
            value = {
                    "name,三国演义",
                    "price,18.8"
            },
            emptyValue = ""
    )
    void testGetFirstValueJson(String key, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object jb = JsonUtil.parse(jsonString);
        Object value = JsonUtil.getValueByKey(jb, key);
        System.out.println(value);
        System.out.println(value.getClass());
        Assertions.assertEquals(expected, value);
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "$.books[0].name,三国演义",
                    "$.books[1].name,水浒传",
                    "$.books[1].value, null",
                    "$.books[1].price, 19.9",
            },
            nullValues={"null"},
            emptyValue = ""
    )
    void testGetObjectValueByJsonPath(String jsonPath, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object jb = JsonUtil.parse(jsonString);
        Object value = JsonUtil.getValueByJsonPath(jb, jsonPath);

        Assertions.assertEquals(expected,  value == null? null: value.toString());
    }

    @ParameterizedTest
    @CsvSource(
            value = {
                    "$.books[0].name,三国演义",
                    "$.books[1].name,水浒传",
            },
            emptyValue = ""
    )
    void testGetStringValueByJsonPath(String jsonPath, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object value = JsonUtil.getValueByJsonPath(jsonString, jsonPath);
        System.out.println(value);
        System.out.println(value.getClass());
        Assertions.assertEquals(expected, value);
    }

}
