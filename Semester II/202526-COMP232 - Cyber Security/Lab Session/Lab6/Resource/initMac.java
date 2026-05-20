import java.security.*;
import javax.crypto.*;
import java.util.Arrays;

/**
 * This program demonstrates how HMAC-SHA256 generates a Message Authentication Code (MAC)
 * and verifies the three conditions specified in Lab 6.
 */
public class initMac {

    public static void main(String[] args) throws Exception {

        // 生成两个不同的密钥 (模拟收发双方有相同密钥，或不同密钥的情况)
        KeyGenerator kg = KeyGenerator.getInstance("HmacSHA256");
        SecretKey secretKeyA = kg.generateKey();
        SecretKey secretKeyB = kg.generateKey();

        // 准备测试文本
        String text1 = "Hi";
        String text2 = "Hello";

        System.out.println("============== HMAC-SHA256 Verification ==============\n");

        // 情况 1: 相同的密钥，相同的文本 [cite: 16]
        System.out.println("--- 1. Same Key, Same Text ---");
        byte[] mac1_sender = calculateMAC(secretKeyA, text1);
        byte[] mac1_verifier = calculateMAC(secretKeyA, text1);
        System.out.println("Sender MAC:   " + toHexString(mac1_sender));
        System.out.println("Verifier MAC: " + toHexString(mac1_verifier));
        System.out.println("Result:       " + (Arrays.equals(mac1_sender, mac1_verifier) ? "Match (Same)" : "Mismatch (Different)") + "\n");

        // 情况 2: 相同的密钥，不同的文本 [cite: 18]
        System.out.println("--- 2. Same Key, Different Text ---");
        byte[] mac2_sender = calculateMAC(secretKeyA, text1);
        byte[] mac2_verifier = calculateMAC(secretKeyA, text2);
        System.out.println("MAC for 'Hi':    " + toHexString(mac2_sender));
        System.out.println("MAC for 'Hello': " + toHexString(mac2_verifier));
        System.out.println("Result:          " + (Arrays.equals(mac2_sender, mac2_verifier) ? "Match (Same)" : "Mismatch (Different)") + "\n");

        // 情况 3: 不同的密钥，相同的文本
        System.out.println("--- 3. Different Key, Same Text ---");
        byte[] mac3_sender = calculateMAC(secretKeyA, text1);
        byte[] mac3_verifier = calculateMAC(secretKeyB, text1);
        System.out.println("MAC with Key A: " + toHexString(mac3_sender));
        System.out.println("MAC with Key B: " + toHexString(mac3_verifier));
        System.out.println("Result:         " + (Arrays.equals(mac3_sender, mac3_verifier) ? "Match (Same)" : "Mismatch (Different)") + "\n");
    }

    /**
     * 辅助方法：使用指定的密钥和文本计算 MAC
     */
    private static byte[] calculateMAC(SecretKey key, String text) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        return mac.doFinal(text.getBytes());
    }

    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    private static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    /*
     * Converts a byte array to hex string
     */
    private static String toHexString(byte[] block) {
        StringBuffer buf = new StringBuffer();
        int len = block.length;
        for (int i = 0; i < len; i++) {
            byte2hex(block[i], buf);
            if (i < len-1) {
                buf.append(":");
            }
        }
        return buf.toString();
    }
}