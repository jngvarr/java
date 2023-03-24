package ru.gb.lesson5;

import java.util.Arrays;

public class CreateRobotCommandHandler implements CommandHandler {

    @Override
    public String commandName() {
        return "create-robot";
    }

    @Override
    public void handleCommand(RobotMap map, String[] args) {
        try {
            try {
                System.out.println("Создан новый робот:" + map.createRobot(new Point(Integer.parseInt
                        (args[0]), Integer.parseInt(args[1]))));
            } catch (PositionException e) {
                System.out.println("При создании робота возникло исключение: " + e.getMessage() + "." +
                        " Попробуйте еще раз");
            }
        } catch (NumberFormatException e) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Ошибка в формате ввода. " + "\u001B[31m" + e.getMessage() + "\u001B[0m" +
                    ". Попробуйте еще раз!");
        }
    }
}

