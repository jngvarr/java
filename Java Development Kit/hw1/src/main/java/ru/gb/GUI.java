package ru.gb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GUI extends JFrame {
    public static final int WINDOW_HEIGHT = 200;
    public static final int WINDOW_WIDTH = 400;
    public static final int WINDOW_POSITION_X = 800;
    public static final int WINDOW_POSITION_Y = 300;
    JButton jButtonSend;
    JButton jButtonLogin;

    JPanel jPanelMessages;
    JPanel jPanelLoginData;
    JTextArea jTextAreaMessages;
    JTextField jTextFieldLogin;
    JTextField jTextFieldMessage;
    JList jList;

    public GUI(Server server) {
        setTitle("Chat Client");
        setLocationRelativeTo(server);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        add(createBottomPanel(), BorderLayout.SOUTH);
        add(createUpPanel(), BorderLayout.NORTH);
        jTextAreaMessages = new JTextArea();
        jList = new JList<>();
        add(jTextAreaMessages);
        //jList.setListData();

        jButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.jTextArea.append(jTextFieldMessage.getText() + "\n");
                System.out.println(jTextFieldMessage.getText());
                jTextFieldMessage.setText("");
                System.out.println(jTextFieldLogin.getText() + " 3");
            }
        });
        jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanelLoginData.setVisible(false);
                server.setTitle(server.getTitle() + " (" + /*jTextFieldLogin.getText() +*/ ")");
                System.out.println(jTextFieldLogin.getText() + " 1");
            }
        });
    }

    private Component createUpPanel() {
        jPanelLoginData = new JPanel(new GridLayout(2, 3));
        JTextField jTextFieldLogin = new JTextField("Фёдор Михалыч");
        JPasswordField jPasswordField = new JPasswordField("password");
        JTextField jTextFieldIP = new JTextField("192.168.0.1");
        JTextField jTextFieldPort = new JTextField("8080");
        jButtonLogin = new JButton("Login");
        System.out.println(jTextFieldLogin.getText() + " 2");
        jPanelLoginData.add(jTextFieldIP);
        jPanelLoginData.add(jTextFieldPort);
        jPanelLoginData.add(jTextFieldLogin);
        jPanelLoginData.add(jPasswordField);
        jPanelLoginData.add(jButtonLogin);

        return jPanelLoginData;
    }

    Component createBottomPanel() {
        jButtonSend = new JButton("Send");
        jTextFieldMessage = new JTextField();
        jPanelMessages = new JPanel(new GridLayout(1, 2));
        jPanelMessages.add(jTextFieldMessage);
        jPanelMessages.add(jButtonSend);
        return jPanelMessages;
    }


}
