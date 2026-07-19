package com.rcbd.auto;

import android.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class Crypto {

    private static final String CHAVE = "RCBD@CHAVE2026BR"; // Mesma chave

    public static String criptografar(String data) {
        try {
            SecretKeySpec skey = new SecretKeySpec(CHAVE.getBytes("UTF-8"), "AES");
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, skey);
            return Base64.encodeToString(cipher.doFinal(data.getBytes("UTF-8")), Base64.DEFAULT);
        } catch(Exception e){
            return "";
        }
    }

    public static String descriptografar(String data) throws Exception {
        SecretKeySpec skey = new SecretKeySpec(CHAVE.getBytes("UTF-8"), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skey);
        return new String(cipher.doFinal(Base64.decode(data, Base64.DEFAULT)), "UTF-8");
    }
}
