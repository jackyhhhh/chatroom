package core;

import context.Config;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class ServerConnection {
    private Socket socket;
    private int localPort;
    private String localhost;
    private InputStream in;
    private OutputStream out;
    private String tmpTableName;

    public ServerConnection(){
        try {
            System.out.println("正在连接服务器...");
            socket = new Socket(Config.getServerConfig("host"), Integer.parseInt(Config.getServerConfig("port")));
            System.out.println("连接服务器成功!!!");
            localPort = socket.getLocalPort();
            localhost = socket.getLocalAddress().getHostAddress();
            tmpTableName = "empty_msg";

            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readLine(){
        StringBuilder builder = new StringBuilder();
        String line;
        byte[] data = new byte[1024];
        int len;
        try {
            while((len=in.read(data)) > -1){
                builder.append(new String(data, 0, len));
                line = builder.toString();
                if(line.endsWith("[OVER][OVER]")){
                    line = new String(line.getBytes(), StandardCharsets.UTF_8);
                    return line.substring(0, line.length()-12);
                }
            }
        } catch (IOException e) {
            close();
            e.printStackTrace();
        }
        return null;
    }

    public void println(String line){
        byte[] data = (line+"[OVER][OVER]").getBytes(StandardCharsets.UTF_8);
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLocalAddressName(){
        return localhost+":"+localPort;
    }

    public String getTmpTableName(){
        return tmpTableName;
    }

    public void setTmpTableName(String name){
        tmpTableName = name;
    }

    public void close(){
        try {
            if(in != null){
                in.close();
            }
            if(out != null){
                out.close();
            }
            if(socket != null){
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
