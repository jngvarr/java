package jngvarr.ru.pto_ackye_rzhd.exceptions;


import java.nio.file.AccessDeniedException;

public class InvalidRefreshTokenException extends AccessDeniedException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
