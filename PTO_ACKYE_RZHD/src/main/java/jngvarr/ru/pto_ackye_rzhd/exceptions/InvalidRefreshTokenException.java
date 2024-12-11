package jngvarr.ru.pto_ackye_rzhd.exceptions;

import org.springframework.security.access.AccessDeniedException;

public class InvalidRefreshTokenException extends AccessDeniedException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
