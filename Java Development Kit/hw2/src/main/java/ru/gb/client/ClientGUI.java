package ru.gb.client;

import ru.gb.server.Server;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class ClientGUI extends JFrame implements ClientView {
    public static final int WINDOW_HEIGHT = 200;
    public static final int WINDOW_WIDTH = 400;
    JButton jButtonSend, jButtonLogin;
    JPanel jPanelMessages, jPanelLoginData, jPanelMainPanel;
    JTextArea jTextAreaMessages;
    JTextField jTextFieldLogin, jTextFieldMessage, jTextFieldIP, jTextFieldPort;
    Server server;
    private String name;
    Client client;
    JPasswordField jPasswordField;

    public ClientGUI(Server server) {
        //this.server = server;
        this.client = new Client(this, server);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Chat Client");
        setLocation(server.getX() + 500, server.getY());
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);

        createPanel();
        setVisible(true);


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

    }

    private void createPanel() {
        add(createBottomPanel(), BorderLayout.SOUTH);
        add(createUpPanel(), BorderLayout.NORTH);
        jTextAreaMessages = new JTextArea();
        add(jTextAreaMessages);
    }

    private Component createUpPanel() {
        jPanelLoginData = new JPanel(new GridLayout(2, 3));
        jTextFieldLogin = new JTextField("Фёдор Михалыч");
        jPasswordField = new JPasswordField("password");
        jTextFieldIP = new JTextField("192.168.0.1");
        jTextFieldPort = new JTextField("8080");
        jButtonLogin = new JButton("Login");

        jButtonLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTitle(getTitle()+" ("+ jTextFieldLogin.getText()+")");
                connectToServer();
            }
        });
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


        jButtonSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });

        jPanelMessages.add(jTextFieldMessage);
        jPanelMessages.add(jButtonSend);
        return jPanelMessages;
    }

    public void sendMessage() {
        String message = jTextFieldMessage.getText();
        client.sendMessage(message);
        jTextFieldMessage.setText("");
    }

    private Component createLog() {
        jTextAreaMessages = new JTextArea();
        jTextAreaMessages.setEditable(false);
        return new JScrollPane(jTextAreaMessages);
    }

    public void answer(String text) {
        appendLog(text);
    }

    private void connectToServer() {
        if (client.connectToServer(jTextFieldLogin.getText())) {
            jPanelLoginData.setVisible(false);
        }
    }

    public void disconnectFromServer() {
        hidePanel(true);
        client.disconnect(client);
    }

    private void hidePanel(boolean visible) {
        jPanelLoginData.setVisible(visible);
    }

    @Override
    public void showMessage(String text) {
       appendLog(text);
    }


    private void appendLog(String text) {
        jTextAreaMessages.append(text+"\n");
    }

    @Override
    public int getDefaultCloseOperation() {
        disconnectFromServer();
        return super.getDefaultCloseOperation();
    }
}
