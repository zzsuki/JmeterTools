import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.jmetertools.utils.JsonUtil;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.Assert;
import java.math.BigDecimal;


public class TestJsonUtil {


    @Test(dataProvider = "getFirstValueJson")
    void testGetFirstValueJson(String key, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object jb = JsonUtil.parse(jsonString);
        Object value = JsonUtil.getValueByKey(jb, key);
        System.out.println(value);
        System.out.println(value.getClass());
        Assert.assertEquals(value, expected);
    }

    @DataProvider(name = "getFirstValueJson")
    public static Object[][] getFirstValueJsonData() {
        return new Object[][] {{"name", "三国演义"}, {"price", "18.8"}};
    }

    @Test(dataProvider = "getObjectValueByJsonPath")
    void testGetObjectValueByJsonPath(String jsonPath, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object jb = JsonUtil.parse(jsonString);
        Object value = JsonUtil.getValueByJsonPath(jb, jsonPath);

        Assert.assertEquals(value == null? "null": value.toString(), expected);
    }

    @DataProvider(name = "getObjectValueByJsonPath")
    public static Object[][] getObjectValueByJsonPathData(){
        return new Object[][] {
                {"$.books[0].name", "三国演义"},
                {"$.books[1].name", "水浒传"},
                {"$.books[1].value", "null"},
                {"$.books[1].price", "19.9"},
        };
    }

    @Test(dataProvider = "getStringValueByJsonPath")
    void testGetStringValueByJsonPath(String jsonPath, String expected){
        String jsonString = "{\"books\": [{\"name\": \"三国演义\", \"price\": 18.8}, {\"name\": \"水浒传\", \"price\": 19.9}]}";
        Object value = JsonUtil.getValueByJsonPath(jsonString, jsonPath);
        System.out.println(value);
        System.out.println(value.getClass());
        Assert.assertEquals(expected, value);
    }

    @DataProvider(name = "getStringValueByJsonPath")
    public static Object[][] getStringValueByJsonPathData(){
        return new Object[][] {
                {"$.books[0].name", "三国演义"},
                {"$.books[1].name", "水浒传"},
        };
    }

}
