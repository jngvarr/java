package ru.gb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GameWindow extends JFrame {
    public static final int WINDOW_HEIGHT = 555;
    public static final int WINDOW_WIDTH = 507;
    public static final int WINDOW_POSX = 800;
    public static final int WINDOW_POSY = 300;

    JButton btnStart = new JButton("New Game");
    JButton btnExit = new JButton("Exit");
    Map map;
    SettingsWindow settings;
    boolean isGameOver;
    boolean isInitialized;

    GameWindow() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(WINDOW_POSX, WINDOW_POSY);
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("TicTacToe"); //заголовок окна
        setResizable(false);   //запрет на изменение размера окна

        settings = new SettingsWindow(this); // передача методу объекта, вызывающего этот метод
        map = new Map();
        add(map);
//        settings.setVisible(true); // видимость окна Settings
        JPanel panelBottom = new JPanel(new GridLayout(1, 2)); //GridLayout() - компановщик окон (размещает элементы в окне) количество (строк и колонок)
        panelBottom.add(btnStart);
        panelBottom.add(btnExit);
        add(panelBottom, BorderLayout.SOUTH); // BorderLayout.SOUTH - тоже компановщик SOUTH - расположение элемента (внизу)
        setVisible(true);  //видимость основного окна
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                settings.setVisible(true); // видимость окна Settings
            }
        });
    }

    void startNewGame(int mode, int fSzX, int fSzY, int wLen) {
        map.startNewGame(mode, fSzX, fSzY, wLen);
    }

}