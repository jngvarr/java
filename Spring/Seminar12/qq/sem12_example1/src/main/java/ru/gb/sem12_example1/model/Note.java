package ru.gb.sem12_example1.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;


@AllArgsConstructor
@NoArgsConstructor
@Data
public class Note {
    private Long id;
    private String title;
    private String body;
    private LocalDateTime creation;

    public Note(String title, String body) {
        this.title = title;
        this.body = body;
    }
}
