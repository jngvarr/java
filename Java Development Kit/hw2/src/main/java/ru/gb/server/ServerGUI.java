package ru.gb.server;

import ru.gb.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ServerGUI extends JFrame implements ServerView {
    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_POSITION_X = 600;
    public static final int WINDOW_POSITION_Y = 300;
    public static final String SERVER_ALREADY_STARTED = "Server already started";
    public static final String SERVER_WAS_STARTED = "Server was started";
    public static final String SERVER_ALREADY_STOPPED = "Server already stopped";
    public static final String SERVER_WAS_STOPPED = "Server was stopped";
    JButton jButtonStart = new JButton("Start");
    JButton jButtonStop = new JButton("Stop");
    JTextArea jTextArea;
    Server server;

    public ServerGUI() {
        this.server = new Server(this);
        jTextArea = new JTextArea();
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSITION_X, WINDOW_POSITION_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Chat Server");
        setResizable(false);

        createServerWindow();
        setVisible(true);
    }

    Component createServerWindow() {
        JPanel jPanelBottom = new JPanel(new GridLayout(1, 2));
        jPanelBottom.add(jButtonStart);
        jPanelBottom.add(jButtonStop);
        add(jTextArea);
        add(jPanelBottom, BorderLayout.SOUTH);


        jButtonStart.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (server.isWorking()) {
                    System.out.println(SERVER_ALREADY_STARTED);
                    showMessage(SERVER_ALREADY_STARTED);
                } else {
                    System.out.println(SERVER_WAS_STARTED);
                    showMessage(SERVER_WAS_STARTED);
                    server.switchServer(server.isWorking());
                }
            }
        });

        jButtonStop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!server.isWorking()) {
                    System.out.println(SERVER_ALREADY_STOPPED);
                    showMessage(SERVER_ALREADY_STOPPED);
                } else {
                    System.out.println(SERVER_WAS_STOPPED);
                    showMessage(SERVER_WAS_STOPPED);
                    server.switchServer(server.isWorking());
                    Iterator<Client> clientsIterator = server.getClientsList().iterator();
                    while (clientsIterator.hasNext()) {
                        server.serverDown(clientsIterator.next());
                        clientsIterator.remove();
                    }
                }
            }
        });
        return jPanelBottom;
    }

    public Server getServer() {
        return server;
    }

    @Override
    public void showMessage(String text) {
        jTextArea.append(text + "\n");
    }
}
