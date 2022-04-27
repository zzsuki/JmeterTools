package com.jmetertools.cipher;

import com.jmetertools.base.Constant;
import com.jmetertools.utils.RandomUtil;
import java.util.List;


public class RangeTokenCipher extends AbCipher{

    /**
     * 构造方法
     */
    public RangeTokenCipher() {
        super(Constant.RANGE_KEY, Constant.ENCRYPT_RANGE_TOKEN_LEN);
    }

    /**
     * 获取加入chaos的token
     * @param token 原始token
     * @return 加入chaos的token
     */
    private static String getChaosToken(String token){
        List<String> chaosSet = Constant.ALPHABET.get();
        chaosSet.addAll(Constant.NUMBERS.get());
        String chaos = String.join("", RandomUtil.getRandomSample(chaosSet, 16));
        return token + chaos;
    }

    /**
     * 去除chaos字段，获取token
     * @param chaosToken 包含chaos的token
     * @return token
     */
    private static String getToken(String chaosToken){
        return chaosToken.substring(0, chaosToken.length() - 16);
    }

    /**
     * 组装包含chaos的token与salt
     * @param chaosToken 包含chaos的token
     * @param salt 盐
     * @return 组装后的字符串
     */
    private static String combineSaltWithChaosToken(String chaosToken, String salt){
        return  chaosToken.substring(0, 10) +
                salt.substring(0, 2) +
                chaosToken.substring(10, 20) +
                salt.substring(2, 4) +
                chaosToken.substring(20, 30) +
                salt.substring(4, 6) +
                chaosToken.substring(30) +
                salt.substring(6);
    }

    /**
     * 对包含chaos的token加密
     * @param word 待加密的token，包含chaos
     * @return 加密后的token
     */
    @Override
    public String encrypt(String word) {
        String salt = String.join("", RandomUtil.getRandomSample(Constant.BROAD_CHAR_SET.get(), 8));
        return this.doFinalEncrypt(combineSaltWithChaosToken(word, salt));
    }

    /**
     * 对不包含chaos的token加密
     * @param word 不包含chaos的原始token
     * @return 加密后的token
     */
    public String encryptRawToken(String word) {
        String chaosToken = getChaosToken(word);
        String salt = String.join("", RandomUtil.getRandomSample(Constant.BROAD_CHAR_SET.get(), 8));
        return this.doFinalEncrypt(combineSaltWithChaosToken(chaosToken, salt));
    }

    /**
     * token解密，但只会返回包含chaos的版本；靶场单点登录采用了在redis里存放 token: chaos的方式；
     * 为方便使用，解密方法未对token和chaos拆分；因为如果chaos也重新生成，后端回判断chaos和redis里的不同，导致判定为重复登录
     * 如果想获取原始的不包含chaos的token，使用getToken方法即可获取
     * @param word 加密后的token，16进制格式的字符串
     * @return token+chaos
     */
    @Override
    public String decrypt(String word) {
        String saltToken = this.doFinalDecrypt(word);
        return saltToken.substring(0, 10) +
                saltToken.substring(12, 22) +
                saltToken.substring(24, 34) +
                saltToken.substring(36, 62);
    }
}
