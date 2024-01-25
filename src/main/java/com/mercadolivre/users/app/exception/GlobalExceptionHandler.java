package com.mercadolivre.users.app.exception;

import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.AlreadyExistsException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(AgeBelowException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIErrorDTO> handleAgeBelowException(final AgeBelowException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException(e.getCode(), e.getMessage());
        return ResponseEntity.badRequest().body(errorDTO);
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<APIErrorDTO> handleAgeBelowException(final AlreadyExistsException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDTO);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIErrorDTO> handleAgeBelowException(final IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException("INVALID_FIELDS", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    private APIErrorDTO getErrorMessageFromException(final String code, final String message) {
        return new APIErrorDTO(code, Map.of("en", message));
    }

}
