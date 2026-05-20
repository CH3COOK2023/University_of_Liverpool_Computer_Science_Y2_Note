import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MessageDigestExample {
    public static void main(String[] args) throws Exception {
        String input = "This is a   message";
        MessageDigest hash = MessageDigest.getInstance("SHA1");

        System.out.println("input : " + input);

        hash.update(Utils.toByteArray(input));

        System.out.println("digest : " + Utils.toHex(hash.digest()));

    }

    /**
     * 获取消息的摘要
     * @param message 消息
     * @return SHA1摘要
     */
    public static byte[] getDigest(String message) throws Exception {
        MessageDigest sha1MessageDigest = MessageDigest.getInstance("SHA1");
        sha1MessageDigest.update(Utils.toByteArray(message));
        return sha1MessageDigest.digest();
    }
}
