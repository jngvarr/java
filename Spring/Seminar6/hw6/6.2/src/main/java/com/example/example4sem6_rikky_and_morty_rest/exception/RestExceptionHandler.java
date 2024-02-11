package com.example.example4sem6_rikky_and_morty_rest.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * Глобальный обработчик исключений для REST контроллеров.
 */

//@ControllerAdvice
//public class RestExceptionHandler {
//        /**
//         * Обрабатывает исключение CharacterNotFoundException.
//         * @param e Исключение CharacterNotFoundException
//         * @return Ответ с ошибкой 404 и сообщением "Character not found"
//         */
//    @ExceptionHandler(CharacterNotFoundException.class)
//    ResponseEntity<String> handleCharacterNotFoundException(CharacterNotFoundException e) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null); //"Character not found"
//    }
//}

/**
 * Когда я пробовала создать отдельный обработчки для RESTful - при неверном id получаю статус 200
 * и в теле html код страницы characterNotFound.html. Поэтому пока оставила один обработчик для
 * CharacterController.java, а обработку исключения для RESTful добавила непосредственно в метод.
 * Почитаю еще, как это можно сделать одновременно и корректно.
 */