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
        RobotMap.Robot robot = (RobotMap.Robot) map.getRobots().get(Long.parseLong(args[0]));
        if (robot != null) {
            System.out.println("Робот до смены направления: " + robot);
            robot.changeDirection(RobotMap.Robot.Direction.valueOf(args[1]));
            System.out.println("Робот сменил направление: " + robot.getDirection());
            System.out.println("-------------------------------------------------------------------------");
        } else System.out.println("Для начала создайте робота!\n------------------------------------------");
    }
}



