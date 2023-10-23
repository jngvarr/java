package ru.gb.client;

import ru.gb.server.Server;

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
    JButton jButtonSend, jButtonLogin;
    JPanel jPanelMessages, jPanelLoginData;
    JTextArea jTextAreaMessages;
    JTextField jTextFieldLogin, jTextFieldMessage;
    boolean isLogged;
    JScrollPane jScrollPane;

    public GUI(Server server) {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setLocationRelativeTo(server);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        jScrollPane = new JScrollPane();
        mainPanel.add(jScrollPane, BorderLayout.CENTER);

        mainPanel.add(createBottomPanel(), BorderLayout.SOUTH);
        mainPanel.add(createUpPanel(), BorderLayout.NORTH);
        jTextAreaMessages = new JTextArea();
        mainPanel.add(jTextAreaMessages, BorderLayout.CENTER);
//        add(jTextAreaMessages);
        add(mainPanel);
        jScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        // @TODO добавить скролинг окна
        //setFocusable(true);
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
                if (isLogged) {
                    String result = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + ": " +
                            jTextFieldLogin.getText() + ": " + jTextFieldMessage.getText() + "\n";
                    server.message(result);
                    System.out.println(jTextFieldMessage.getText());
                    jTextAreaMessages.append(result);

                    jTextFieldMessage.setText("");
                }
            }
        });
        jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                isLogged = true;
                jPanelLoginData.setVisible(false);
                server.setVisible(false);
                setTitle(getTitle() + " (" + jTextFieldLogin.getText() + ")");
                jTextAreaMessages.setText(String.valueOf(server.readLog()));
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

    private Component createLog() {
        jTextAreaMessages = new JTextArea();
        jTextAreaMessages.setEditable(false);
        return new JScrollPane(jTextAreaMessages);
    }
}
