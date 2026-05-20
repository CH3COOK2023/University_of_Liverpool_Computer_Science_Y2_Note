import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Arrays;

public class Test {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(1024, new SecureRandom());
        KeyPair pair = generator.generateKeyPair(); // 包含公钥和私钥
        String message = "Hello World!"; // 待加密的消息
        System.out.println("明文: " + message);
        byte[] cipherText = RandomKeyRSAExample.encodeUsingRSA(message, pair.getPublic());
        System.out.println("公钥加密后： " + Arrays.toString(cipherText));
        byte[] digests = MessageDigestExample.getDigest(message);
        System.out.println("摘要： " + Arrays.toString(digests));

        byte[] plainText = RandomKeyRSAExample.decodeUsingRSA(cipherText, pair.getPrivate());
        System.out.println("私钥解密后： " + new String(plainText));
        System.out.println("摘要是否一致： " + Arrays.equals(digests, MessageDigestExample.getDigest(new String(plainText))));

        // 中间人更改密文，导致报错
        try{
            cipherText[0] = 0;
            System.out.println("中间人更改密文后： " + Arrays.toString(cipherText));
            digests = MessageDigestExample.getDigest(new String(plainText));
            plainText = RandomKeyRSAExample.decodeUsingRSA(cipherText, pair.getPrivate());
            System.out.println("摘要是否一致： " + Arrays.equals(digests, MessageDigestExample.getDigest(new String(plainText))));

        }catch (Exception ignored){
            System.err.println("报错！"+ignored.getMessage());
        }
    }
}
