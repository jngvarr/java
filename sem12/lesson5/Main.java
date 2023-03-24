package ru.gb.lesson5;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    // Client <-> API <-> Model
    // Client_2

    static Scanner sc = new Scanner(System.in);

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
        // help


        System.out.println("Введите команду для создания карты:");
        RobotMap map = null;
        while (true) {
            String command = sc.nextLine();
            if (command.startsWith("create-map")) {
                String[] split = command.split(" "); // [create-map 3 5]
                String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [3 5]

                try {
                    map = new RobotMap(Integer.parseInt(arguments[0]), Integer.parseInt(arguments[1]));
                    System.out.println("Карта создана!");
                    break;
                } catch (IllegalArgumentException e) {
                    System.out.println("При создании карты возникло исключение: " + e.getMessage() + "." +
                            " Попробуйте еще раз");
                }
            } else {
                System.out.println("Команда не найдена. Попробуйте еще раз");
            }
        }

        List<CommandHandler> handlers = List.of(
                new ChangeDirectionCommandHandler()
                // TODO: 20.03.2023 Вписать свои реализации обработчиков
        );
        CommandManager commandManager = new CommandManager(map, handlers);

        System.out.println("ИГРАЕМ...");
        System.out.println("Список допустимых команд: ...");
        while (true) {
            String command = sc.nextLine();
//            try {
                commandManager.handleCommand(command);
//            } catch (CommandExecutionException e) {
//                System.out.println("Не удалось обработать команду: " + eю);
//            }

//            if (command.startsWith("create-robot")) {
//                System.out.println("Создаем робота!!!");
//                // TODO: 20.03.2023
//            } else if (command.startsWith("move-robot")) {
//                System.out.println("Двигаем робота");
//                // TODO: 20.03.2023
//            } else if (command.startsWith("change-direction")) {
//                String[] split = command.split(" "); // [change-direction id LEFT]
//                String[] arguments = Arrays.copyOfRange(split, 1, split.length); // [id LEFT]
//
//                UUID robotId = UUID.fromString(arguments[0]);
//                RobotMap.Direction direction = RobotMap.Direction.valueOf(arguments[1]);
//
//                changeDirection(map, robotId, direction);
//            } else if (command.startsWith("q")) {
//              break;
//            } else {
//                System.out.println("Команда не найдена. Попробуйте еще раз");
//            }
        }
    }

//    private static void changeDirection(RobotMap map, UUID robotId, RobotMap.Direction direction) {
//        RobotMap.Robot robotById = map.findRobotById(robotId);
//        if (robotById != null) {
//            robotById.changeDirection(direction);
//        } else {
//            System.out.println("Робот с идентификаторо " + robotId + " не найден");
//        }
//    }

}
