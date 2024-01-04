package ru.gb;

/**
 * hw1
 */
public class App {
    public static void main(String[] args) {
        Person john = new Person("John", "Jackson", 30);
        john.writePersonToFile(john,"personJohn");

        Person johnTheSame = new Person().readPersonFromFile("personJohn");
        System.out.println(johnTheSame);
    }
}
