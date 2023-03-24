package ru.gb.lesson5;

import java.util.Arrays;

public class MoveComandHandler implements CommandHandler {
    @Override
    public String commandName() {
        return "move";
    }

    @Override
    public void handleCommand(RobotMap map, String[] args) {
        RobotMap.Robot robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(args[0]));
        if (robot != null) {
            try {
                robot.move();
                System.out.printf("Текущее расположение роботов%s: \n", map.getRobots());
                System.out.println("---------------------------------------------------------------------");
            } catch (PositionException e) {
                System.out.println("При попытке передвинуть робота возникло исключение: " + e.getMessage()
                        + ". Попробуйте еще раз");
            }
        } else System.out.println("Для начала создайте робота!\n-----------------------------------------" +
                "-------------------------------------------------------");
    }
}
