package ru.gb.server;

import ru.gb.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

public class Server extends JFrame {
    private boolean isServerWorking;
    Logger logger = new Logger();
    ServerGUI serverGUI;
    List<Client> clients;
    private ServerView serverView;

    public Server(ServerView serverView) {
        this.serverView = serverView;
        clients = new ArrayList<>();
    }

    public boolean connectUser(Client clientGUI) {
        if (!isServerWorking) {
            return false;
        }
        clients.add(clientGUI);
        return true;
    }


    public String getLog() {
        return readLog();
    }



    public void sendMessage(String message) {
        if (!isServerWorking) return;
        answerAll(message);
        serverView.showMessage(message);
        logger.writeLogToFile(message);
    }

    private void answerAll(String answer) {
        for (Client client : clients) {
            client.serverAnswer(answer);
        }
    }
    public List<Client> getClientsList() {
        return clients;
    }
    public boolean isWorking() {
        return isServerWorking;
    }

    public void switchServer(boolean working) {
        isServerWorking = !working;
    }

    public void disconnectUser(Client client) {
        if (client != null) {
            clients.remove(client);
        }
    }

    public void serverDown(Client client) {
        if (client != null) {
            client.disconnectFromServer();
        }
    }

    public String readLog() {
        return String.valueOf(logger.readLogTFromFile());
    }
}
