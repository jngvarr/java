package ru.gb;

import java.net.Socket;

public class ClientManager {
while (!serverSocket.isClosed()){
        Socket socket = serverSocket.accept();
        System.out.println("Подключен новый клиент!");
        ClientManager client = new ClientManager(socket);
        Thread thread = new Thread(client);
        thread.start();

    public void closeSocket(){
        try{
            if (serverSocket != null) serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }
}
