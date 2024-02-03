package ru.gb.myWebApplication.services;

import ru.gb.myWebApplication.model.Note;

import java.util.List;

/**
 * Сервис для управления заметками
 */
public interface NoteService {
    /**
     * Создание новой заметки.
     *
     * @param note создаваемая заметка
     * @return созданная заметка
     */
    Note createNote(Note note);

    /**
     * Получить список всех заметок.
     *
     * @return список всех заметок
     */
    List<Note> getAll();

    /**
     * Просмотр заметки по ID
     *
     * @param id идентификатор просматриваемой заметки
     * @return сама заметка
     */
    Note getNoteById(long id);

    /**
     * Редактировать заметку
     *
     * @param note заметка для изменения
     * @return отредактированная заметка
     */
    Note editNote(Note note);

    /**
     * Удаление заметки
     *
     * @param id идентификатор удаляемой заметки
     */
    void delete(long id);
}
