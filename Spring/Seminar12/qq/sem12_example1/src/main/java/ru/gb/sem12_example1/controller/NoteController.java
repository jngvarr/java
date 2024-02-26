package ru.gb.sem12_example1.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gb.sem12_example1.model.Note;
import ru.gb.sem12_example1.service.FileGateway;

import java.time.LocalDateTime;

/**
 * Ну, это наш рест контролёр
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/notes")
public class NoteController {
    private final FileGateway fileGateway;


    /**
     * Добавить заметку
     * @param note заметка
     * @return заметка
     */
    @PostMapping
    public ResponseEntity<Note> addNote(@RequestBody Note note) {
        note.setCreation(LocalDateTime.now());
        fileGateway.writeToFile(note.getTitle() + ".txt", note.toString());
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
