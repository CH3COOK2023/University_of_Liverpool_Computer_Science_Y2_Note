import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Demo {
    public static void main(String[] args) throws Exception {
        // Establish a TCP connection with the mail server.
        Socket socket = new Socket("35.246.112.180", 1025);

        // Create a BufferedReader to read a line at a time.
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        // Read greeting from the server.
        String response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("220")) {
            throw new Exception("220 reply not received from server.");
        }

        // Get a reference to the socket's output stream.
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // Send HELO command and get server response.
        String command = "HELO alice\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("250 reply not received from server.");
        }

        // Send MAIL FROM command.
        command = "MAIL FROM: <sender@example.com>\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("250 reply not received from server.");
        }

        // Send RCPT TO command.
        command = "RCPT TO: <recipient@example.com>\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("250 reply not received from server.");
        }

        // Send DATA command.
        command = "DATA\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("354")) {
            throw new Exception("354 reply not received from server.");
        }

        // Send message data.
        // End with line with a single period.
        System.out.print("Subject: Test Email\r\n");
        os.writeBytes("Subject: Test Email\r\n");
        System.out.print("This is the body of the email.\r\n");
        os.writeBytes("This is the body of the email.\r\n");
        System.out.print(".\r\n");
        os.writeBytes(".\r\n");
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("250 reply not received from server.");
        }

        // Send QUIT command.
        command = "QUIT\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("221")) {
            throw new Exception("221 reply not received from server.");
        }

        // Close the socket.
        socket.close();
        System.out.println("Connection closed.");
    }
}