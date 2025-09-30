import java.io.*;
import java.net.*;

public class EmailSender {
    public static void main(String[] args) throws Exception
    {
        // Establish a TCP connection with the mail server.
        Socket socket = new Socket("35.246.112.180",1025);

        // .getInputStream() method RECEIVE the data from server.
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        // .getOutputStream() method SENT the data to server.
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

        // Read greeting from the server.
        String responseMessage = br.readLine();
        System.out.println(responseMessage);
        if (!responseMessage.startsWith("220")) throw new Exception("220 reply not received from server.");

        // Send example
        bw.write("HELO alice");
        bw.newLine();
        bw.flush();
        System.out.println(responseMessage = br.readLine());
        if (!responseMessage.startsWith("250")) throw new Exception("250 reply not received from server.");


        // Send MAIL FROM command.
        bw.write("MAIL FROM: <sender@example.com>");
        bw.flush();
        System.out.println(responseMessage = br.readLine());

        // Send RCPT TO command.

        bw.write("RCPT TO: <test@test.com>\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());

        // Send DATA command.

        bw.write("DATA\r\nSUBJECT: JAVA test\r\n"+java.util.UUID.randomUUID().toString());
        bw.flush();
        System.out.println(responseMessage = br.readLine());

        // Send message data.
        // End with line with a single period.

        bw.write(".\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());

        // Send QUIT command.

        bw.write("QUIT");
        bw.flush();
        System.out.println(responseMessage = br.readLine());

    }
}