import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import java.security.Security;
/*import org.bouncycastle.jce.provider.BouncyCastleProvider;
/
/**
 * RSA example with random key generation.
 */
public class RandomKeyRSAExample
{
    public static void main(
        String[]    args)
        throws Exception
    {
       /* Security.addProvider(new BouncyCastleProvider()); */   
        byte[]           input = new byte[] { (byte)0xbe, (byte)0xef }; 
        Cipher	         cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        SecureRandom     random = new SecureRandom();
        
        // create the keys
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        
        generator.initialize(512, random);

        KeyPair          pair = generator.generateKeyPair();
        Key              pubKey = pair.getPublic();
        Key              privKey = pair.getPrivate();

        System.out.println("input : " + Utils.toHex(input));
        
        // encryption step
        
        cipher.init(Cipher.ENCRYPT_MODE, pubKey);

        byte[] cipherText = cipher.doFinal(input);

        System.out.println("cipher: " + Utils.toHex(cipherText));
        
        // decryption step

        cipher.init(Cipher.DECRYPT_MODE, privKey);

        byte[] plainText = cipher.doFinal(cipherText);
        
        System.out.println("plain : " + Utils.toHex(plainText));
    }
}
