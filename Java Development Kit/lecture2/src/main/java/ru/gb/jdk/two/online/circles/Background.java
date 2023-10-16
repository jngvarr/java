package ru.gb.jdk.two.online.circles;

import ru.gb.jdk.two.online.common.Interactable;
import ru.gb.jdk.two.online.common.MainCanvas;

import java.awt.*;

public class Background implements Interactable {
    private float time;
    private static final float AMPLITUDE = 255f / 2f;
    private Color color;

    @Override
    public void update(MainCanvas canvas, float deltaTime) {
        time += deltaTime;
        int red = Math.round(AMPLITUDE + AMPLITUDE * (float) Math.sin(time * 2f));
        int green = Math.round(AMPLITUDE + AMPLITUDE * (float) Math.sin(time * 3f));
        int blue = Math.round(AMPLITUDE + AMPLITUDE * (float) Math.sin(time));
        color = new Color(red, green, blue);
    }

    @Override
    public void render(MainCanvas canvas, Graphics g) {
        canvas.setBackground(color);
    }
}
