package com.jmetertools.cipher;

import com.jmetertools.base.Constant;
import com.jmetertools.utils.RandomUtil;


public class PasswordCipher extends AbCipher{

    /**
     * 构造方法
     * @param key 加密用的key
     */
    public PasswordCipher(String key) {
        super(key, Constant.ENCRYPT_PASSWORD_LEN);
    }

    /**
     * 密码加密
     * @param word 待加密密码
     * @return 加密后的16进制字符串
     */
    @Override
    public String encrypt(String word) {
        int passwordLen = word.length();
        String salt = String.join("", RandomUtil.getSample(Constant.BROAD_CHAR_SET.get(), 8));
        int paddingLen = 22 - passwordLen;
        String padding = String.join("", RandomUtil.getSample(Constant.BROAD_CHAR_SET.get(), paddingLen));
        String withSalt = word.substring(0, 8) + salt + word.substring(8);
        String withPadding = withSalt + padding + String.format("%02d", passwordLen);
        return this.doFinalEncrypt(withPadding);
    }

    /**
     * 密码解密
     * @param word 加密后的16进制字符串密码
     * @return 解密后的密码
     */
    @Override
    public String decrypt(String word) {
        String withPadding = this.doFinalDecrypt(word);
        int passwordLen = Integer.parseInt(withPadding.substring(withPadding.length() -2 ));
        return withPadding.substring(0, 8) + withPadding.substring(16, 8 + passwordLen);

    }
}
