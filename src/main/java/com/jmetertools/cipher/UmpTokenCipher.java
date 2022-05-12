package com.jmetertools.cipher;


import com.jmetertools.base.Constant;
import com.jmetertools.utils.RandomUtil;


public class UmpTokenCipher extends AbCipher{

    /**
     * 构造方法
     */
    public UmpTokenCipher() {
        super(Constant.UMP_KEY, Constant.ENCRYPT_UMP_TOKEN_LEN);
    }

    @Override
    public String encrypt(String word) {
        String salt = String.join("", RandomUtil.getSample(Constant.BROAD_CHAR_SET.get(), 8));
        String withSalt = word.substring(0, 10) +
                salt.substring(0, 2) +
                word.substring(10, 20) +
                salt.substring(2, 4) +
                word.substring(20, 30) +
                salt.substring(4, 6) +
                word.substring(30) +
                salt.substring(6);
        return this.doFinalEncrypt(withSalt);
    }

    @Override
    public String decrypt(String word) {
        String withSalt = this.doFinalDecrypt(word);
        return withSalt.substring(0, 10) +
                withSalt.substring(12, 22) +
                withSalt.substring(24, 34) +
                withSalt.substring(36, withSalt.length() - 2);
    }
}
