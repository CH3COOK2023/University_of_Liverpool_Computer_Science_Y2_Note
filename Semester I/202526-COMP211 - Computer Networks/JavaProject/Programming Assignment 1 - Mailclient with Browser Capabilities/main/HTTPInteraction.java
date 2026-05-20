/*************************************
 *  Filename:  HTTPInteraction.java
 **+***+******************************/

import java.io.*;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Class for downloading one object from an Http server.
 * Student ID:201945844
 */
public class HTTPInteraction {
    // local debug main method.
	public static void main(String[] args) throws Exception {
		HTTPInteraction a = new HTTPInteraction("comp211.gairing.com"); // no "/" case
        commandOut(a.send());
	}

    private static final int HTTP_PORT = 80;
    private static final String CRLF = "\r\n";
    private String host;
    private String path;


    /**
     * Constructor, construct an interaction Object of this class.
     *
     * @param url request URL.
     */
    public HTTPInteraction(String url){
        // Split URL with host name and path.
        // Path might not exist, but we have to make sure it starts with '/'
        url = url.contains("/")?url:url.concat("/");
        // Split URL.
        String[] hostAndPath = url.split("/", 2);
        // Generally this case will NOT happen.
        if (hostAndPath.length != 2)
		{
			commandOut("URL can not be analysed.");
			return;
		}
        host = hostAndPath[0];
        path = "/".concat(hostAndPath[1]);
    }

	/**
     * Send HTTP request and waiting for response. If OK, then return as String.
     * Return Exception if something wrong. Exceptions will not be caught.
	 * @return response as an Object
	 * @throws IOException Any possible Exception.
	 */
	public String send() throws IOException {
        // Socket connection.
        Socket connection = new Socket(host,HTTP_PORT);
        // Using bufferedWR here is much easier I think.
        BufferedReader fromServer = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        BufferedWriter toServer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
        commandOut("Connecting server: " + host + CRLF);

        // Send requestMessage to http server
        // One command otherwise the server will return 429:Too many request.
        String command = String.format("GET %s HTTP/1.1%s",path,CRLF);
        commandOut(command);
		toServer.write(command);
		toServer.flush();

        command = String.format("Host: %s%s",host,CRLF);
        commandOut(command);
        toServer.write(command);
        toServer.flush();
        // To CRLF to make sure that you confirm submit.
        command = CRLF;
        commandOut(command);
        toServer.write(command);
        toServer.flush();

        command = CRLF;
        commandOut(command);
        toServer.write(command);
        toServer.flush();

        // variable
		String statusLine;    // status line
		int statusCode;        // status code
		String headers = "";    // headers
		int bodyLength;    // length of body // This var is not used in test case.
        String body;
        // read from server
        List<String> responsesInLines = new ArrayList<>();
        String tmpVar;
        connection.setSoTimeout(3_000); // 3_000 ms is OK but longer might be more stable.
        while (true){
             try{
                 tmpVar = fromServer.readLine();
                 commandOut(tmpVar);
                 if(tmpVar == null) {
                     break;
                 }
                 responsesInLines.add("".concat(tmpVar).concat(CRLF));
             }catch (Exception timeOutException){
                 break;
             }
        }
        // Parse status code.
        try{
            statusLine = responsesInLines.get(0);
            statusCode = Integer.parseInt(statusLine.split(" ",3)[1]);
        }catch (Exception e){
            throw new IOException("Cannot resolve status line and status code!");
        }
        // Return exception if status code is NOT 200.
		if(statusCode!=200){
			connection.close();
			return statusLine;
		}
        commandOut("Status line: ".concat(statusLine));
        commandOut("Status code: ".concat(String.valueOf(statusCode)));
        // Parse the return message.
        try{
            StringBuilder bodyStringBuilder = new StringBuilder(); // StringBuilder to get headers.
            int breakLineIndex = -1;
            for (String row : responsesInLines) {
                breakLineIndex++;
                if(CRLF.equals(row))
                    break;
                if(row.startsWith("Content-Length:"))
                    bodyLength = Integer.parseInt(row.replace(CRLF,"").split(" ", 2)[1]);
            }

            for(int i = breakLineIndex+1;i<responsesInLines.size();i++)
                bodyStringBuilder.append(responsesInLines.get(i));
            body = bodyStringBuilder.toString();
        }catch (Exception e){
            // Generally this will NOT happen.
            throw new IOException("Cannot resolve headers and Content-Length value!");
        }

        commandOut("Headers:\n".concat(headers).concat(CRLF));

        // close the connection.
        commandOut("Done reading file. Closing connection.");
        connection.close();
        return body.replace(CRLF,"\n");
    }

    /**
     * This function only for debug locally.
     * @param info Message you want to show in terminal.
     */
    public static void commandOut(String info) {
        if(info!=null)
            info = info.replace("\r","\\r").replace("\n","\\n");
        info = info == null? "":info; // Assert not null.
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("[".concat(now.format(formatter)).concat("] ").concat(info));
    }
}