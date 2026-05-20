package PBE;/* Password based encryption and decryption with AES. It is taken from
 *
 *https://stackoverflow.com/questions/43190139/how-to-decrypt-a-string-using-pbe-algorithm
 *
 * with a few insignificant changes
 */


import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;


public class PBE_AES2 {
    public static void main(String[] args) throws Exception {

        SecureRandom rnd = new SecureRandom();
        byte[] iv = new byte[16];
        rnd.nextBytes(iv); // 随机化 IV
        byte[] salt = {(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99};
        String password = "password";
        byte[] plaintext = "This is a very very secret sentence.".getBytes();

        IvParameterSpec ivParamSpec = new IvParameterSpec(iv); // 构建 IV
        PBEParameterSpec pbeParamSpec = new PBEParameterSpec(salt, 10000, ivParamSpec); // 指定PBE参数规范
        PBEKeySpec keySpec = new PBEKeySpec(password.toCharArray()); // 指定 BPE 密码规范


        SecretKeyFactory kf = SecretKeyFactory.getInstance("PBEWithHmacSHA256AndAES_128");
        SecretKey secretKey = kf.generateSecret(keySpec);


        System.out.println(new String(secretKey.getEncoded()));

        // 加密
        Cipher enc = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
        enc.init(Cipher.ENCRYPT_MODE, secretKey, pbeParamSpec);
        byte[] ciphertext = enc.doFinal(plaintext);
        System.out.println("Encrypted text: " + Utils.toHex(ciphertext));

        // 解密
        Cipher dec = Cipher.getInstance("PBEWithHmacSHA256AndAES_128");
        dec.init(Cipher.DECRYPT_MODE, secretKey, pbeParamSpec);
        byte[] decrypted = dec.doFinal(ciphertext);
        String message = new String(decrypted);

        System.out.println(message);

    }
}
