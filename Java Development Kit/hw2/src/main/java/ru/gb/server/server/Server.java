package ru.gb.server.server;

import ru.gb.server.client.Client;

public class Server {
    private boolean isServerWorking;
    private Logger logger = new Logger();
    Client client;

    public boolean connectUser(Client client){
        if (!isServerWorking){
            return false;
        }
        clientGUIList.add(client);
        return true;
    }

    public String getHistory() {
        return readLog();
    }

    public void disconnectUser(Client clientGUI){
        clientGUIList.remove(clientGUI);
        if (clientGUI != null){
            clientGUI.disconnectFromServer();
        }
    }
    public String readLog() {
        return String.valueOf(logger.readLogTFromFile());
    }
    public void sendMessage(String message) {
        if (!isServerWorking) return;
        jTextArea.append(message);
        logger.writeLogToFile(message);
    }
}
