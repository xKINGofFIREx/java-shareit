package ru.practicum.shareit.exception;

import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Exception handleNotFoundException(NotFoundException e) {
        return new Exception("error", e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Exception handleValidationException(ValidationException e) {
        return new Exception("error", e);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Exception handleInvalidArgumentException(InvalidArgumentException e) {
        return new Exception("error", e);
    }

    @ExceptionHandler
    public ResponseEntity<UnsupportedStatusException> handleUnsupportedStatusException(ConversionFailedException e) {
        return new ResponseEntity<>(
                new UnsupportedStatusException(HttpStatus.BAD_REQUEST.value(),
                        "Unknown state: UNSUPPORTED_STATUS",
                        e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
}
