package ru.gb.jdk.two.online.common;

import java.awt.*;

public interface Interactable {
    void update(MainCanvas canvas, float deltaTime);
    void render(MainCanvas canvas, Graphics g);
}
