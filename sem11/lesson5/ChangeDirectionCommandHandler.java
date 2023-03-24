package ru.gb.lesson5;

import java.awt.*;
import java.util.Arrays;
import java.util.UUID;

public class ChangeDirectionCommandHandler implements CommandHandler {

    @Override
    public String commandName() {
        return "change-direction";
    }

    @Override
    public void handleCommand(RobotMap map, String[] args) {
        try {
            RobotMap.Robot robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(args[0]));

            if (robot != null) {
                System.out.println("Робот до смены направления: " + robot);
                robot.changeDirection(RobotMap.Robot.Direction.valueOf(args[1]));
                System.out.printf("Робот сменил направление: %s.\n", robot.getDirection());
            } else System.out.println("\u001B[31m"+"Для начала создайте робота!"+ "\u001B[0m");
        } catch (NumberFormatException e) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Ошибка в формате ввода. " + "\u001B[31m" + e.getMessage() + "\u001B[0m" +
                    ". Попробуйте еще раз!");
        }
    }
}



