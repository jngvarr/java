package ru.gb;

import javax.swing.*;
import java.awt.*;

public class Server extends JFrame {
    public static final int WINDOW_HEIGHT = 400;
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_POSITION_X = 600;
    public static final int WINDOW_POSITION_Y = 600;
    JButton jButtonStart = new JButton("Start");
    JButton jButtonStop = new JButton("Stop");

    Server() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSITION_X, WINDOW_POSITION_Y);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Chat Server");
        setVisible(true);
        setResizable(false);

        JPanel jPanelBottom = new JPanel(new GridLayout(1,2));
        jPanelBottom.add(jButtonStart);
        jPanelBottom.add(jButtonStop);
        add(jPanelBottom, BorderLayout.SOUTH);
    }

}
