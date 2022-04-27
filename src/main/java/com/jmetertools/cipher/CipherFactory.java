package com.jmetertools.cipher;


import com.jmetertools.base.Constant;

import java.util.HashMap;
import java.util.Map;


public class CipherFactory {
    private static final Map<String, AbCipher> cachedCipher = new HashMap<>();

    static {
        cachedCipher.put("range/password", new PasswordCipher(Constant.RANGE_KEY));
        cachedCipher.put("range/token", new RangeTokenCipher());
        cachedCipher.put("ump/password", new PasswordCipher(Constant.UMP_KEY));
        cachedCipher.put("ump/token", new UmpTokenCipher());
    }

    public static AbCipher createCipher(String cipherFormat){
        if (cipherFormat == null || cipherFormat.isEmpty()){
            return null;
        }
        return cachedCipher.get(cipherFormat.toLowerCase());
    }
}
