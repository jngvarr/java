package Controller;

import Model.Animals;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class RegistryController {
    String path = System.getProperty("user.dir") + "\\src\\Sources";
    String fileName = "\\db.csv";
    boolean bdIsEmpty = false;

    public void updateDB(String[] data, int noteNumber) throws IOException {
        List<String[]> list = readDataFromFile(path + fileName);
        list.get(noteNumber)[2] = data[0];
        list.get(noteNumber)[3] = data[1];
        list.get(noteNumber)[4] = data[2];
        writeDataToFile(list, path);
    }

    public void deleteAnimalData(String[] data, int noteNumber) throws IOException {
        List<String[]> list = readDataFromFile(path + fileName);
        list.get(noteNumber)[1] = data[0];
        list.get(noteNumber)[2] = data[1];
        list.get(noteNumber)[3] = data[2];
        list.get(noteNumber)[4] = data[3];
        writeDataToFile(list, path);
    }

    public void commandsList(int commandsNumber) {
        List<String[]> list = readDataFromFile(path + fileName);
        String[] animalData = list.get(commandsNumber);
        String[] commands = animalData[4].trim().split(",");
        System.out.println(animalData[2] + "(ID = " + animalData[1] + ") умеет выполнять команды: ");
        for (String command : commands) {
            System.out.print(command.trim() + " ");
        }
        System.out.println();
    }

    public void training(int commandsNumber, String newCommand) throws IOException {
        List<String[]> list = readDataFromFile(path + fileName);
        list.get(commandsNumber)[4] = list.get(commandsNumber)[4] + ", " + newCommand;
        writeDataToFile(list, path);
    }
    public void setID() {
        int lastNum = 0;
        List<String[]> list = readDataFromFile(path + fileName);
        if (!list.isEmpty()) lastNum = Integer.parseInt(list.get(list.size() - 1)[0]);
        Animals.setNumber(lastNum);
    }

    public void add(Animals animal) throws IOException {
        OutputStream outStream = new FileOutputStream(path + fileName, true);
        List<String[]> list = readDataFromFile(path + fileName);
        if (isBdIsEmpty(list)) outStream.write("N;ID;Name;Day_of_birth;Commands\n".getBytes(StandardCharsets.UTF_8));
        outStream.write(
                (animal.getID().substring(animal.getID().indexOf("#")+1, animal.getID().indexOf("-")) + ";"
                        + animal.getID() + ";"
                        + animal.getName() + ";"
                        + animal.getDayOfBirth() + ";"
                        + animal.getCommands() + "\n")
                        .getBytes(StandardCharsets.UTF_8));
        outStream.close();
    }

    public boolean isBdIsEmpty(List<String[]> list) {
        if (list.isEmpty()) return true;
        else return false;
    }

    public List<String[]> readDataFromFile(String path) {
        List<String[]> list = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(path), StandardCharsets.UTF_8)) {
            while (sc.hasNextLine()) {
                list.add(sc.nextLine().split(";"));
            }
        } catch (FileNotFoundException f) {
            f.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void writeDataToFile(List<String[]> animalList, String path) throws IOException {
        OutputStream outStream = new FileOutputStream(path + fileName);
        if (bdIsEmpty) {
            String titles = "N;ID;Name;Day_of_birth;Commands\n";
            outStream.write(titles.getBytes(StandardCharsets.UTF_8));
            bdIsEmpty = false;
        }
        for (String[] animal : animalList) {
            outStream.write((animal[0] + ";" + animal[1] + ";" + animal[2] + ";" + animal[3] + ";" + animal[4] + ";\n").getBytes(StandardCharsets.UTF_8));
        }
        outStream.close();
    }
}
