package ru.gb.homework.src;

import javax.security.auth.login.Configuration;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class Notebook implements Comparable<Notebook>, Iterable<Parameter> {
    protected int price;
    protected int ram;
    protected String brand;
    protected double screenDiagonal;


    public Notebook(int price, int ram) {
        this.price = price;
        this.ram = ram;
        brand = "HP";
        screenDiagonal = 17.3;

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
        return String.valueOf("Price =\t" + price + ", Ram =\t" + ram + "\n");
    }

    @Override
    public int compareTo(Notebook n) {
        return n.price - this.price;
    }

    public int reverseCompareTo(Notebook n) {
        return this.price - n.price;
    }

    public int getPrice() {
        return price;
    }

    public int getRam() {
        return ram;
    }

    @Override
    public Iterator<Parameter> iterator() {
        return null;
    }
}

