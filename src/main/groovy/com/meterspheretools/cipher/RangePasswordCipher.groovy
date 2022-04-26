package com.meterspheretools.cipher

import com.meterspheretools.cipher.AbstractCipher

class RangePasswordCipher extends AbstractCipher{
    /**
     * 构造方法
     * @param key 加密用的key
     * @param contentSize 最终内容长度，设置为64时，若加密完结果只有63位，则在最高位补0
     */
    RangePasswordCipher(String key, int contentSize) {
        super(key, contentSize)
    }

    @Override
    String encrypt(String word) {
        def passwordLen = word.length()

    }

    @Override
    String decrypt(String word) {
        return null
    }
}
