import com.jmetertools.cipher.UmpTokenCipher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class TestUmpTokenCipher {
    @Test
    void testUmpTokenCipher(){
        String token = "af4485c5c22a8f1e72de02c0c724091b49b24bdfa4d65b72e1c253de70a8f3349fde5d45485554669796799766ddc8c5";
        UmpTokenCipher cipher = new UmpTokenCipher();
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
