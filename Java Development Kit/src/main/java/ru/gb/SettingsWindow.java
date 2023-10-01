package ru.gb;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SettingsWindow extends JFrame {
    public static final int WINDOW_HEIGHT = 230;
    public static final int WINDOW_WIDTH = 350;
    private static final String CURRENT_WIN_VALUE = " Установленная длина: ";
    private static final String CURRENT_FIELD_SIZE = " Текущий размер поля: ";
    private final int MIN_SIZE = 3;
    JButton btnStart;
    //GameWindow gameWindow = new GameWindow();
    JLabel lblGameModeChoice;
    JPanel mainPanel;
    JRadioButton jRadioButtonHuman;
    JRadioButton jRadioButtonAi;
    ButtonGroup buttonGroupMode;
    JLabel labelFieldSize;
    JLabel currentSize;
    JSlider sliderFieldSize;
    JLabel winSize;
    JLabel currentWinSize;
    JSlider sliderWinSize;

    SettingsWindow(GameWindow gameWindow) {
        buttonGroupMode = new ButtonGroup();
        lblGameModeChoice = new JLabel(" Выберите режим игры: ");
        jRadioButtonAi = new JRadioButton("Human vs Ai");
        jRadioButtonHuman = new JRadioButton("Human vs Human");
        buttonGroupMode.add(jRadioButtonAi);
        buttonGroupMode.add(jRadioButtonHuman);
        labelFieldSize = new JLabel(" Выбор размера поля.");
        currentSize = new JLabel(CURRENT_FIELD_SIZE + MIN_SIZE);
        winSize = new JLabel(" Выбор длины для победы. ");
        currentWinSize = new JLabel(CURRENT_WIN_VALUE + MIN_SIZE);
        btnStart = new JButton("Start new Game");
        sliderFieldSize = new JSlider(MIN_SIZE, 10, MIN_SIZE);
        sliderWinSize = new JSlider(MIN_SIZE, 10, MIN_SIZE);
        mainPanel = new JPanel(new GridLayout(9, 1));

        setLocationRelativeTo(gameWindow);
        setTitle("Game settings"); //заголовок окна
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                gameWindow.startNewGame(0, 3, 3, 3);
                setVisible(false);
            }
        });
        sliderWinSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentWinSize.setText(CURRENT_WIN_VALUE + sliderWinSize.getValue());
            }
        });

        sliderFieldSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                currentSize.setText(CURRENT_FIELD_SIZE + sliderFieldSize.getValue());
            }
        });

        mainPanel.add(lblGameModeChoice);
        mainPanel.add(jRadioButtonHuman);
        jRadioButtonHuman.setSelected(true);
        mainPanel.add(jRadioButtonAi);
        mainPanel.add(winSize);
        mainPanel.add(currentWinSize);
        mainPanel.add(sliderWinSize);
        mainPanel.add(labelFieldSize);
        mainPanel.add(currentSize);
        mainPanel.add(sliderFieldSize);
        add(mainPanel);
        add(btnStart, BorderLayout.SOUTH);
    }
}
