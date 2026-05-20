package DES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec; 
import javax.crypto.spec.SecretKeySpec;

public class DES_CBC_AutoIV {
    public static void main(String[] args) throws Exception {
        // --- 1. 密钥生成 ---
        // 查找 DES 算法的密钥生成器
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        SecretKey key = kg.generateKey();

        // 将生成的密钥转换为字节数组并封装
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "DES");

        // --- 2. 获取 Cipher 实例 ---
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec); // 重点：先初始化init
        // 用初始化的 cipher去获取IV，
        IvParameterSpec ivSpec = new IvParameterSpec(cipher.getIV());

        // --- 5. 执行加密 ---
        String plainText = "This is a secret!"; // 待加密的明文

        // 将明文转为字节数组并进行加密计算
        byte[] encoded = cipher.doFinal(plainText.getBytes());

        // 打印加密后的结果（密文）
        System.out.println("Resulting Cipher Text:\n");
        for (byte x : encoded)
            System.out.print(x + " ");
        System.out.println();


        // 解密
        cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(cipher.getIV()));
        System.out.println("\nDecoded text = " + new String(cipher.doFinal(encoded)));
    }
}
