package ru.gb.homework.src;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Notebook implements Comparable<Notebook>, Iterator<String>{//, Iterable<Paramtr>{
    protected static int number;
    protected int price;
    protected int ram;
    protected String brand;
    protected double screenDiagonal;
    protected String id;

    static {
        Notebook.number = 0;
    }

    public Notebook(int price, int ram, double screenDiagonal) {
        id = "Notebook id: #" + ++Notebook.number;
        this.price = price;
        this.ram = ram;
        brand = "HP";
        this.screenDiagonal = screenDiagonal;
    }


    public List<Parameter> parameters;
    int index;

    public Notebook() {
        parameters = new ArrayList<>();
        index = 0;
    }

    public void addParameters(Parameter parameter) {
        parameters.add(parameter);
    }

    public String getInfo() {
        return String.format("Price= %d, RAM = %d", price, ram);
    }

    @Override
    public String toString() {
        return String.valueOf(id + ", Manufacturer: \t" + brand + ", Screen diagonal: \t" + screenDiagonal + ", RAM :\t" + ram + ", Price :\t" + price + "\n");
    }

    @Override
    public int compareTo(Notebook n) {
        return this.price - n.price;
    }

    public int reverseCompareTo(Notebook n) {
        return n.price - this.price;
    }

    public int getPrice() {
        return price;
    }

    public int getRam() {
        return ram;
    }

    @Override
    public boolean hasNext() {
        return index++ < 5;
    }

    @Override
    public String next() {
        switch (index) {
            case 1:
                return String.format("%s", id);
            case 2:
                return String.format("Manufacturer: %s", brand);
            case 3:
                return String.format("Screen diagonal: %s", screenDiagonal);
            case 4:
                return String.format("RAM, GB %d", ram);
        }
        return String.format("Price: %d", price);
    }

//    @Override
//    public Iterator<Paramtr> iterator() {
//        return null;
//    }

//    @Override
//    public Iterator<Parameter> iterator() {
//        return null;
//    }

//
}

