package ru.gb.lesson5;

public interface CommandHandler {

    String commandName();

    void handleCommand(RobotMap map, String[] args);

}
