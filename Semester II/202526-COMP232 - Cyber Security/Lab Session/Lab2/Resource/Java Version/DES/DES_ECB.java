package DES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 实验 1: DES 加密（ECB 模式）
 * 展示了最基础的对称加密流程。
 */
public class DES_ECB {
    public static void main(String[] args) throws Exception {
        // --- 1. 密钥生成阶段 ---
        // 获取 DES 算法的密钥生成器实例
        // 自动生成一个随机的 DES 密钥
        // KeyGenerator 实际上是根据你请求的算法（如 "DES"）去寻找底层的 Security Provider（安全提供者）
        // 并由该提供者返回一个具体的实现类。
        SecretKey key = KeyGenerator.getInstance("DES").generateKey();
        // 在这里，SecretKey 是接口，其返回实现类对象是： com.sun.crypto.provider.DESKey

        // 将生成的原始密钥字节封装成 SecretKeySpec 对象，以便 Cipher 使用
        // key.getEncoded() 相当于 .toByteArray()
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "DES");

        // --- 2. 初始化 Cipher（加密器） 对象 ---
        // 获取实现 DES 算法的 Cipher 实例（Java 默认通常为 DES/ECB/PKCS5Padding）
        Cipher cipher = Cipher.getInstance("DES");

        // 使用生成的密钥初始化 Cipher，设置为“加密模式”
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);

        // --- 3. 执行加密 ---
        String plainText = "This is a secret"; // 准备加密的明文
        // 调用 doFinal 方法进行加密，返回密文字节数组
        byte[] cipherText = cipher.doFinal(plainText.getBytes());

        // 打印加密后的密文（以字节数值形式输出）
        System.out.println("Resulting Cipher Text:\n");
        for (int i = 0; i < cipherText.length; i++)
            System.out.print(cipherText[i] + " ");
        System.out.println();


        // --- 4. 执行解密 ---
        // 重新初始化 Cipher，设置为“解密模式”，使用相同的密钥
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        // 调用 doFinal 方法解密，并将结果转换回字符串
        String decryptedText = new String(cipher.doFinal(cipherText));

        System.out.println("Decrypted Text:\n" + decryptedText);
    }
}