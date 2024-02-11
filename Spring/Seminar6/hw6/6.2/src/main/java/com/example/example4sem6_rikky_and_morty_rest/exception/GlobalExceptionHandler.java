package com.example.example4sem6_rikky_and_morty_rest.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для контроллеров.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Обрабатывает исключение CharacterNotFoundException.
     * @param e Исключение CharacterNotFoundException
     * @param model Модель данных
     * @return Название страницы ошибки
     */
    @ExceptionHandler(CharacterNotFoundException.class)
    public String handleCharacterNotFoundException(CharacterNotFoundException e, Model model) {
        model.addAttribute("errorMessage", e.getMessage());
        return "characterNotFound";
    }
}
