package ru.gb.jdk.two.online.common;

import ru.gb.jdk.two.online.circles.MainWindow;

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class MainCanvas extends JPanel {
    private final CanvasRepaintListener controller;
    // private final MainWindow controller;
    private long lastFrameTime;


    //    MainCanvas(MainWindow controller) {
    public MainCanvas(CanvasRepaintListener controller) {
        //setBackground(Color.BLUE);
        this.controller = controller;
        lastFrameTime = System.nanoTime();
        setBackground(new Color(new Random().nextInt()));
    }

    @Override
    protected void paintComponent(Graphics g) { //метод перерисовки компонента
        super.paintComponent(g);
        //controller.onDrawFrame();// заставляем фрем перерисовыться

        try {
            Thread.sleep(16);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        float deltaTime = (System.nanoTime() - lastFrameTime) * 0.000000001f;
        controller.onDrawFrame(this, g, deltaTime);
        lastFrameTime = System.nanoTime();
        repaint();
    }

    public int getLeft() {
        return 0;
    }

    public int getRight() {
        return getWidth() - 1;
    }

    public int getTop() {
        return 0;
    }

    public int getBottom() {
        return getHeight() - 1;
    }
}
