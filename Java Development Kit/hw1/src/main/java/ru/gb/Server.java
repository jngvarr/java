package ru.gb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class Server extends JFrame {
    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_POSITION_X = 600;
    public static final int WINDOW_POSITION_Y = 600;
    JButton jButtonStart = new JButton("Start");
    JButton jButtonStop = new JButton("Stop");
    boolean isServerWorking;
    JTextArea jTextArea;

    Server() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSITION_X, WINDOW_POSITION_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Chat Server");
        setVisible(true);
        setResizable(false);

        JPanel jPanelBottom = new JPanel(new GridLayout(1, 2));
        jPanelBottom.add(jButtonStart);
        jPanelBottom.add(jButtonStop);
        add(jPanelBottom, BorderLayout.SOUTH);

        jTextArea = new JTextArea();
        add(jTextArea);

        jButtonStart.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (isServerWorking) {
                    System.out.println("Server already started");
                    jTextArea.append("Server already started\n");
                } else {
                    System.out.println("Server was started");
                    jTextArea.append("Server was started\n");
                    isServerWorking = true;
                }
            }
        });
        jButtonStop.addActionListener(new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isServerWorking) {System.out.println("Server already stopped");
                jTextArea.append("Server already stopped\n");}
                else {
                    System.out.println("Server was stopped");
                jTextArea.append("Server was stopped\n");}
                    isServerWorking = false;
                }
        });
    }

}
