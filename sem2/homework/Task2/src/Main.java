import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class Main {
    public static void main(String[] args) {
// 2. С помощью Java создать файл file.txt, и записать в него слово TEST 100 раз.
// Если уже файл создан, то перезаписываем его.
        String str = "TEST";
        Path file = Path.of("file.txt");
        try {
            Files.createFile(file);
        } catch (IOException e) {
            System.out.println("File already exists.");
            ;
        }
        for (int i = 0; i < 100; i++) {
            try {
                Files.write(file, (str + "\n").getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                System.out.println("Что-то не пишется");
                ;
            }
        }
    }
}