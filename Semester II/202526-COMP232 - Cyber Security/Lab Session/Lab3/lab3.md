这个实验中我们要测试 BPE DES 的速度！

> **How fast is DES encryption?**



我们讲过了PBE AES，但是我们尝试使用 PBE DES 作为例子！



# BPE DES 

```java
public class PBEs {
    public static void main(String[] args) throws Exception {
        PBEKeySpec pbeKeySpec;
        PBEParameterSpec pbeParamSpec;
        SecretKeyFactory keyFac;
        // 定义盐值
        byte[] salt = {(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99};

        pbeParamSpec = new PBEParameterSpec(salt, 2048); // 把盐值和2048轮迭代这两个参数传入 PBE 规格

        char[] password = "newpassword".toCharArray();


        pbeKeySpec = new PBEKeySpec(password); // BPE Key Specification

        keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES"); // Key Factory， 算法是PBEWithMD5AndDES

        Key pbeKey = keyFac.generateSecret(pbeKeySpec); // 生成密钥

        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");

        pbeCipher.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParamSpec); // 把密钥和 PBE 规格传递进去

        byte[] cleartext = "This is a very very secret sentence".getBytes();

        byte[] encryptedText = pbeCipher.doFinal(cleartext);
        System.out.println("cipher : " + Utils.toHex(encryptedText));

        pbeCipher.init(Cipher.DECRYPT_MODE, pbeKey, pbeParamSpec);
        System.out.println("Decrypted = " + new String(pbeCipher.doFinal(encryptedText)));
    }
}
```



我们将其抽离出一个函数，去掉`sout`后计算时间。

我们尝试了 1k 次运行，只需要1700 - 1900ms

```java
run("123456","This is a very very secret sentence.");
```

迭代次数为 $2048$





尝试将迭代次数变大为两倍，此时时间来到了 2900 ms 差不多也是变为两倍！

如果变为十倍，此时时间来到了14000ms，接近10倍但没有到！





> # 阅读
>
> ### 迭代次数 (Iteration counts: 1, 2, 8... 65536)
>
> - **影响：非常显著，呈线性增长。**
> - **原理解析：** 在 PBE 中，你输入的“密码”并不能直接用来加密，而是需要通过**密钥派生函数（KDF）**加上“盐（Salt）”来生成真正的 DES 密钥。迭代次数就是指这个派生过程（通常是哈希运算，如 MD5 或 SHA-1）重复执行的次数。
> - **耗时表现：** 迭代次数主要影响**密钥生成阶段**的时间。迭代次数设置为 65536 时，密钥生成的耗时大约是迭代 1 次的 65536 倍。这种设计的初衷正是为了故意拉长计算时间，从而有效抵御黑客的字典攻击和暴力破解。
>
> ### 不同的密码 (Various passwords)
>
> - **影响：几乎没有影响（微乎其微）。**
> - **原理解析：** 无论你输入的是简单的 "123456" 还是长达几十个字符的复杂密码，它们都会被送入底层的哈希函数中处理。底层的哈希算法（例如 MD5/SHA）是按固定大小的数据块（通常是 64 字节/512 位）来处理输入的。
> - **耗时表现：** 只要密码长度没有夸张到跨越多个哈希数据块，处理不同内容、不同常规长度的密码所需的时间是固定的。在宏观的时间测量上，你观察不到耗时差异。
>
> ### 不同大小的明文 (Plain texts of different sizes)
>
> - **影响：显著，呈线性正相关。**
> - **原理解析：** DES 是一种分组密码（Block Cipher），它会将你的明文数据切分成 64 位（8 字节）的小块，然后逐块进行加密。
> - **耗时表现：** 这一项影响的是真正的**加密/解密阶段**。明文文件越大，被切分出的 8 字节数据块就越多，DES 算法需要执行的轮数就越多。因此，加密 1MB 数据的时间大约是加密 1KB 数据的 1000 倍。







```java
char[] chars = System.console().readPassword(); // 这个语句要在真正的控制台（终端）中才能运行否则会拿不到 .console() 而报空指针异常错误！
```



我们应该增加还是减少迭代次数，以使加密更能抵抗暴力搜索攻击？这样做可能有哪些缺点？

> 为了使加密更能抵抗暴力搜索攻击（Brute-force attack），我们绝对应该**增加**迭代次数。因为杠杆很大！
>
> 负面影响：
>
> - **性能下降与用户体验受损：** 密码学是公平的，合法用户在每次解密文件或登录账号时，也必须经历同样漫长的计算过程。如果迭代次数设置得过高（比如生成密钥需要 3 到 5 秒），系统就会显得非常卡顿，严重影响正常的业务流转和用户体验。
> - **计算资源消耗激增：** 高迭代次数本质上是在故意消耗计算能力（通常是 CPU 资源）。
>   - **服务端压力：** 如果密码验证（如哈希计算）发生在服务器端，海量的并发登录请求会迅速吃满服务器的 CPU 资源。黑客甚至可以利用这一点，通过发送大量虚假登录请求来耗尽服务器算力，从而引发拒绝服务攻击（DoS 攻击）。
>   - **移动端耗电：** 如果加解密计算发生在手机或笔记本电脑上，密集的 CPU 运算会显著增加耗电量，甚至导致设备发热。







































