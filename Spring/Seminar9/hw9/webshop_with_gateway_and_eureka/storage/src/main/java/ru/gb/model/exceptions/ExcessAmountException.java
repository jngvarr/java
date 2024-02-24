package ru.gb.model.exceptions;

/**
 * Превышение остатка товара на складе.
 */
public class ExcessAmountException extends RuntimeException {
    public ExcessAmountException(String message) {
        super(message);
    }
}
