package core;

import context.Config;
import context.DBManager;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Main extends JPanel {
    public static final BufferedImage background = loadImage("client.jpg");
    public static final int WIDTH = 544;
    public static final int HEIGHT = 850;
    private final ServerConnection sc;
    private int contentPos;
    private String notice = "";
    private final TypeArea ta;
    private int noticeCount = 1;

    public static BufferedImage loadImage(String name){
        try {
            return ImageIO.read(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(name)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    Main(){
        sc = new ServerConnection();
        DBManager.connect();

        ta = TypeArea.getInstance();
        JTextArea jta = ta.getJTA();
        jta.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER && e.isControlDown()){
                    sendMsgInTypeArea();
                }
            }
        });
        jta.setFocusable(true);
        this.setSize(WIDTH, HEIGHT);
        this.add(ta);
        this.setLayout(new FlowLayout(FlowLayout.LEADING, 15, 670));

    }

    public void sendMsgInTypeArea(){
        String msg = ta.getAreaText();
        System.out.println("getFromArea:  "+msg);
        if(! "".equals(msg)){
            sc.println(msg);
            ta.setAreaText("");
        }else {
            notice = Config.getNowTimestamp()+" 对不起, 不能发送空白消息 !!!";
            noticeCount = 0;
        }
        repaint();
    }

    private void paintMsgContent(Graphics g, String msg){
        g.setColor(Color.BLUE);
        int len = msg.length();
        if(len <= 40){
            g.drawString(msg, 30, contentPos);
        }else if(len <= 80) {
            g.drawString(msg.substring(0, 40), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(41), 30, contentPos);
        }else{
            g.drawString(msg.substring(0, 40), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(41, 80), 30, contentPos);
            contentPos += 20;
            g.drawString(msg.substring(80), 30, contentPos);
        }
        g.setColor(Color.BLACK);
    }

    private void paintNew8Msg(Graphics g){
        ResultSet rs = DBManager.getNew8Messages(sc);
        contentPos = 30;
        try {
            while(rs.next()){
                String timestamp = rs.getString("_timestamp");
                String msgContent = rs.getString("msg_content");
                String host = rs.getString("host");
                int port = rs.getInt("port");

                String name = host + ":" + port;
                if(name.equals(sc.getLocalAddressName())){
                    g.drawString("我("+name+")   "+timestamp+" :", 20, contentPos);
                }else {
                    g.drawString("用户("+name+")   "+timestamp+" :", 20, contentPos);
                }
                contentPos += 20;
                g.setFont(new Font("宋体",Font.BOLD,14));
                paintMsgContent(g, msgContent);
                g.setFont(new Font("宋体",Font.PLAIN,13));
                contentPos += 30;
            }
        } catch (SQLException sqlException) {
            sqlException.printStackTrace();
        }
    }

    @Override
    public void paint(Graphics g) {
        g.setFont(new Font("宋体",Font.PLAIN,13));
        g.drawImage(background, 0, 0, null);
        paintNew8Msg(g);
        if(notice.contains("断开") || notice.contains("对不起, 不能发送空白消息 !!!")){
            g.setColor(Color.RED);
        }else {
            g.setColor(Color.BLUE);
        }
        g.drawString(notice, 105,659);
        g.setColor(Color.BLACK);
    }

    public void start(){
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX();
                int y = e.getY();
                if(x >= 409 && x <= 490 && y >= 775 && y <= 801){
                    sendMsgInTypeArea();
                }

            }
        });

        JFrame frame = new JFrame("ChatRoom");
        frame.add(this);
        frame.setSize(WIDTH, HEIGHT);
        frame.setMaximizedBounds(new Rectangle(WIDTH, HEIGHT));
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        frame.setVisible(true);


        String inLine;
        while(true){
            inLine = sc.readLine();
            if(inLine == null){
                System.out.println("您已退出聊天室 !!!");
                break;
            }else if(! inLine.equals("")){
                if(inLine.startsWith("TMP_TABLE")){
                    System.out.println("inLine:"+inLine);
                    int index = inLine.indexOf("[");
                    String tableNameStr = inLine.substring(0, index);
                    String noticeStr = inLine.substring(index+12);
                    String name = tableNameStr.substring(10);
                    if(name!=null){
                        sc.setTmpTableName(name);
                    }
                    notice = noticeStr.substring(7);
                    System.out.println("收到服务端发来的个人专用临时聊天记录表名:"+name);
                    System.out.println("您已进入聊天室");
                }else if(inLine.startsWith("NOTICE")){
                    notice = inLine.substring(7);
                    noticeCount = 1;
                }
                repaint();
                if(noticeCount++ % 5 == 0){
                    notice = "";
                }
            }
        }
    }

    public static void main(String[] args) {
        Main client = new Main();
        client.start();
    }
}
