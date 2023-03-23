package ru.gb.lesson5;

import java.awt.*;
import java.lang.reflect.InvocationHandler;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    // Client <-> API <-> Model
    // Client_2

    public static void main(String[] args) {
        // 1.
        // Карта с каким-то размером nxm.
        // На ней можно создать робов, указывая начальное положение.
        // Если начальное положение некорректное ИЛИ эта позиция занята другим робом, то кидаем исключение.
        // Робот имеет направление (вверх, вправо, вниз, влево). У роботов можно менять направление и передвигать их на 1 шаг вперед
        // 2.
        // Написать контроллер к этому коду, который будет выступать посредником между консолью (пользователем) и этой игрой.
        // (0,0)      ------------------            (0, m)
        // ...
        // (n, 0)    -----------------------        (n, m)

        // Robot, Map, Point

        // Домашнее задание:
        // Реализовать чтение команд с консоли и выполнить их в main методе
        // Список команд:
        // create-map 3 5 -- РЕАЛИЗОВАНО!
        // create-robot 2 7
        // move-robot id
        // change-direction id LEFT
        boolean game = true;
        Scanner sc = new Scanner(System.in);
        System.out.println("Введите команду \"create-map 5 5\" для создания карты:");
        RobotMap map = null;
        RobotMap.Robot robot = null;
        while (true) {
            String command = sc.nextLine();
            if (command.startsWith("create-map")) {
                String[] split = command.split(" "); // [create-map 3 5]
                String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [3 5]
                try {
                    map = new RobotMap(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("При создании карты возникло исключение: " + e.getMessage() + "." +
                            " Попробуйте еще раз");
                }
            } else {
                System.out.println("Команда не найдена. Попробуйте еще раз");
            }
        }

        System.out.println("ИГРАЕМ...");

        while (game == true) {
            System.out.println("Введите команду по образцу: \n" +
                    "1. Для создания нового робота  - \"create-robot 3 3\".\n" +
                    "2. Сменить направление движения робота - \"change-direction id LEFT\".\n" +
                    "3. Походить роботом - \"move-robot id\"\n" +
                    "4. Закончить игру - \"stop\"");
            String command = sc.nextLine();
            String[] split = command.split(" ");
            switch (split[0]) {
                case "create-robot" -> {
                    String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [2 7]
                    try {
                        System.out.println("Создан новый робот:" + map.createRobot(new Point(Integer.parseInt
                                (arguments[0]), Integer.parseInt(arguments[1]))));
                        System.out.println("-------------------------------------------------------------------------");
                        break;
                    } catch (PositionException e) {
                        System.out.println("При создании робота возникло исключение: " + e.getMessage() + "." +
                                " Попробуйте еще раз");
                    }
                }
                case "change-direction" -> {
                        String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [id direction]
                        robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(arguments[0]));
                        //System.out.println(robot.getDirection());
                    if (robot != null) {
                        System.out.println("Робот до смены направления: " + robot);
                        System.out.println("Робот сменил направление: " + robot.changeDirection(arguments[1]));
                        System.out.println("-------------------------------------------------------------------------");
                    } else System.out.println("Для начала создайте робота!");
                    System.out.println("---------------------------------------------------------------------");
                }
                case "move-robot" -> {
                    String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [2]
                    robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(arguments[0]));
                    if (robot != null) {
                        try {
                            robot.move();
                            System.out.printf("Текущее расположение роботов%s: \n", map.getRobots());
                            System.out.println("---------------------------------------------------------------------");
                        } catch (PositionException e) {
                            System.out.println("При попытке передвинуть робота возникло исключение: " + e.getMessage()
                                    + ". Попробуйте еще раз");
                        }
                    } else System.out.println("Для начала создайте робота!");
                    System.out.println("---------------------------------------------------------------------");
                }
                case "stop" -> {    //split[0].equalsIgnoreCase("stop");
                    game = false;
                }
            }
        }
    }

//        RobotMap.Robot robot1 = null;
//        RobotMap.Robot robot2 = null;
//        try {
//            robot1 = map.createRobot(new Point(2, 5));
//            robot2 = map.createRobot(new Point(4, 5));
//
//            System.out.println(robot1);
//            System.out.println(robot2);
//        } catch (PositionException e) {
//            System.out.println("Во время создания робота случилось исключение: " + e.getMessage());
//        }
//
//        if (robot2 != null) {
//            try {
//                robot2.move();
//            } catch (PositionException e) {
//                System.out.println("Не удалось переместить робота: " + e.getMessage());
//            }
//        }

// create robot (3, 5)


}



