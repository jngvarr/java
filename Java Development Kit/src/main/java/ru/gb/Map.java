package ru.gb;

import javax.swing.*;
import java.awt.*;

public class Map extends JPanel {
    private int panelWidth;
    private int panelHeight;
    private int cellWidth;
    private int cellHeight;

    Map() {
        //setBackground(Color.BLACK); // установка фонового цвета окна
    }

    void startNewGame(int mode, int fSzX, int fSzY, int wLen) {
        System.out.printf("Mode: %d;\nSize: x=%d, y=%d\nWin Length: %d", mode, fSzX, fSzY, wLen);
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        render(g);
    }

    private void render(Graphics g) {
        g.drawLine(0, 0, 100, 100);
        panelHeight = getHeight();
        panelWidth = getWidth();
        cellHeight = panelHeight / 3;
        cellWidth = panelWidth / 3;
        g.setColor(Color.BLACK);

        for (int h = 0; h < 3; h++) {
            int y = h * cellHeight;
            g.drawLine(0, y, panelWidth, y);
        }

        for (int w = 0; w < 3; w++) {
            int x = w * cellWidth;
            g.drawLine(0, x, x, panelWidth);
        }
    }
}
