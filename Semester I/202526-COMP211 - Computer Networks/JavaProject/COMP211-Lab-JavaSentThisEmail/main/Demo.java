import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class Demo {
    public static void main(String[] args) throws Exception {
        // 与邮件服务器建立TCP连接
        Socket socket = new Socket("35.246.112.180", 1025);

        // 创建BufferedReader用于读取服务器响应
        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        // 读取服务器的欢迎信息
        String response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("220")) {
            throw new Exception("未从服务器收到220回复。");
        }

        // 获取socket的输出流用于发送命令
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        // 发送HELO命令并获取服务器响应
        String command = "HELO alice\r\n";
        System.out.print(command);
        os.writeBytes(command);
        os.flush();
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("未从服务器收到250回复。");
        }

        // 发送MAIL FROM命令
        command = "MAIL FROM: <sender@example.com>\r\n";
        System.out.print(command);
        os.writeBytes(command);
        os.flush();
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("MAIL FROM命令未收到250回复。");
        }

        // 发送RCPT TO命令
        command = "RCPT TO: <receiver@example.org>\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("RCPT TO命令未收到250回复。");
        }

        // 发送DATA命令
        command = "DATA\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("354")) {
            throw new Exception("DATA命令未收到354回复。");
        }

        // 发送邮件内容
        os.writeBytes("SUBJECT: Hello\r\n");  // 邮件主题
        os.writeBytes("\r\n");  // 主题与正文之间需要空行
        os.writeBytes("Hi Bob, How's the weather?\r\n");  // 邮件正文
        os.writeBytes("Alice.1523\r\n");
        os.writeBytes(".\r\n");  // 单个句点表示邮件内容结束
        System.out.println("发送邮件内容...");

        // 读取服务器对邮件内容的响应
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("250")) {
            throw new Exception("邮件内容发送后未收到250回复。");
        }

        // 发送QUIT命令
        command = "QUIT\r\n";
        System.out.print(command);
        os.writeBytes(command);
        response = br.readLine();
        System.out.println(response);
        if (!response.startsWith("221")) {
            throw new Exception("QUIT命令未收到221回复。");
        }

        // 关闭连接
        os.close();
        br.close();
        socket.close();
    }
}
