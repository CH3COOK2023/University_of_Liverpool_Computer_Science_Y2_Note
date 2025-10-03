package Lab3;

import java.io.*;
import java.net.*;
public class EmailSenderLab3 {
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
        bw.write("HELO alice\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());
        if (!responseMessage.startsWith("250")) throw new Exception("250 reply not received from server.");
        // Send MAIL FROM command.
        bw.write("MAIL FROM: <JAVA@TEST.com>\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine()); // 卡住
        // Send RCPT TO command.
        bw.write("RCPT TO: <JAVA@TEST.com>\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());
        // Send DATA command.
        bw.write("DATA\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());

        // Send message data.
        bw.write("SUBJECT: JAVA test\r\n"+java.util.UUID.randomUUID().toString());
        bw.newLine();
        bw.flush();

        // End with line with a single period.
        bw.write(".\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());
        // Send QUIT command.
        bw.write("QUIT\r\n");
        bw.flush();
        System.out.println(responseMessage = br.readLine());
        socket.close();
    }
}