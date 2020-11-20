package core;

import javax.swing.*;
import java.awt.*;

public class TypeArea extends JPanel {
    private static final JTextArea jta =  new JTextArea(5, 60);;
    private static TypeArea ta;

    private TypeArea(){
        init();
    }

    private void init(){
        jta.setLineWrap(true);
        jta.setForeground(Color.BLACK);
        jta.setFont(new Font("楷体",Font.BOLD,14));
        jta.setBackground(Color.WHITE);

        JScrollPane jsp = new JScrollPane(jta);
        Dimension size = jta.getPreferredSize();
        jsp.setBounds(50, 50, size.width, size.height);
        this.add(jsp);
    }

    public static TypeArea getInstance(){
        if(ta == null){
            ta = new TypeArea();
        }
        return ta;
    }

    public String getAreaText(){
        return jta.getText();
    }

    public void setAreaText(String s){
        jta.setText(s);
    }

    public JTextArea getJTA(){
        return jta;
    }
}
