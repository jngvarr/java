package ru.gb.jdk.two.online.bricks;

import ru.gb.jdk.two.online.common.MainCanvas;
import ru.gb.jdk.two.online.common.Sprite;

import java.awt.*;
import java.util.Random;

public class Bricks extends Sprite {
    private static Random rnd = new Random();
    private final Color color;
    private float vX; // скорость по оси Х
    private float vY; // скорость по оси У

    Bricks(float x, float y) {
        super(x,y);
        halfHeight = 20 + rnd.nextFloat(50);
        halfWidth = halfHeight;
        color = new Color(rnd.nextInt());
        vX = 100f + rnd.nextFloat(200);
        vY = 100f + rnd.nextFloat(200);
    }

    @Override
    public void render(MainCanvas canvas, Graphics g) {
        g.fillRect((int) getLeft(), (int) getTop(),
                (int) getWidth(), (int) getHeight());
        g.setColor(color);
    }

    @Override
    public void update(MainCanvas canvas, float deltaTime) {
        x += vX * deltaTime;
        y += vY * deltaTime;

        if (getLeft() < canvas.getLeft()) {
            setLeft((canvas.getLeft()));
            vX = -vX;
        }
        if (getRight() > canvas.getRight()) {
            setRight((canvas.getRight()));
            vX = -vX;
        }
        if (getTop() < canvas.getTop()) {
            setTop((canvas.getTop()));
            vY = -vY;
        }
        if (getBottom() > canvas.getBottom()) {
            setBottom((canvas.getBottom()));
            vY = -vY;
        }
    }
}
