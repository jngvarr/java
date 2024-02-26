package ru.gb.model.exceptions;

/**
 * Товар не найден.
 */
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
