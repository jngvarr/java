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
//        toysList = store.manualStoreFilling(toysList);                            // добавление игрушек в магазин в ручную
        store.writeToyDataToFile(toysList, path);
        List<String[]> toysStorelist = store.readDataFromFile(path);                // Список всех игрушек в магазине
        Map<String, Integer> toysByTitle = store.printToysByTitle(toysStorelist);   // Сколько каких игрушек в магазине
        store.setWeight(toysStorelist);
        System.out.print("Prize fund percentage: >");
        System.out.println("\n__________________________________________________________________________________________");
        short prizeFundPercent = 20; //new Scanner(System.in).nextShort();
        List<String[]> prizeFundList = store.prizeFundForming(prizeFundPercent, toysStorelist, toysByTitle);   // Список всех призовых игрушек
        Map<String, Integer> prizeToysByTitle = store.printToysByTitle(prizeFundList);                         // Количество призов по видам
        path = System.getProperty("user.dir") + "\\prizeFund.csv";
        store.writeDataToFile(prizeFundList, path);
        List<List<String[]>> lists = store.raffle(prizeFundList, toysStorelist);
        path = System.getProperty("user.dir") + "\\prizes.csv";
        store.writeDataToFile(lists.get(0), path);
        path = System.getProperty("user.dir") + "\\storeRemains.csv";
        store.writeDataToFile(lists.get(1), path);
    }
}
