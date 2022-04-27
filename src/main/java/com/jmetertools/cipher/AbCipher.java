package com.jmetertools.cipher;


import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import com.jmetertools.utils.ByteUtil;
import groovyjarjarantlr4.v4.runtime.misc.NotNull;

import static java.nio.charset.StandardCharsets.ISO_8859_1;
import static java.nio.charset.StandardCharsets.UTF_8;



public abstract class AbCipher {
    String key;
    int contentSize;
    final String CipherTransformation = "AES/ECB/NoPadding";

    /**
     * 构造方法
     * @param key 加密用的key
     * @param contentSize 最终内容长度，设置为64时，若加密完结果只有63位，则在最高位补0
     */
    public AbCipher(String key, int contentSize){
        this.key = key;
        this.contentSize = contentSize;
    }

    /**
     * 获取加解密用的cipher对象
     * @param transformation Cipher 信息，格式：algorithm/mode/padding
     * @return cipher对象
     */
    protected Cipher getCipher(@NotNull String transformation){
        try{
            // example: "AES/ECB/NoPadding"
            return Cipher.getInstance(transformation);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * 获取基于key的SecretKeySpec对象
     * @param key 加密的密钥
     * @return SecretKeySpec对象
     */
    protected SecretKeySpec getKeySpec(@NotNull String key){
        byte[] byteKey = key.getBytes(UTF_8);
        return new SecretKeySpec(byteKey, "AES");
    }

    /**
     * 填充高位字符(默认高位为0时会不显示，所以需要填充长度)
     * @param word 需要填充的字符串
     * @param length 填充的目标长度
     * @return 高位补0后的字符串
     */
    protected String fillHighPoint(String word, int length){
        int currentLen = word.length();
        if (word.length() % length != 0){
            StringBuilder sb = new StringBuilder(word);
            for (int i = 0; i < length - currentLen; i++){
                sb.insert(0, "0");
            }
            return sb.toString();
        }
        return word;
    }

    /**
     * 加密
     * @param word 待加密内容
     * @return 返回加密后的字符串，会自动补齐高位
     */
    protected String doFinalEncrypt(String word){
        try {
            // 获取并初始化cipher
            Cipher cipher = this.getCipher(this.CipherTransformation);
            cipher.init(Cipher.ENCRYPT_MODE, this.getKeySpec(this.key));
            // 获取加密结果（byte 数组）
            byte[] encrypted = cipher.doFinal(word.getBytes(ISO_8859_1));
            // 将bytes转换为16进制字符串并进行高位填充
            return this.fillHighPoint(ByteUtil.binary(encrypted, 16), this.contentSize);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 解密
     * @param word 待解密字符串
     * @return 解密后的字符串
     */
    protected String doFinalDecrypt(String word){
        try {
            // 获取并初始化cipher
            Cipher cipher = this.getCipher(this.CipherTransformation);
            cipher.init(Cipher.DECRYPT_MODE, this.getKeySpec(this.key));
            // 将16进制字符串转为byte数组
            byte[] byteEncrypted = ByteUtil.toByteArray(word);
            // 进行解密
            return new String(cipher.doFinal(byteEncrypted), ISO_8859_1);
        } catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }


    public abstract String encrypt(String word);
    public abstract String decrypt(String word);

}
