package ru.gb.hw;

import java.io.*;

public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, IllegalAccessException {
        Student student = new Student("Иван", 20, 4.2);
        try (FileOutputStream fos = new FileOutputStream("student.bin");
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            System.out.println("Объект 'student' до сериализации: ");
            System.out.println(student);
            oos.writeObject(student);
            System.out.println("Объект 'student' сериализован.");
        }
        try (FileInputStream fis = new FileInputStream("student.bin");
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            Student studentDataFromBin = (Student) ois.readObject();
            System.out.println("Объект 'student' десериализован.\n");
            System.out.println("Объект 'student' после бинарной десериализации: ");
            System.out.println(studentDataFromBin);
        }

        Task2.saveJSONData(student);
        Task2.saveXMLData(student);
        Student studentDataFromXML = Task2.loadXMLData();
        Student studentDataFromJSON = Task2.loadJSONData();
        System.out.println("Объект 'student' после десериализации из .xml: \n" + studentDataFromXML);
        System.out.println("Объект 'student' после десериализации из .json: \n" + studentDataFromJSON);
//        Field[] fields = student.getClass().getDeclaredFields();
//        for (Field field : fields) {
//            field.setAccessible(true);
//            System.out.printf("%s: %s\n", field.getName(), field.get(student));
//        }
    }
}

