package Toystore;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.io.File;

public class Main {
    public static void main(String[] args) throws IOException {
        String path = "i:\\gb\\ToyShop\\Toystore\\toysList.csv";
        OutputStream outStream = new FileOutputStream(path);
        List<Toy> toysList;
        Store store = new Store();
        toysList  = store.defaultStoreFilling(0);
        toysList = store.manualStoreFilling(toysList);
        String titles = "N;ID;Title;\n";
        outStream.write(titles.getBytes(StandardCharsets.UTF_8));
        for (Toy toy : toysList) {
//            String data = "";
//            data = toy.ID.substring(toy.ID.indexOf("#") + 1, toy.ID.indexOf("-")) + ";" + toy.ID + ";" + toy.title + ";\n";
            outStream.write((toy.ID.substring(toy.ID.indexOf("#") + 1, toy.ID.indexOf("-")) + ";" + toy.ID + ";"
                    + toy.title + ";\n").getBytes(StandardCharsets.UTF_8));
        }
        outStream.close();
        Map<String, Integer> countToysByTitle = new HashMap<>();
        List<String[]> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(path), StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                list.add(sc.nextLine().split(";"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String[] s : list) {
//            System.out.println(Arrays.asList(s));
//            System.out.println(s[0] + " " + s[1] + " " + s[2]);
            if (countToysByTitle.containsKey(s[2])) countToysByTitle.put(s[2], countToysByTitle.get(s[2])+1);
            else countToysByTitle.put(s[2], 1);
        }
        for (Map.Entry <String,Integer> pair: countToysByTitle.entrySet()) {
            System.out.println(pair.getKey()+" : "+ pair.getValue());
        }
//        System.out.println(Arrays.asList(list.get(0)));
//        System.out.println(list.get(0)[0]);
    }
}