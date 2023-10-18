package ru.gb.server.client;

import ru.gb.server.server.Server;
import ru.gb.server.server.ServerGUI;

public class Client {

    boolean isLogged;
    ClientView clientView;
    private String name;
    private Server server;

    public Client(ClientView clientView, Server server) {
        this.clientView = clientView;
        this.server = server;
    }

    public boolean connectToServer(String name) {
        this.name = name;
        if (server.connectUser(this)) {
            printText("Вы успешно подключились!\n");
            isLogged = true;
            String log = server.readLog();
            if (log != null) {
                printText(log);
            }
            return true;
        } else {
            printText("Подключение не удалось");
            return false;
        }
    }

    //мы посылаем
    public void sendMessage(String message) {
        if (isLogged) {
            if (!message.isEmpty()) {
                server.sendMessage(name + ": " + message);
            }
        } else {
            printText("Нет подключения к серверу");
        }
    }

    //нам посылают
    public void serverAnswer(String answer) {
        printText(answer);
    }

    public void disconnect() {
        if (isLogged) {
            isLogged = false;
            clientView.disconnectFromServer();
            server.disconnectUser(this);
            printText("Вы были отключены от сервера!");
        }
    }

    public String getName() {
        return name;
    }

    private void printText(String text) {
        clientView.showMessage(text);
    }
}

