package ru.gb.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.gb.model.exceptions.ExceptionBody;
import ru.gb.model.exceptions.ExcessAmountException;
import ru.gb.model.exceptions.ResourceNotFoundException;


import java.time.LocalDateTime;

/**
 * Контроллер обработки исключений.
 */
@RestControllerAdvice
public class AdviceController {
    /**
     * Исключение при превышении остатка товара.
     * @param e объект исключения.
     * @return обертку собственного исключения.
     */
    @ExceptionHandler(ExcessAmountException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody excessAmount(ExcessAmountException e){
        ExceptionBody exceptionBody = new ExceptionBody();
        exceptionBody.setMessage(e.getMessage());
        exceptionBody.setDateTime(LocalDateTime.now());
        return exceptionBody;
    }

    /**
     * Исключение при отсутствии товара.
     * @param e объект исключения.
     * @return обертку собственного исключения.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionBody excessAmount(ResourceNotFoundException e){
        ExceptionBody exceptionBody = new ExceptionBody();
        exceptionBody.setMessage(e.getMessage());
        exceptionBody.setDateTime(LocalDateTime.now());
        return exceptionBody;
    }

}
