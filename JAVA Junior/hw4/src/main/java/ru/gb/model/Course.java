package ru.gb.model;

import org.stringtemplate.v4.ST;

import javax.persistence.*;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "schoolDB.course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    int id;
    String title;
    int duration;
    private static Map<String, int> courses;
    Map.Entry("1", 5),
            Map.Entry("", 3),Map.Entry(" ",6);

    public Course(String title, int duration) {
        this.title = title;
        this.duration = duration;
    }

    public Course() {
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
