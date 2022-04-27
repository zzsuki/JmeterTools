import com.jmetertools.cipher.AbCipher;
import com.jmetertools.cipher.PasswordCipher;
import org.junit.jupiter.api.Test;
import com.jmetertools.cipher.CipherFactory;
import org.junit.jupiter.api.Assertions;


public class TestPasswordCipher {
    @Test
    void testPasswordEncrypted(){
        String password = "Admin@123";
        AbCipher cipher = CipherFactory.createCipher("ump/password");
        Assertions.assertTrue(cipher instanceof PasswordCipher);
        int i = 1;
        // 测试1000次，防止出现高位未补齐
        while (i-- >0) {
            String encryptedPassword = cipher.encrypt(password);
            System.out.println(encryptedPassword);
            Assertions.assertEquals(64, encryptedPassword.length());
        }
    }
    @Test
    void testPasswordDecrypted(){
        String encryptedPass = "AD9FCB4D03B362B985E5A4B524683337172818E2E46DE49033D4CDA451A99057";
        AbCipher cipher = CipherFactory.createCipher("ump/password");
        String password = cipher.decrypt(encryptedPass);
        Assertions.assertEquals("Admin@123", password);
    }

}
