package ru.gb.lesson5;

public class StopCommandHandler implements CommandHandler{
    @Override
    public String commandName() {
        return "Stop ";
    }

//    public boolean handleCommand() {
//        return false;
//    }

    @Override
    public void handleCommand(RobotMap map, String[] args) {
    }
}
