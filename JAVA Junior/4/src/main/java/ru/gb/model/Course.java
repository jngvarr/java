package ru.gb.model;

import javax.persistence.*;
import java.util.Random;

@Entity
@Table(name = "courses")
public class Course {
    private static final Random rnd = new Random();

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;
    String title;
    int duration;


    public Course(String title, int duration) {
        this.title = title;
        this.duration = duration;
    }

    public Course() {
    }


    public static Course create() {
        String title = "Course #";
        return new Course(String.format("%s%d", title, rnd.nextInt(20)), rnd.nextInt(10));
    }

    public void update() {
        duration = rnd.nextInt(20);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", duration=" + duration +
                '}';
    }
}
