package com.mercadolivre.users.app.exception;

import com.mercadolivre.users.core.exception.AgeBelowException;
import com.mercadolivre.users.core.exception.AlreadyExistsException;
import com.mercadolivre.users.core.exception.NotFoundException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

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
    public ResponseEntity<APIErrorDTO> handleIllegalArgumentException(final IllegalArgumentException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException("INVALID_FIELDS", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDTO);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<APIErrorDTO> handleNotFoundException(final NotFoundException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException(e.getCode(), e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDTO);
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<APIErrorDTO> handleIllegalStateException(final IllegalStateException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = getErrorMessageFromException("INVALID_APPLICATION_STATE", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDTO);
    }

    private APIErrorDTO getErrorMessageFromException(final String code, final String message) {
        return new APIErrorDTO(code, Map.of("en", message));
    }

}
