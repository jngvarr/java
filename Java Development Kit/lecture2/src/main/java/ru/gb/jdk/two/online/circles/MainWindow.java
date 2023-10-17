package ru.gb.jdk.two.online.circles;

import ru.gb.jdk.two.online.circles.exceptions.TooMuchBallException;
import ru.gb.jdk.two.online.common.CanvasRepaintListener;
import ru.gb.jdk.two.online.common.Interactable;
import ru.gb.jdk.two.online.common.MainCanvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class MainWindow extends JFrame implements CanvasRepaintListener, Thread.UncaughtExceptionHandler{
    private static final int POS_X = 400;
    private static final int POS_Y = 200;
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = 600;
    public static final String TITLE = "Circles";
    public static final int SPRITES_DEFAULT_QUANTITY = 10;
    public static final int SPRITES_MAX_QUANTITY = 15;
    private Interactable[] sprites;
    private int spritesQuantity;
    Random rnd = new Random();

    private MainWindow() {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setBounds(POS_X, POS_Y, WINDOW_WIDTH, WINDOW_HEIGHT);
        setTitle(TITLE);

        initSprites();
        MainCanvas canvas = new MainCanvas(this);
        addMouseListener(new MouseListener(this));
//        canvas.addMouseListener(this);
//            @Override
//            public void mouseReleased(MouseEvent e) {
//                super.mouseReleased();
//            }
//        });

        add(canvas);
        setVisible(true);
    }

    private void initSprites() {
        sprites = new Interactable[SPRITES_MAX_QUANTITY];
        sprites[0] = new Background();
        spritesQuantity++;
        for (int i = 1; i < SPRITES_DEFAULT_QUANTITY; i++) {
            addSprite(rnd.nextInt(WINDOW_WIDTH), rnd.nextInt(WINDOW_HEIGHT));
        }
    }

    public void addSprite(int x, int y) {
        if (spritesQuantity >= SPRITES_MAX_QUANTITY) throw new TooMuchBallException();
        sprites[spritesQuantity++] = new Ball(x, y);
    }

    public void removeSprite() {
        if (spritesQuantity <= 1) return;
        spritesQuantity--;
    }

    public void onDrawFrame(MainCanvas canvas, Graphics g, float deltaTime) {
        update(canvas, deltaTime);
        render(canvas, g);
    }

    private void update(MainCanvas canvas, float deltaTime) {
        for (int i = 0; i < spritesQuantity; i++) {
            sprites[i].update(canvas, deltaTime);
        }
    }

    private void render(MainCanvas canvas, Graphics g) {
        for (int i = 0; i < spritesQuantity; i++) {
            sprites[i].render(canvas, g);
        }
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        if (e instanceof TooMuchBallException) {
            e.fillInStackTrace();
        }
    }

    public static void main(String[] args) {
        new MainWindow();
    }

}
