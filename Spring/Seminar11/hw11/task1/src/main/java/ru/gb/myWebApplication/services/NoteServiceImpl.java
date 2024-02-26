package ru.gb.myWebApplication.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.gb.myWebApplication.model.Note;
import ru.gb.myWebApplication.repository.NoteRepository;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Optional;

/**
 * Реализация сервиса для управления заметками
 */
@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository repository;

    /**
     * Создание новой заметки.
     *
     * @param note новая заметка для добавления
     * @return Добавленная заметка
     */
    @Override
    public Note createNote(Note note) {
        return repository.save(note);
    }

    /**
     * Получить список всех заметок.
     *
     * @return список всех заметок
     */
    @Override
    public List<Note> getAll() {
        return repository.findAll();
    }

    /**
     * Просмотр заметки по ID
     *
     * @param id идентификатор просматриваемой заметки
     * @return сама заметка
     */
    @Override
    public Note getNoteById(long id) {
        return repository.findById(id).orElseThrow();
    }

    /**
     * Редактировать заметку
     *
     * @param note заметка для изменения
     * @return отредактированная заметка
     */
    @Override
    public Note editNote(Note note) {
        Optional<Note> optionalNote = repository.findById(note.getId());
        Note newNote = null;
        if (optionalNote.isPresent()) {
            newNote = optionalNote.get();
            newNote.setTitle(note.getTitle());
            newNote.setContent(note.getContent());
        }
        assert newNote != null;
        return repository.save(newNote);
    }

    /**
     * Удаление заметки
     *
     * @param id идентификатор удаляемой заметки
     */
    @Override
    public void delete(long id) {
        Note toDelete = getNoteById(id);
        repository.delete(toDelete);
    }
}
