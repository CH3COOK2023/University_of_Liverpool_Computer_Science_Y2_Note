package AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

// Original program by Jason Wiess is modified by A. Lisitsa to make encryption working 
//in CBC mode; 
// In this version IV is initialised by cipher object itself, but it can be extracted by
// getIV method and used later with decryption;  

public class AES_CBC_getIV {
    public static void main(String[] args) throws Exception {
        SecretKey key = KeyGenerator.getInstance("AES").generateKey();
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "AES");

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");

        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        IvParameterSpec ivSpec = new IvParameterSpec(cipher.getIV());

        String plainText = "This is a secret!";

        byte[] encrypted = cipher.doFinal(plainText.getBytes());

        System.out.println("Resulting Cipher Text:\n");
        for (byte x : encrypted)
            System.out.print(x + " ");
        System.out.println();

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec);
        System.out.println(new String(cipher.doFinal(encrypted)));
    }
}
