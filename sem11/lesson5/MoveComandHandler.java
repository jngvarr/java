package ru.gb.lesson5;

import java.util.Arrays;

public class MoveComandHandler implements CommandHandler {
    @Override
    public String commandName() {
        return "move-robot";
    }

    @Override
    public void handleCommand(RobotMap map, String[] args) {
        try {
            RobotMap.Robot robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(args[0]));
            if (robot != null) {
                try {
                    robot.move();
                    System.out.printf("Текущее расположение роботов%s: \n", map.getRobots());
                } catch (PositionException e) {
                    System.out.println("При попытке передвинуть робота возникло исключение: " + e.getMessage()
                            + ". Попробуйте еще раз");
                }
            } else System.out.println("\u001B[31m"+"Для начала создайте робота!"+ "\u001B[0m");
        } catch (NumberFormatException e) {
            System.out.println("-------------------------------------------------------------------------------------");
            System.out.println("Ошибка в формате ввода. "+"\u001B[31m" + e.getMessage() +"\u001B[0m"+ ". Попробуйте еще раз!");

        }
    }
}
//System.out.println(color + "[" + health + "]" + "\u001B[0m");
//        }
//        }


//    public static final String ANSI_RESET = "\u001B[0m";
//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_BLACK = "\u001B[30m";
//      public static final String ANSI_YELLOW = "\u001B[33m";