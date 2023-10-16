package ru.gb.jdk.two.online.bricks;

import ru.gb.jdk.two.online.circles.Background;
import ru.gb.jdk.two.online.common.CanvasRepaintListener;
import ru.gb.jdk.two.online.common.Interactable;
import ru.gb.jdk.two.online.common.MainCanvas;

import javax.swing.*;
import java.awt.*;

public class MainWindow extends JFrame implements CanvasRepaintListener {
    private static final int POS_X = 400;
    private static final int POS_Y = 200;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    private  final Interactable[] sprites = new Interactable[10];

    private MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle("Bricks");
        sprites[0] =  new Background();
        for (int i = 1; i < sprites.length; i++) {
            sprites[i] = new Brics();
        }

        MainCanvas canvas = new MainCanvas(this);
        add(canvas);
        setVisible(true);
    }

    public void onDrawFrame(MainCanvas canvas, Graphics g, float deltaTime) {
        update(canvas, deltaTime);
        render(canvas, g);
    }

    private void update(MainCanvas canvas, float deltaTime){
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].update(canvas, deltaTime);
        }
    }
    private void render(MainCanvas canvas, Graphics g){
        for (int i = 0; i < sprites.length; i++) {
            sprites[i].render(canvas, g);
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }

}
