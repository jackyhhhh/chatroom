package core;

import context.Config;
import context.Order;
import managers.ConnectionsManager;
import managers.DBManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {
    private ServerSocket serverSocket;
    Main(){
        try {
            System.out.println("正在启动服务器......");
            int port = Integer.parseInt(Config.getServerConfig("port"));
            serverSocket = new ServerSocket(port);
            System.out.println("服务器启动成功! 正在初始化数据库......");
            DBManager.init();
            System.out.println("数据库初始化成功 !!!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void start(){
        System.out.println("正在等待客户端的连接...");
        while(true){
            try {
                Socket socket = serverSocket.accept();
                ClientHandler handler = new ClientHandler(socket);
                Thread t = new Thread(handler);
                t.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static class ClientHandler implements Runnable{
        private final Socket socket;
        ClientHandler(Socket socket){
            this.socket = socket;
        }

        @Override
        public void run() {
            ClientConnection conn = new ClientConnection(socket);
            ConnectionsManager.add(conn);
            if(DBManager.createTableTmpCurrentMsg(conn)){
                ConnectionsManager.sendTmpTableNameToClient(conn);
            }
            String onlineNotice = Config.getNowTimestamp()+"  用户("+conn.getName()+") 上线了!!";
            ConnectionsManager.sendNoticeToAll(onlineNotice);
            System.out.println(onlineNotice);
            String line;
            while (!(line = conn.readLine()).equals("")) {
                System.out.println("成功收到:"+ line);
                DBManager.saveMsg(conn, line);
                ConnectionsManager.sendOrderToAll(Order.REFRESH);
            }
        }
    }

    public static void main(String[] args) {
        Main server = new Main();
        server.start();
    }

}
