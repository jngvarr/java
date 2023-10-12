package ru.gb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
    //JList jList;

    public GUI(Server server) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setLocationRelativeTo(server);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        add(createBottomPanel(), BorderLayout.SOUTH);
        add(createUpPanel(), BorderLayout.NORTH);
        jTextAreaMessages = new JTextArea();
        //jList = new JList<>();
        add(jTextAreaMessages);
        //jList.setListData();
        String qq = this.jTextFieldLogin.getText();
        //setFocusable(true);
        Logger logger = new Logger();
        jTextFieldMessage.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    jButtonSend.doClick();
                }
            }

            @Override
            public void keyPressed(KeyEvent e) {

            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        jButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                server.jTextArea.append(jTextFieldMessage.getText() + "\n");
                String result = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " +
                        jTextFieldLogin.getText() + ": " + jTextFieldMessage.getText() + "\n";
                System.out.println(jTextFieldMessage.getText());
                jTextAreaMessages.append(result);
                logger.writeLogToFile(result);
                jTextFieldMessage.setText("");
            }
        });
        jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jPanelLoginData.setVisible(false);
                server.setVisible(false);
                setTitle(getTitle() + " (" + jTextFieldLogin.getText() + ")");
                // jPanelMessages.setFocusable(true);
                jTextAreaMessages.setText(String.valueOf(logger.readLogTFromFile()));
            }
        });
    }

    private Component createUpPanel() {
        jPanelLoginData = new JPanel(new GridLayout(2, 3));
        jTextFieldLogin = new JTextField("Фёдор Михалыч");
        JPasswordField jPasswordField = new JPasswordField("password");
        JTextField jTextFieldIP = new JTextField("192.168.0.1");
        JTextField jTextFieldPort = new JTextField("8080");
        jButtonLogin = new JButton("Login");
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
