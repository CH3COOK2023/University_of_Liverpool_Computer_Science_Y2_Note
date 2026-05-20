package DES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 实验 1：DES 加密（CBC 模式 - 手动初始化 IV）
 * 这个程序演示了 CBC 模式的操作，它比 ECB 模式更安全。
 */
public class DES_CBC {
    public static void main(String[] args) throws Exception {
        // --- 1. 密钥生成 ---
        // 查找 DES 算法的密钥生成器
        KeyGenerator kg = KeyGenerator.getInstance("DES");
        SecretKey key = kg.generateKey();

        // 将生成的密钥转换为字节数组并封装
        SecretKeySpec keySpec = new SecretKeySpec(key.getEncoded(), "DES");

        // --- 2. 获取 Cipher 实例 ---
        // 注意这里的参数："算法/模式/填充方式"
        // CBC 模式要求必须指定一个填充方案（如 PKCS5Padding），因为数据必须对齐到块大小
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        // --- 3. 手动初始化 IV (Initialization Vector) ---
        // CBC 模式需要一个“初始向量”来增加加密的随机性。
        // 对于 DES，IV 必须是 8 个字节（64位），因为 DES 的块大小是 8 字节。
        // 将字节数组包装成 IvParameterSpec 对象，这样 Cipher 才能识别
        // Iv 就是 Initial Vector 初始向量！
        // 在这里我们手动指定了一个 IV
        IvParameterSpec ivSpec = new IvParameterSpec(new byte[]{0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00});

        // --- 4. 初始化 Cipher ---
        // 与 ECB 不同，这里需要额外传入 ivSpec 参数
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);

        // --- 5. 执行加密 ---
        String plainText = "This is a secret!"; // 待加密的明文

        // 将明文转为字节数组并进行加密计算
        byte[] encoded = cipher.doFinal(plainText.getBytes());

        // 打印加密后的结果（密文）
        System.out.println("Resulting Cipher Text:\n");
        for (byte x : encoded)
            System.out.print(x + " ");
        System.out.println();


        // 解密，也要传入 ivSpec
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        System.out.println("\nDecoded text = " + new String(cipher.doFinal(encoded)));
    }
}