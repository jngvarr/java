package ru.gb.server.client;

import ru.gb.server.server.Server;
import ru.gb.server.server.ServerGUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientGUI extends JFrame implements ClientView{
    public static final int WINDOW_HEIGHT = 200;
    public static final int WINDOW_WIDTH = 400;
    JButton jButtonSend, jButtonLogin;
    JPanel jPanelMessages, jPanelLoginData;
    JTextArea jTextAreaMessages;
    JPasswordField jPasswordField;
    JTextField jTextFieldLogin, jTextFieldMessage, jTextFieldIP,jTextFieldPort;
    JScrollPane jScrollPane;
    boolean isLogged;
    private Client client;

    public ClientGUI(Server server) {
        this.client = new Client(this, server);

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
                    client.sendMessage(result);
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
                jTextAreaMessages.setText(String.valueOf(client.readLog()));
            }
        });
    }

    private Component createUpPanel() {
        jPanelLoginData = new JPanel(new GridLayout(2, 3));
        jTextFieldLogin = new JTextField("Фёдор Михалыч");
        jPasswordField = new JPasswordField("password");
        jTextFieldIP = new JTextField("192.168.0.1");
        jTextFieldPort = new JTextField("8080");
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

    @Override
    public void showMessage(String text) {

    }

    @Override
    public void disconnectFromServer() {

    }
    @Override
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING){
            disconnectFromServer();
        }
    }
}
