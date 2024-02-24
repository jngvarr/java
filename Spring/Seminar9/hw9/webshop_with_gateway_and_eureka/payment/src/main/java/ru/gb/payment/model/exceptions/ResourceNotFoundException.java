package ru.gb.payment.model.exceptions;

/**
 * Отсутствие переданного счета.
 */
public class ResourceNotFoundException extends RuntimeException{

    public ResourceNotFoundException(String message) {
        super(message);
    }
}
