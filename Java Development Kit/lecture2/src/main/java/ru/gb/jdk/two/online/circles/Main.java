package ru.gb.jdk.two.online.circles;

public class Main {
    public interface MouseListener {
        void mouseUp();

        void mouseDown();
    }

    private static class MouseAdapter implements MouseListener {
        @Override
        public void mouseUp() {
        }

        @Override
        public void mouseDown() {
        }
    }

    private static void addMouseListener(MouseListener l) {
        l.mouseDown();
        l.mouseUp();
    }

    public static void main(String[] args) {
        MouseAdapter m = new MouseAdapter();
        addMouseListener(m);
        addMouseListener(new MouseAdapter());
        MouseListener l = new MouseListener() {
            @Override
            public void mouseUp() {
            }

            @Override
            public void mouseDown() {
            }
        };
        addMouseListener(l);
        addMouseListener(new MouseListener() {
            @Override
            public void mouseUp() {
            }

            @Override
            public void mouseDown() {
            }
        });
        m.mouseDown();
        m.mouseUp();
    }
}
