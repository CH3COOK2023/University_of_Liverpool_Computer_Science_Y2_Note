/************************************
 * Filename:  SMTPInteraction.java
 ************************************/

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 本类：跟邮件服务器建立个 SMTP 连接，然后发封邮件
 */
public class SMTPInteraction {
    /**
     * 如果发送指令后服务器不会返回status code，使用此变量。用于<code>sentCommand()</code>中
     */
    private static final int NO_TARGET_STATUS_CODE = -1;
    /**
     * 标准换行符
     */
    private static final String CRLF = "\r\n";
    /**
     * 连接到SMPT服务器的Socket
     ***/
    private final Socket connection;
    /**
     * 接收流
     */
    private final BufferedReader fromServer;
    /**
     * 发送流
     */
    private final BufferedWriter toServer;
    /**
     * 用在<code>close()</code>函数中的变量
     */
    private boolean isConnected = false;

    /**
     * 对象构造方法函数。
     * 创建一个 SMTP 交互对象。创建套接字和相关的流。初始化 SMTP 连接
     *
     * @param mailMessage
     * @throws IOException 任何可能的异常
     */
    public SMTPInteraction(EmailMessage mailMessage) throws IOException {
        /**
         * 这个 Socket的 host 和 port 应当从 EmailClient 的对象 mailMessage 交互界面中获取！
         */
        String host = mailMessage.DestHost == null || mailMessage.DestHost.isEmpty() ? "35.246.112.180" : mailMessage.DestHost;
        commandOut("Socket Host: ".concat(host));
        int port = mailMessage.DestHostPort;
        commandOut("Socket Port: ".concat(String.valueOf(port)));

        connection = new Socket(host, port);

        fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        toServer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));

        // 从服务器读取一行数据，检查 status code == 220?
        // 是：继续
        // 否：Throws new Exception();
        String responseMessageFromServer = fromServer.readLine();
        if (responseMessageFromServer == null || !responseMessageFromServer.startsWith("220"))
            throw new IOException("Status code error!");
        commandOut("RESPONSE | ".concat(responseMessageFromServer));
        // 运行到这里应当是握手成功！
        // [2025-10-06 13:35:44] RESPONSE | 220 EventMachine SMTP Server
        // String localhost = InetAddress.getLocalHost().getHostName();
        isConnected = true;
    }

    /**
     * 控制台输出
     */
    public static void commandOut(String info) {
        // 获取当前时间
        LocalDateTime now = LocalDateTime.now();

        // 定义时间格式
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 格式化时间并返回
        System.out.println("[".concat(now.format(formatter)).concat("] ").concat(info));
    }

    /* Send message. Write the correct SMTP-commands in the
       correct order. No checking for errors, just throw them to the
       caller. */
    public void send(EmailMessage emailMessage) throws IOException {
        String context = emailMessage.Body;
        String sender = emailMessage.Sender;
        String receiver = emailMessage.Recipient;
        String subject = "";
        try{
            subject = emailMessage.Headers.split("\r\n")[2].split(" ")[1];
        }catch (Exception ignored){
            commandOut("ERROR    | Can not get subject.");
        }

        sendCommand("HELO alice",250);
        sendCommand("MAIL FROM: ".concat(sender),250);
        sendCommand("RCPT TO: ".concat(receiver),250);
        sendCommand("DATA".concat(receiver),354);
        sendCommand("SUBJECT: ".concat(subject),NO_TARGET_STATUS_CODE);
        String[] lines = context.replace("\r\n", "\n").split("\n");
        for (String line : lines) {
            if(".".equals(line))
                line = " ".concat(line);
            sendCommand(line,NO_TARGET_STATUS_CODE);
        }
        sendCommand(".",250);
    }

    /**
     * 关闭 SMTP 服务器连接
     */
    public void close() {
        isConnected = false;
        try {
            sendCommand("QUIT", 221);
            connection.close();
        } catch (IOException e) {
            System.out.println("Unable to close connection: " + e);
            isConnected = true;
        }
    }

    /**
     * 把 SMTP 命令发给服务器
     * @param commandSendToServer 给服务器发送的命令
     * @param targetStatusCode 目标 Status Code， 如果不一样，抛出异常。如果为 NO_TARGET_STATUS_CODE 则不检查。
     * @throws IOException 非目标Status Code 异常
     */
    private void sendCommand(String commandSendToServer, int targetStatusCode) throws IOException {
        toServer.write(commandSendToServer);
        toServer.write(CRLF);
        toServer.flush();
        commandOut("SEND     | ".concat(commandSendToServer));
        if(targetStatusCode!= NO_TARGET_STATUS_CODE){
            String response = fromServer.readLine();
            if(response == null || !response.startsWith(String.valueOf(targetStatusCode)))
                throw new IOException("Status Code Error!");
            commandOut("RESPONSE | ".concat(response));
        }
    }
}

