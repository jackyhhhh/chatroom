package managers;

import core.ClientConnection;

import java.util.ArrayList;
import java.util.List;

public class ConnectionsManager {
    private static final List<ClientConnection> allConns = new ArrayList<>();

    public static void add(ClientConnection cc){
        synchronized (allConns){
            allConns.add(cc);
        }
    }

    public static synchronized void remove(ClientConnection cc){
        synchronized (allConns){
            allConns.remove(cc);
        }
    }

    public static void sendOrderToAll(int order){
        synchronized (allConns){
            for(ClientConnection cc : allConns){
                cc.println("ORDER-"+order);
            }
        }
    }

    public static void sendNoticeToAll(String notice){
        synchronized (allConns){
            for(ClientConnection cc : allConns){
                cc.println("NOTICE-"+notice);
            }
        }
    }

    public static void sendTmpTableNameToClient(ClientConnection cc){
        synchronized (cc.getOutputStream()){
            String tmpTableName = cc.getTmpTableName();
            cc.println("TMP_TABLE-"+tmpTableName);
            System.out.println("已发送临时聊天记录表的表名到对应客户端!");
        }
    }
}
