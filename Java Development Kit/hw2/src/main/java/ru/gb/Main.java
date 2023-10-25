package ru.gb;

import ru.gb.client.Client;
import ru.gb.client.ClientGUI;
import ru.gb.server.Server;
import ru.gb.server.ServerGUI;

public class Main {
    public static void main(String[] args) {
        ServerGUI serverWindow = new ServerGUI();
        Server server = serverWindow.getServer();
        new ClientGUI(server);
        new ClientGUI(server);
    }
}