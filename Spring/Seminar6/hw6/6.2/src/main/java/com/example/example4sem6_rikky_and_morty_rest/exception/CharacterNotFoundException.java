package com.example.example4sem6_rikky_and_morty_rest.exception;

/**
 * Исключение, возникающее при отсутствии информации о персонаже.
 */
public class CharacterNotFoundException extends RuntimeException{
    /**
     * Конструктор класса CharacterNotFoundException.
     * @param message Сообщение об исключении
     */
    public CharacterNotFoundException(String message) {
        super(message);
    }
}
