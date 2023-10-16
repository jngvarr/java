package ru.gb.jdk.two.online.common;

import java.awt.*;

public interface CanvasRepaintListener {
    void onDrawFrame(MainCanvas canvas, Graphics g, float deltaTime);
}
