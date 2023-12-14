package ru.gb.hw;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import ru.gb.seminar.task2.ToDo;

import java.io.*;
import java.util.List;

public class Task2 {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final XmlMapper xmlMapper = new XmlMapper();


    public static void saveJSONData(Student student) throws IOException {
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.writeValue(new File("student.json"), student);
    }

    public static void saveXMLData(Student student) throws IOException {
        xmlMapper.writeValue(new File("student.json"), student);
    }


    public static Student loadXMLData() throws IOException {
        return xmlMapper.readValue(new File("student.xml"), Student.class);
    }

    public static Student loadJSONData() throws IOException {
        return objectMapper.readValue(new File("student.json"), Student.class);
    }
}

