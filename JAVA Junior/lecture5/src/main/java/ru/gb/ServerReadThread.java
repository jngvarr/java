package ru.gb;

import java.io.*;
import java.net.Socket;

public class ServerReadThread extends Thread {
    Socket socket;

    public ServerReadThread(Socket socket) {
        this.socket = socket;
        this.run();
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String inLine;
            OutputStream outStream = socket.getOutputStream();
            PrintStream printStream = new PrintStream(outStream);
            while ((inLine = reader.readLine()) != null) {
                printStream.println("(" + socket.getPort() + ") " + inLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
