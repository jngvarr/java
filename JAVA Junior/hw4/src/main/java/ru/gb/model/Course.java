package ru.gb.model;

import org.stringtemplate.v4.ST;

import javax.persistence.*;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Entity
@Table(name = "schoolDB.course")
public class Course {
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
        Map.Entry entry = (Map.Entry) listOfCourses.get(new Random().nextInt(4));
        return new Course((String) entry.getKey(), (Integer) entry.getValue());
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
