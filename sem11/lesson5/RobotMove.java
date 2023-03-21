package ru.gb.lesson5;

import ru.gb.lesson5.RobotMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class RobotMove extends JFrame implements KeyListener {
    @Override
    public void keyTyped(KeyEvent e) {

    }

    public RobotMove() {
        addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        Point newPosition = switch (event.getKeyCode()) {
            case KeyEvent.VK_UP -> new Point(RobotMap.position.getX() - 1, RobotMap.position.getY());
            case KeyEvent.VK_RIGHT -> new Point(RobotMap.position.getX(), RobotMap.position.getY() + 1);
            case KeyEvent.VK_DOWN -> new Point(RobotMap.position.getX() + 1, RobotMap.position.getY());
            case KeyEvent.VK_LEFT -> new Point(RobotMap.position.getX(), RobotMap.position.getY() - 1);
            default -> throw new IllegalStateException("Unexpected value: " + event.getKeyCode());
        };
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}
