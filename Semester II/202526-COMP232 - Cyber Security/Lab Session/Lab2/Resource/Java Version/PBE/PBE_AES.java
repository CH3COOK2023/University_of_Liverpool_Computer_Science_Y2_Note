package PBE;
/**
 * Password based encryption with AES, this program is derived by A. Lisitsa  from the code by mrclay
 * taken from
 * https://github.com/mrclay/jSecureEdit/tree/master/src/org/mrclay/crypto
 * <p>
 * Notice that it **works only for the key size 128** see  the line
 * <p>
 * > KeySpec spec = new PBEKeySpec(password, salt, 1024, 128);
 * <p>
 * below.
 * For other AES key sizes (192,256) it does not work, producing the exception: Illegal key size or default parameters.
 **/

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.spec.KeySpec;


public class PBE_AES {
    public static void main(String[] args) throws Exception {
        // 这是用户输入的密码，可能很简单。把他转为char[]
        char[] password = "newpassword".toCharArray();
        // 这是生产密钥的“工厂” 。这里使用了 PBKDF2WithHmacSHA1 算法，它是目前非常流行的从密码生成密钥的标准
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        // 先定义一个盐值，用于混合密码
        // 我们用 keySpec (Key Specification 密钥规范)把这个密码混合一下
        // 混合 1024 轮，密钥长度 128
        // 128位：是 AES 的最低长度要求，但即便如此，它在目前也被认为是极其安全的，足以抵御现有的所有暴力破解手段。
        // 因为一些法律和出口限制，Java的加密只允许128位，如果要更高位需要导入安装【无限制强度策略文件】
        // 当然 KeySpec 只是记录这些规范，并不运行
        byte[] salt = {(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99};
        KeySpec keySpec = new PBEKeySpec(password, salt, 1024, 128);
        // 然后通过 SecretKeyFactory ， 根据这个规范，生成对应的 key， 并且转为 byte[]
        Key secretKey = new SecretKeySpec(factory.generateSecret(keySpec).getEncoded(), "AES");
        // 下面就是正常的 AES 步骤
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] cleartext = "This is a very very secret sentence!".getBytes();
        byte[] encryptedText = cipher.doFinal(cleartext);
        System.out.println("cipher : " + Utils.toHex(encryptedText));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(cipher.getIV()));
        System.out.println("\n Decrypted = " + new String(cipher.doFinal(encryptedText)));
    }
}

