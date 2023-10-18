package ru.gb.server.server.repository;

import java.io.FileReader;
import java.io.FileWriter;

public class Storage implements Repository {

    private String log_path;

    public Storage(String log_path){
        this.log_path = log_path;
    }
    @Override
    public void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(log_path, true)) {
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String readLog() {
        StringBuilder stringBuilder = new StringBuilder();
        try (FileReader reader = new FileReader(log_path);) {
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append((char) c);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String getHistory() {
        return readLog();
    }
}

