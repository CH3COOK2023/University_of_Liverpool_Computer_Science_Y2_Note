package PBE;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.Key;
import java.security.NoSuchAlgorithmException;


/**
 * Example of using Password-based encryption
 */

public class PBEs {
    public static void run(String password, String plainText) throws Exception {
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;

        byte[] salt = {(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99};

        pbeParamSpec = new PBEParameterSpec(salt, 2048*10);

        char[] psw = password.toCharArray();


        pbeKeySpec = new PBEKeySpec(psw); // BPE Key Specification

        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");

        Key pbeKey = keyFac.generateSecret(pbeKeySpec);

        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec);

        byte[] cleartext = plainText.getBytes();

        byte[] encryptedText = pbeCipher.doFinal(cleartext);

        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
    }
    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();

        for (int i = 0; i < 1000; i++) {
            run("123459","This is a very very secret sentence.");
        }

        System.out.println("Time = " + (System.currentTimeMillis() - start) + " ms.");
    }
}
