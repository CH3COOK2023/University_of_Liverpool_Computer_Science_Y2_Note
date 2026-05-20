import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;

public class Main {
    public static final byte[] salt = {(byte) 0xc7, (byte) 0x73, (byte) 0x21, (byte) 0x8c, (byte) 0x7e, (byte) 0xc8, (byte) 0xee, (byte) 0x99};
    public static final String[] passwordList = {"P@S$W0rD", "thisismypassword", "VeryLongP@$$W0rD", "%O^t#2Fv0JUjVdRV2RW%"};
    public static final String plainText = "This is a secret message for COMP232.";

    public static void main(String[] args) throws Exception {
        taskA();
    }

    public static void taskC() throws Exception {
        BufferedWriter br = new BufferedWriter(new FileWriter("data.txt"));
        int startIter = 1024;
        int endIter = 2048;
        int loopCount = 1000; // 1000 is enought

        // warm up runtime environment
        for (int i = 0; i < 2000; i++)
            decode(passwordList[0], 1024, salt, encode(passwordList[0], 1024, salt, plainText));

        System.out.println("Iteration, Pwd1_Dec(ms), Pwd2_Dec(ms), Pwd3_Dec(ms), Pwd4_Dec(ms)");

        for (int iter = startIter; iter <= endIter; iter++) {
            br.write(iter + ",");
            for (int i = 0; i < passwordList.length; i++) {
                String password = passwordList[i];
                // encrypt time
                long startEncryptTime = System.nanoTime();
                byte[] cipherText = null;
                for (int k = 0; k < loopCount; k++)
                    cipherText = encode(password, iter, salt, plainText);
                double avgEncryptTime = (System.nanoTime() - startEncryptTime) / (double) loopCount / 1_000_000.0;

                // decrypt time
                long startDecrypt = System.nanoTime();
                for (int k = 0; k < loopCount; k++) decode(password, iter, salt, cipherText);
                double avgDecryptTime = (System.nanoTime() - startDecrypt) / (double) loopCount / 1_000_000.0;

                // total time
                br.write(String.format("(%.5f,%.5f)", avgEncryptTime, avgDecryptTime));
                if (i != passwordList.length - 1) br.write(",");
            }
            System.out.println("Iter = " + iter);
            br.newLine();
            br.flush();
        }
        br.close();
    }

    public static void taskB() throws Exception {
        calculateBruteForce("P@S$W0rD             | lc+uc+num+char", 95, 8, 0.12);
        calculateBruteForce("thisismypassword     | lc            ", 26, 16, 0.12);
        calculateBruteForce("VeryLongP@$$W0rD     | lc+uc+num+char", 95, 16, 0.12);
        calculateBruteForce("%O^t#2Fv0JUjVdRV2RW% | lc+uc+num+char", 95, 20, 0.12);
    }

    public static void calculateBruteForce(String label, int space, int length, double msPerAttempt) {
        BigInteger attempts = BigInteger.valueOf(space).pow(length);
        // search half space is possible to find out the password
        BigDecimal totalMs = new BigDecimal(attempts).multiply(BigDecimal.valueOf(msPerAttempt)).divide(BigDecimal.valueOf(2), RoundingMode.HALF_UP);

        BigDecimal days = totalMs.divide(BigDecimal.valueOf(86_400_000), 2, RoundingMode.HALF_UP);
        BigDecimal years = days.divide(BigDecimal.valueOf(365), 2, RoundingMode.HALF_UP);
        System.out.printf("Password info: %-30s | Length: %2d | Estimate Year : %s%n", label, length, years.toPlainString());
    }

    public static void taskA() throws Exception {
        int iterations = 1024; // Iteration Time
        int loopCount = 10000; // loop to ensure stability

        // Warm up - without timing to make sure JVM loaded the .class and JIT.
        for (int i = 0; i < 5000; i++)
            decode(passwordList[0], iterations, salt, encode(passwordList[0], iterations, salt, plainText));

        System.out.printf("%-25s | %-12s | %-12s | %-12s%n", "Password", "Encrypt(ms)", "Decrypt(ms)", "Total(ms)");
        System.out.println("---------------------------------------------------------------------");

        for (String password : passwordList) {
            // measure encode time
            long startEncryptTime = System.nanoTime();
            byte[] cipherText = null;
            for (int i = 0; i < loopCount; i++)
                cipherText = encode(password, iterations, salt, plainText);

            double avgEncryptTime = (System.nanoTime() - startEncryptTime) / (double) loopCount / 1_000_000.0;

            // measure decode time
            long startDecrypt = System.nanoTime();
            for (int i = 0; i < loopCount; i++) decode(password, iterations, salt, cipherText);
            double avgDecryptTime = (System.nanoTime() - startDecrypt) / (double) loopCount / 1_000_000.0;

            // total time
            System.out.printf("%-25s | %-12.6f | %-12.6f | %-12.6f%n", password, avgEncryptTime, avgDecryptTime, avgEncryptTime + avgDecryptTime);
        }
    }

    public static byte[] encode(String password, int iterationTime, byte[] salt, String text) throws Exception {
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterationTime));

        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.ENCRYPT_MODE, key, new PBEParameterSpec(salt, iterationTime));

        return pbeCipher.doFinal(text.getBytes());
    }

    public static byte[] decode(String password, int iterationTime, byte[] salt, byte[] cipherText) throws Exception {
        SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(new PBEKeySpec(password.toCharArray(), salt, iterationTime));

        Cipher pbeCipher = Cipher.getInstance("PBEWithMD5AndDES");
        pbeCipher.init(Cipher.DECRYPT_MODE, key, new PBEParameterSpec(salt, iterationTime));

        return pbeCipher.doFinal(cipherText);
    }
}