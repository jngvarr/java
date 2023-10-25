package ru.gb;

import java.nio.channels.GatheringByteChannel;

public class Main {


    private static class Box {
        private Object obj;

        public Box(Object obj) {
            this.obj = obj;
        }

        public Object getObj() {
            return obj;
        }

        public void setObj(Object obj) {
            this.obj = obj;
        }

        public void printInfo() {
            System.out.printf("Box (%s): %s", obj.getClass().getSimpleName(), obj.toString());
        }
    }

    public static void main(String[] args) {
        Box b1 = new Box(20);
        Box b2 = new Box(30);
//        System.out.println(b1.getObj() + b2.getObj());
        System.out.println((Integer) b1.getObj() + (Integer) b2.getObj());

        Box b3 = new Box("Hello, ");
        Box b4 = new Box("World");

        System.out.println((String) b3.getObj() + (String) b4.getObj());

        Box ibox1 = new Box(20);
        Box ibox2 = new Box(30);
        if (ibox1.getObj() instanceof Integer && ibox2.getObj() instanceof Integer) {
            int sum = ((Integer) b1.getObj() + (Integer) b2.getObj());
            System.out.println("sum = " + sum);
        } else System.out.println("The content of the boxes differ by type");
        ibox1.setObj("sdf"); // "What can go wrong here? You can do it!"

    }
}