import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Task2 {
    public static void main(String[] args) {
        String text = "Что такое хеш? Если коротко, то криптографическая хеш-функция, чаще называемая просто хешем, — " +
                "это математический алгоритм, преобразовывающий произвольный массив данных в состоящую из букв и цифр строку " +
                "фиксированной длины. Причем при условии использования того же типа хеша длина эта будет оставаться неизменной, " +
                "вне зависимости от объема вводных данных.";
        sortByLength(text);
    }

    private static void sortByLength(String text) {
        String[] splitText = text.split(" ");
        Map<Integer, List<String>> db = new TreeMap<>();
        for (String string : splitText) {
            List<String> temp = new ArrayList<>();
            if (db.containsKey(string.length())) {
                temp = db.get(string.length());
            }
            temp.add(string);
            db.put(string.length(), temp);
        }
        for(var item:db.entrySet()){
            System.out.printf("[%d: %s]\n", item.getKey(), item.getValue());
        }
//        System.out.println(db);
    }
}
/*
for (String string : splitText) {
        int count =0;
        while(db.containsKey(string.length() * 100 + count)) {
            count++;
        }
        db.putIfAbsent(string.length() * 100 + count, string);
    }
    System.out.println(db);
*/