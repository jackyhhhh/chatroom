package managers;

import context.Config;
import context.DBUtils;
import core.ClientConnection;

public class DBManager {
    private static final DBUtils dbUtils;
    static {
        dbUtils = DBUtils.getInstance();
    }

    public static void init(){
        checkDatabaseExist();
        checkTableHistoryMsgExist();
        checkTableEmptyMsgExist();
    }

    private static void checkDatabaseExist(){
        if(dbUtils.notExistDatabase(Config.getDBConfig("db"))){
            System.out.println("数据库(chatroom) 不存在, 正在建库...");
            if(dbUtils.execute(" CREATE DATABASE `chatroom` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;")){
                System.out.println("数据库(chatroom) 创建成功!!!");
            }else {
                System.out.println("数据库(chatroom) 创建失败!!!");
            }
        }

        dbUtils.execute("use "+ Config.getDBConfig("db")+";");
    }

    private static void checkTableHistoryMsgExist(){
        if(dbUtils.notExistTable("history_msg")){
            System.out.println("历史聊天记录表(history_msg)不存在, 正在建表...");
            String sql = "CREATE TABLE `history_msg` ("
                    + "`id` int(5) NOT NULL AUTO_INCREMENT,"
                    + "`_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "`host` varchar(15) NOT NULL,"
                    + "`port` int NOT NULL,"
                    + " `msg_content` text NOT NULL,"
                    + "UNIQUE KEY `id` (`id`)"
                    + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            if(dbUtils.execute(sql)) {
                System.out.println("历史聊天记录表(history_msg) 创建成功!!!");
            } else {
                System.out.println("历史聊天记录表(history_msg) 创建失败!!!");
            }
        }
    }

    private static void checkTableEmptyMsgExist(){
        if(dbUtils.notExistTable("empty_msg")){
            System.out.println("空聊天记录表(empty_msg)不存在, 正在建表...");
            String sql = "CREATE TABLE `empty_msg` ("
                    + "`id` int(5) NOT NULL AUTO_INCREMENT,"
                    + "`_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "`host` varchar(15) NOT NULL,"
                    + "`port` int NOT NULL,"
                    + " `msg_content` text NOT NULL,"
                    + "UNIQUE KEY `id` (`id`)"
                    + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
            if(dbUtils.execute(sql)) {
                System.out.println("空聊天记录表(empty_msg) 创建成功!!!");
            } else {
                System.out.println("空聊天记录表(empty_msg) 创建失败!!!");
            }
        }
    }


    public static boolean createTableTmpCurrentMsg(ClientConnection cc){
        String tmpTableName = cc.getTmpTableName();
        dbUtils.execute("DROP TABLE IF EXISTS "+tmpTableName);
        String sql = "CREATE TABLE `"+tmpTableName+"` ("
                + "`id` int(5) NOT NULL AUTO_INCREMENT,"
                + "`_timestamp` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                + "`host` varchar(15) NOT NULL,"
                + "`port` int NOT NULL,"
                + " `msg_content` text NOT NULL,"
                + "UNIQUE KEY `id` (`id`)"
                + ")ENGINE=InnoDB DEFAULT CHARSET=utf8;";
        if(dbUtils.execute(sql)) {
            System.out.println("临时聊天记录表("+tmpTableName+") 创建成功!!!");
            return true;
        } else {
            System.out.println("临时聊天记录表("+tmpTableName+") 创建失败!!!");
            return false;
        }
    }

    public static void deleteTableTmpCurrentMsg(ClientConnection cc){
        String tmpTableName = cc.getTmpTableName();
        String sql = "DROP TABLE IF EXISTS "+tmpTableName;
        if(dbUtils.execute(sql)){
            System.out.println("临时聊天ni记录表("+tmpTableName+") 删除成功!!!");
        }else{
            System.out.println("临时聊天记录表("+tmpTableName+") 删除失败!!!");
        }
    }

    public static void saveMsg(ClientConnection cc, String msg){
        String host = cc.getHost();
        int port = cc.getPort();
        String sql1 = "INSERT INTO history_msg(host, port, msg_content) values(\""+host+"\","+port+",\""+msg+"\");";
        String sql2 = "INSERT INTO "+cc.getTmpTableName()+" (host, port, msg_content) values(\""+host+"\","+port+",\""+msg+"\");";
        dbUtils.execute(sql1);
        dbUtils.execute(sql2);
    }

}
