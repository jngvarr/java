import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main extends JFrame implements KeyListener {


    @Override
    public void keyTyped(KeyEvent e) {

    }

    public static void main(String[] args) {
        Main m = new Main();
    }

    public Main() {
        setSize(500, 500);
        setVisible(true);
        addKeyListener(this);
    }

    @Override
    public void keyPressed(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.VK_UP -> System.out.println("up");
            case KeyEvent.VK_DOWN -> System.out.println("down");
            case KeyEvent.VK_RIGHT -> System.out.println("right");
            case KeyEvent.VK_LEFT -> System.out.println("left");
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
//            case KeyEvent.VK_UP -> new Point(RobotMap.position.getX() - 1, RobotMap.position.getY());
//            case KeyEvent.VK_RIGHT -> new Point(RobotMap.position.getX(), RobotMap.position.getY() + 1);
//            case KeyEvent.VK_DOWN -> new Point(RobotMap.position.getX() + 1, RobotMap.position.getY());
//            case KeyEvent.VK_LEFT -> new Point(RobotMap.position.getX(), RobotMap.position.getY() - 1);
//            default -> throw new IllegalStateException("Unexpected value: " + event.getKeyCode());
}

;
//        RobotMap.position = newPosition;
//        System.out.println(RobotMap.position);

