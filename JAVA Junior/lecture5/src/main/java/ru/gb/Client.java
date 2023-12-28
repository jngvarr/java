package ru.gb;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        try {
            InetAddress address = InetAddress.getLocalHost();
            Socket client = new Socket(address, 1300);
            System.out.println(client.getInetAddress());
            System.out.println(client.getLocalPort());
            InputStream inStream = client.getInputStream();
            OutputStream outStream = client.getOutputStream();
            DataInputStream dataInputStream = new DataInputStream(inStream);
            PrintStream printStream = new PrintStream(outStream);
            printStream.println("Привет!");
            System.out.println(dataInputStream.readLine());
            String inLine;
            Scanner scanner = new Scanner(System.in);
            while (!(inLine = scanner.nextLine()).equals("exit")){
                printStream.println(inLine);
                System.out.println(dataInputStream.readLine());
            }
            scanner.close();
            client.close();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
