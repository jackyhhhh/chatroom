package core;

import context.Config;
import managers.ConnectionsManager;
import managers.DBManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ClientConnection {
    private final Socket socket;
    private final InputStream in;
    private final OutputStream out;
    private final String host;
    private final int port;
    private final String tmpTableName;

    public ClientConnection(Socket socket){
        this.socket = socket;
        this.host = socket.getInetAddress().getHostAddress();
        this.port = socket.getPort();
        this.tmpTableName = "current_msg_" + System.currentTimeMillis();
        try {
            in = socket.getInputStream();
            out = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException();
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
                    return line.substring(0, line.length()-12);
                }
            }
        } catch (IOException e) {
            close();
            ConnectionsManager.remove(this);
            System.out.println(Config.getNowTimestamp()+"  用户(" +getName()+ ")  断开了连接!!!");
            ConnectionsManager.sendNoticeToAll(Config.getNowTimestamp()+"  用户(" +getName()+ ")  断开了连接!!!");
            DBManager.deleteTableTmpCurrentMsg(this);
        }
        return "";
    }

    public void println(String line){
        byte[] data = (line+"[OVER][OVER]").getBytes();
        try {
            out.write(data);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public OutputStream getOutputStream(){
        return out;
    }

    public String getName(){
        return host + ":" + port;
    }

    public String getTmpTableName(){
        return tmpTableName;
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
