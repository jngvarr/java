package ru.gb.client;

import ru.gb.server.Server;

import javax.swing.*;

public class Client extends JFrame {
    ClientView clientView;
    private boolean isLogged;
    Server server;
    private String name;

    public Client(ClientView clientView, Server serverWindow) {
        this.clientView = clientView;
        this.server = serverWindow;
    }


    public boolean connectToServer(String name) {
        this.name = name;
        if (server.connectUser(this)) {
            printText("Вы успешно подключились!");
            isLogged = true;
            String log = server.getLog();
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

    public void disconnect(Client client) {
        if (isLogged) {
            isLogged = false;
//            clientView.disconnectFromServer();
            server.disconnectUser(this);
            printText("Вы были отключены от сервера!");
        }
    }

    boolean isConnected() {
        return isLogged;
    }

    public String getName() {
        return name;
    }
    public void disconnectFromServer(){
        clientView.disconnectFromServer();
    }
    private void printText(String text) {
        clientView.showMessage(text);
    }
}