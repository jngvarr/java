package ru.gb.myWebApplication.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gb.myWebApplication.model.Note;
import ru.gb.myWebApplication.services.NoteServiceImpl;

import java.util.List;
/**
 *  Контроллер, управляющий HTTP-запросами для операций с заметками
 */
@RestController
@RequestMapping("/notes")
@RequiredArgsConstructor
public class NoteController {
    private final NoteServiceImpl service;
    /**
     * Получить список всех заметок.
     *
     * @return список всех заметок
     */
    @GetMapping
    private ResponseEntity<List<Note>> getAll() {
        return new ResponseEntity<>(service.getAll(), HttpStatus.OK);
    }
    /**
     * Просмотр заметки по ID
     *
     * @param id идентификатор просматриваемой заметки
     * @return сама заметка
     */
    @GetMapping("/{id}")
    public ResponseEntity<Note> getById(@PathVariable long id) {
        Note noteById;
        try {
            noteById = service.getNoteById(id);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Note());
        }
        return new ResponseEntity<>(noteById, HttpStatus.OK);
    }
    /**
     * Создание новой заметки.
     *
     * @param note создаваемая заметка
     * @return созданная заметка
     */
    @PostMapping
    public ResponseEntity<Note> createNote(@RequestBody Note note) {
        return new ResponseEntity<>(service.createNote(note), HttpStatus.CREATED);
    }

    /**
     * Редактировать заметку
     *
     * @param note заметка для изменения
     * @return отредактированная заметка
     */
    @PutMapping
    public ResponseEntity<Note> updateNote(@RequestBody Note note) {
        return new ResponseEntity<>(service.editNote(note), HttpStatus.OK);
    }
    /**
     * Удаление заметки
     *
     * @param id идентификатор удаляемой заметки
     */
    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable long id) {
        service.delete(id);
        return ResponseEntity.ok().build();
    }
}
