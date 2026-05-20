这份实验（COMP232 Lab 2）的核心目标是让你通过 Java 或 Python 实践对称加密算法（DES 和 AES）以及基于密码的加密（PBE） 。

以下是本次 Lab 需要完成的主要任务概括：

**2. 基于密码的加密 PBE (Task 2)**

- **理解原理**：学习如何将密码、盐值（Salt）和迭代次数通过混合函数生成密钥 。
- **性能测试**：尝试修改 **迭代次数（Iteration Count）**，观察其对程序执行时间的影响 。
- **代码实现**：在给定的示例代码基础上进行修改，**实现解密（Decryption）功能** 。

**3. 从 DES 迁移到 AES (Task 3)**

- **算法升级**：将之前任务中的 DES 算法替换为更现代的 **AES** 算法 。

  

  

- **实验探索**：在 Java 中注意 AES 的 PBE 密钥长度限制（通常为 128 位），在 Python 中探索不同长度的 AES 密钥 。

  

**提示：**

- 如果你选择 Python，请确保先安装 `pycryptodome` 库 。
- Java 用户需要参考 JCA/JCE 参考手册来处理 AES 的具体实现 。

# Task 1

**DES 加密模式对比 (Task 1)**

- **运行与观察**：编译并运行 DES 在 **ECB** 和 **CBC** 两种模式下的程序 。
- **性能演示**：通过修改或对比程序，**演示 CBC 模式相对于 ECB 模式的优势**（通常涉及安全性，如抵御重放攻击或模式识别） 。

> [!NOTE]
>
> 简单来说，DES 和 AES 是**加密算法**（相当于“锁”的构造），而 ECB 和 CBC 是**加密模式**（相当于“如何重复使用这把锁来锁上一大堆货物”）。
>
> ECB (Electronic Codebook) - 电子密码本模式：
>
> 这是最简单、最基础的模式 。
>
> - **工作原理**：将明文分成若干固定大小的块，每一块都使用**完全相同**的密钥进行独立加密。
> - **公式**：$C_i = E_k(P_i)$（第 $i$ 块密文 = 用密钥 $k$ 加密第 $i$ 块明文）。
> - **缺点（致命伤）**：如果明文中存在相同的块（比如图片中大面积的纯色区域），加密后的密文块也会完全相同。
>   - **结果**：黑客即使无法破解密钥，也能通过观察密文的**重复模式**推测出原始数据的特征。这也是为什么 Lab 任务 1 要求你证明 CBC 的优势 。
>
> **CBC (Cipher Block Chaining) - 密码块链接模式**
>
> 这是目前应用最广泛、更安全的模式之一 。
>
> - **工作原理**：每个明文块在加密前，先与**前一个密文块**进行异或（XOR）运算。
> - **公式**：$C_i = E_k(P_i \oplus C_{i-1})$。
> - **初始化向量 (IV)**：由于第一块前面没有密文，所以需要一个随机的“初始值”，即 **IV (Initialization Vector)** 。
> - **优点**：
>   - 即使明文块完全相同，由于它们与不同的前文“链接”在一起，加密后的结果也会完全不同。
>   - 这有效地隐藏了数据的统计模式，安全性远高于 ECB 。



## DES ECB 加密

在 Java 中使用简单 DES（ECB）加密的方法如下

1. 先生成对称密钥（Key）
2. 然后构建Cipher（加密、解密器）

```java
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
```



## DES CBC IV 加密

IV 就是 Initial Vector 初始向量

### 手动指定 IV

```java
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
```



我们在 `Cipher` 对象 `.init()` 之前创建好 `IvParameterSpec` ，然后要同时传入三个参数，除了`Cipher.ENCRYPT_MODE`以外，相比 ECB，除了 keySpec（密钥）之外，还要额外传入这个 `IvParameterSpec` 类对象。



> `IvParameterSpec`中，**Spec** 是 **Specification**（规范/规格）的缩写



### 自动指定 IV

不同的是 `Cipher` 对象必须先 `.init()` ，无需传入 IV，随后让 `cipher` 直接 `.getIV()` 生成一个IV

```java
// --- 2. 获取 Cipher 实例 ---
Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
cipher.init(Cipher.ENCRYPT_MODE, keySpec); // 重点：先初始化init
// 用初始化的 cipher去获取IV，
IvParameterSpec ivSpec = new IvParameterSpec(cipher.getIV());
```



然后加密的时候就不需要再传入 IV 了！直接加密即可

```java
byte[] encoded = cipher.doFinal(plainText.getBytes());
```



但是对于解密，你需要用 `cipher`的`.getIV()`方法获取到原来第一开始生成的 IV



```java
cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(cipher.getIV()));
System.out.println("\nDecoded text = " + new String(cipher.doFinal(encoded)));
```













































