package Toystore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Model {
    public void printWeightFromList(List<String[]> list){
        Map<String, Integer> toysWeights = new HashMap<>();
        for (String[] s : list) {
            toysWeights.put(s[2], Integer.parseInt(s[3]));
        }
        for (Map.Entry<String, Integer> pair : toysWeights.entrySet()) {
            System.out.println(pair.getKey() + "'s weight is: " + pair.getValue() + ".");
        }
    }

    public Map<String, Integer> printToysByTitle(List<String[]> list) {
        Map<String, Integer> countToysByTitle = new HashMap<>();
        for (String[] s : list) {
            if (countToysByTitle.containsKey(s[2])) countToysByTitle.put(s[2], countToysByTitle.get(s[2]) + 1);
            else countToysByTitle.put(s[2], 1);
        }
        for (Map.Entry<String, Integer> pair : countToysByTitle.entrySet()) {
            if (!pair.getKey().equalsIgnoreCase("Title"))
                System.out.println(pair.getKey() + " : " + pair.getValue());
            else countToysByTitle.remove(pair);
        }
        System.out.println("-----------------------------------------------------------------------------");
        return countToysByTitle;
    }
    Comparator<String[]> stringArrComparator = new Comparator<String[]>() {
        @Override
        public int compare(String[] o1, String[] o2) {
            return Integer.compare(Integer.parseInt(o1[0]), Integer.parseInt(o2[0]));
        }
    };
    public List<String[]> readDataFromFile(String path) {
        List<String[]> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(path), StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                list.add(sc.nextLine().split(";"));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    public void writeToyDataToFile(List<Toy> toysList, String path) throws IOException {
        OutputStream outStream = new FileOutputStream(path);
        String titles = "N;ID;Title;Weight;\n";
        outStream.write(titles.getBytes(StandardCharsets.UTF_8));
        for (Toy toy : toysList) {
            outStream.write((toy.ID.substring(toy.ID.indexOf("#") + 1, toy.ID.indexOf("-")) + ";" + toy.ID + ";"
                    + toy.title + ";" + toy.getWeight() + ";" + ";\n").getBytes(StandardCharsets.UTF_8));
        }
        outStream.close();
    }
    public void writeDataToFile(List<String[]> toysList, String path) throws IOException {
        OutputStream outStream = new FileOutputStream(path);
        String titles = "N;ID;Title;Weight;\n";
        outStream.write(titles.getBytes(StandardCharsets.UTF_8));
        for (String[] toy : toysList) {
            outStream.write((toy[0] + ";" + toy[1] + ";" + toy[2] + ";" + toy[3] + ";\n").getBytes(StandardCharsets.UTF_8));
        }
        outStream.close();
    }

}
