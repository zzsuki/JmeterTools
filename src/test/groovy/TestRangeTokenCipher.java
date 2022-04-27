import com.jmetertools.cipher.RangeTokenCipher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;


public class TestRangeTokenCipher {
    @Test
    void testRangeTokenCipher(){
        String token = "aff8477a8e07308cb794dd712ca0e363d01f2bf87137fd83feb39c8457839265e508fcae5523584a12a7798fc2df9ff6147b7c4598972e3f94668a36197677ab";
        RangeTokenCipher cipher = new RangeTokenCipher();
        int i = 1;
        // 测试1000次，防止出现高位未补齐
        while (i-- >0) {
            String chaosToken = cipher.decrypt(token);
            System.out.println(chaosToken);
            String newToken = cipher.encrypt(chaosToken);
            System.out.println(newToken);
            String newChaosToken = cipher.decrypt(newToken);
            Assertions.assertEquals(chaosToken, newChaosToken);
        }
    }
}
