/************************************
 * Filename:  SMTPInteraction.java
 ************************************/

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Create SMTP connection, and send an email on that website.
 * Student ID 201945844.
 */
public class SMTPInteraction {

    /**
     * Use this in case that you want DISABLED status code check
     */
    private static final int NO_TARGET_STATUS_CODE = -1;
    /**
     * Terminal output prefix
     */
    private static final String RESPONSE = "RESPONSE | ";
    /**
     * Terminal output prefix
     */
    private static final String SEND = "SEND     | ";
    /**
     * Linux "\n"
     */
    private static final String CRLF = "\r\n";
    /**
     * Socket that connect to the server
     ***/
    private final Socket connection;
    /**
     * Read from the server
     */
    private final BufferedReader fromServer;
    /**
     * Send to the server
     */
    private final BufferedWriter toServer;


    /**
     * Constructor.
     * Create an SMTP interaction Object. Initialize var and try handshake to server.
     *
     * @param mailMessage Object of EmailMessage, contains much info from GUI surface.
     * @throws IOException Any possible Exception.
     */
    public SMTPInteraction(EmailMessage mailMessage) throws IOException {
        // The host and port should be fetched from mailMessage(Instance of EmailClient).
        // The host should NOT be null or empty, if it is, set to default value "35.246.112.180".
        String host = mailMessage.DestHost == null || mailMessage.DestHost.isEmpty() ? "35.246.112.180" : mailMessage.DestHost;
        commandOut("Socket Host: ".concat(host));
        // Port is fetched from GUI surface.
        int port = mailMessage.DestHostPort;
        commandOut("Socket Port: ".concat(String.valueOf(port)));

        // Create a new socket
        connection = new Socket(host, port);

        // Using Buffered stream
        // fromServer -- receive from server
        // toServer -- send to server
        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));


        // Handshake with SMTP server!
        // Check status code == 220?
        // Continue if YES.
        // Throws new Exception() if NO.
        {
            String responseMessageFromServer = fromServer.readLine(); // Try to get response message.
            // Check the status code by simply using .startWith().
            if (responseMessageFromServer == null || !responseMessageFromServer.startsWith("220"))
                throw new IOException("Status Code Error! Returned message is | ".concat(responseMessageFromServer != null ? responseMessageFromServer : ""));
            commandOut(RESPONSE.concat(responseMessageFromServer));
        }
        // It should be successfully handshake if code run here.
        // Example text like:
        // [2025-10-06 13:35:44] RESPONSE | 220 EventMachine SMTP Server

    }

    /**
     * Print on the terminal.
     *
     * @param info Info that you want to print.
     */
    public static void commandOut(String info) {
        info = info == null? "":info;
        String[] list = info.replace("\r","\n").replace("\n\n", "\n").split("\n");
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (String s : list) System.out.println("[".concat(now.format(formatter)).concat("] ").concat(s));
    }

    /**
     * Method to send an email.
     *
     * @param emailMessage Object of EmailMessage.
     * @throws IOException Any possible exception.
     */
    public void send(EmailMessage emailMessage) throws IOException {

        String content = emailMessage.Body; // Content of the email.
        String headers = emailMessage.Headers; // Headers of the request.
        String sender = emailMessage.Sender; // Person who send the email.
        String receiver = emailMessage.Recipient; // Person who receive the email.

        // HELO command
        sendCommand("HELO alice", 250);
        // MAIL FROM command
        sendCommand("MAIL FROM: ".concat(sender), 250);
        // RCPT TO command
        sendCommand("RCPT TO: ".concat(receiver), 250);
        // DATA command
        sendCommand("DATA", 354);
        // Add headers here and DISABLE the status code check.
        sendCommand(headers, NO_TARGET_STATUS_CODE);
        // A new line so that satisfied the rules.
        sendCommand("", NO_TARGET_STATUS_CODE);
        // Split the content.
        String[] lines = content.replace("\r\n", "\n").split("\n");
        for (String line : lines) {
            // Send the content line by line.
            // if one row of the content is "." and it will TRIGGER the termination!
            // so we have to solve this potential bug by adding an extra space in front of it!
            // (Although the OJ does not include this case.)
            if (".".equals(line)) line = " ".concat(line);
            sendCommand(line, NO_TARGET_STATUS_CODE);
        }

        sendCommand(".", 250);
    }

    /**
     * Close SMTP by sending "QUIT" command and close Socket instance.
     */
    public void close() {
        try {
            sendCommand("QUIT", 221);
        } catch (Exception e) {
            // If "QUIT" command can not be sent, the email could not send as well.
            // But we can still close the socket, so we separate the try-catch.
            commandOut("Unable to send QUIT command.");
            commandOut(e.getMessage());
        }
        try {
            connection.close();
        } catch (IOException e) {
            commandOut("Unable to close connection: " + e);
            // Bad case if connection cannot close...
        }
    }

    /**
     * Send command to server and check the status code
     *
     * @param commandSendToServer Command that sent to the server.
     * @param targetStatusCode    The target status code.
     * @throws IOException throw new exception when different status code occurs.
     */
    private void sendCommand(String commandSendToServer, int targetStatusCode) throws IOException {
        toServer.write(commandSendToServer);
        toServer.write(CRLF);
        toServer.flush(); // Flush is necessary to make sure it sent to server instead of buffered.
        commandOut(SEND.concat(commandSendToServer));
        // DISABLED status code check in some cases.
        if (targetStatusCode != NO_TARGET_STATUS_CODE) {
            String response = fromServer.readLine();
            // Status code check and null check as well.
            if (response == null || !response.startsWith(String.valueOf(targetStatusCode)))
                throw new IOException("Status Code Error! Returned message is | ".concat(response != null ? response : ""));
            commandOut(RESPONSE.concat(response));
        }
    }
}

