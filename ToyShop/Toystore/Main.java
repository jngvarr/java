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
        OutputStream outStream = new FileOutputStream(path);
        List<Toy> toysList;
        Store store = new Store();
        toysList = store.defaultStoreFilling(100);
//        toysList = store.manualStoreFilling(toysList);

        String titles = "N;ID;Title;\n";
        outStream.write(titles.getBytes(StandardCharsets.UTF_8));
        for (Toy toy : toysList) {
            outStream.write((toy.ID.substring(toy.ID.indexOf("#") + 1, toy.ID.indexOf("-")) + ";" + toy.ID + ";"
                    + toy.title + ";\n").getBytes(StandardCharsets.UTF_8));
        }
        outStream.close();
        List<String[]> toysStorelist= store.readDataFromFile(path);                 // Список всех игрушек в магазине
        Map<String,Integer> toysByTitle = store.printToysByTitle (toysStorelist);   // Сколько каких игрушек в магазине

        System.out.print("Prize fund percentage: >");
        short prizeFundPercent = 10; //new Scanner(System.in).nextShort();
        List<String[]> prizeFundList= store.prizeFundForming(prizeFundPercent, toysStorelist, toysByTitle);
        Map<String,Integer> prizeToysByTitle = store.printToysByTitle (prizeFundList);
    }
}
