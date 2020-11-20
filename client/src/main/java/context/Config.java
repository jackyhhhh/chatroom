package context;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Config {
    private static final Map<String, String> DBConfig = new HashMap<>();
    private static final Map<String, String> serverConfig = new HashMap<>();
    private static final Element root = getRootFromXML();
    static {
        init();
    }

    private static void init(){
        initDBConfig();
        initServerConfig();
    }

    private static void initDBConfig(){
        Element database = root.element("database");
        DBConfig.put("driver", database.elementTextTrim("jdbc_driver"));
        DBConfig.put("host", database.elementTextTrim("db_host"));
        DBConfig.put("port", database.elementTextTrim("db_port"));
        DBConfig.put("db", database.elementTextTrim("db_name"));
        DBConfig.put("user", database.elementTextTrim("db_user"));
        DBConfig.put("password", database.elementTextTrim("db_password"));
    }

    private static Element getRootFromXML(){
        SAXReader reader = new SAXReader();
        Element root = null;
        try {
            Document doc = reader.read(Config.class.getClassLoader().getResourceAsStream("config.xml"));
            root = doc.getRootElement();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        return root;
    }

    private static void initServerConfig(){
        Element server = root.element("server");
        serverConfig.put("host", server.attributeValue("host"));
        serverConfig.put("port", server.attributeValue("port"));
    }

    public static String getServerConfig(String name){
        return serverConfig.get(name);
    }

    public static String getDBConfig(String name){
        return DBConfig.get(name);
    }

    public static String getNowTimestamp(){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date now = new Date();
        return fmt.format(now);
    }

}
