package ru.gb.server;

import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Logger {


    String filename = "./log.txt";

    protected void writeLogToFile(String data) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(filename, true))) {
            bufferedWriter.write(data+"\n");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    protected StringBuffer readLogTFromFile() {
        StringBuffer stringBuffer = new StringBuffer();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(filename))) {

            String line = bufferedReader.readLine();
            if (line == null || line.isBlank()) {
                System.out.println("Log is empty.");
                return stringBuffer.append("Log is empty.\n");
            }

            while (line != null) {
                stringBuffer.append(line).append("\n");
                line = bufferedReader.readLine();
            }

            return stringBuffer;

        } catch (IOException ioe) {
            System.out.println("Log file is not found: " + filename);
        }
        return stringBuffer.append("Log file is not found: " + filename + "\n");
    }
}


