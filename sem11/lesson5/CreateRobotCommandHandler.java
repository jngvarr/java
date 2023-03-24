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
            System.out.println("Создан новый робот:" + map.createRobot(new Point(Integer.parseInt
                    (args[0]), Integer.parseInt(args[1]))));
            System.out.println("-------------------------------------------------------------------------");
        } catch (PositionException e) {
            System.out.println("При создании робота возникло исключение: " + e.getMessage() + "." +
                    " Попробуйте еще раз");
        }
    }
}
