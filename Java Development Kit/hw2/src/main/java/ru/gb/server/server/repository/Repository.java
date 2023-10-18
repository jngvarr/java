package ru.gb.server.server.repository;

public interface Repository {
    void saveInLog(String text);
    String readLog();
    String getHistory();


}