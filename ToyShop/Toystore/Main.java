package Toystore;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.File;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = System.getProperty("user.dir") + "\\toysList.csv";
        List<Toy> toysList;
        Store store = new Store();
        toysList = store.defaultStoreFilling(1000);
//        toysList = store.manualStoreFilling(toysList);
        store.writeToyDataToFile(toysList, path);

        List<String[]> toysStorelist = store.readDataFromFile(path);                 // Список всех игрушек в магазине
        Map<String, Integer> toysByTitle = store.printToysByTitle(toysStorelist);   // Сколько каких игрушек в магазине

        System.out.print("Prize fund percentage: >");
        System.out.println("\n__________________________________________________________________________________________");
        short prizeFundPercent = 20; //new Scanner(System.in).nextShort();
        List<String[]> prizeFundList = store.prizeFundForming(prizeFundPercent, toysStorelist, toysByTitle);     // Список всех призовых игрушек
        Map<String, Integer> prizeToysByTitle = store.printToysByTitle(prizeFundList);                         // Количество призов по видам
        path = System.getProperty("user.dir") + "\\prizeFund.csv";
        store.writeDataToFile(prizeFundList, path);
        store.raffle(prizeFundList);

    }
}
