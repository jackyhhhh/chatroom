package context;

import core.ServerConnection;

import java.sql.ResultSet;

public class DBManager {
    private static final DBUtils dbUtils;
    static {
        dbUtils = DBUtils.getInstance();
    }

    public static void connect(){
        System.out.println("正在连接数据库chatroom......");
        if(dbUtils.execute("USE chatroom;")){
            System.out.println("连接数据库chatroom成功!!!");
        }else {
            System.out.println("连接数据库chatroom失败!!!");
        }
    }

    public static ResultSet getNew8Messages(ServerConnection sc){
        String tmpTableName = sc.getTmpTableName();
        String sql = "SELECT * FROM (SELECT _timestamp, port, msg_content, host FROM "+tmpTableName+" ORDER BY _timestamp DESC LIMIT 8) AS aa ORDER BY _timestamp;";
        return dbUtils.executeQuery(sql);
    }
}
