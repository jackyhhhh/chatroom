package context;

import java.sql.*;

public class DBUtils {
    private static DBUtils dbUtils;
    private static Statement statement;
    private static ResultSet rs;

    private DBUtils() {
        try {
            Class.forName(Config.getDBConfig("driver"));
            System.out.println("数据库驱动加载成功!! 正在连接mysql ......");

            StringBuilder builder = new StringBuilder();
            builder.append("jdbc:mysql://");
            builder.append(Config.getDBConfig("host")).append(":");
            builder.append(Config.getDBConfig("port"));
            builder.append("?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8");
            String url = new String(builder);
            String user = Config.getDBConfig("user");
            String password = Config.getDBConfig("password");

            Connection conn = DriverManager.getConnection(url, user, password);
            System.out.println("获取到mysql连接conn: "+ conn.toString());
            statement = conn.createStatement();

        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public static DBUtils getInstance(){
        if(dbUtils == null){
            dbUtils = new DBUtils();
        }
        return dbUtils;
    }

    public boolean notExistDatabase(String dbName){
        int count = getCountInOneResult("SELECT count(*) as count FROM information_schema.SCHEMATA where SCHEMA_NAME= '"+dbName+"';");
        return count == 0;
    }

    public boolean notExistTable(String tableName){
        int count = getCountInOneResult("SELECT count(table_name) as count FROM information_schema.TABLES WHERE table_name = '"+tableName+"';");
        return count == 0;
    }

    public ResultSet executeQuery(String sql){
        try {
            rs = statement.executeQuery(sql);
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return rs;
    }

    public boolean execute(String sql){
        boolean flag = false;
        try {
            statement.execute(sql);
            flag = true;
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return flag;
    }

    public int getCountInOneResult(String checkSql){
        executeQuery(checkSql);
        String count = getStringInOneResult("COUNT");
        return Integer.parseInt(count);
    }

    public String getStringInOneResult(String columnLabel){
        String value = null;
        try {
            while(rs.next()){
                value = rs.getString(columnLabel);
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
        return value;
    }

}