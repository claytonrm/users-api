package com.mercadolivre.users.app.exception;

import com.mercadolivre.users.core.exception.AgeBelowException;
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

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIErrorDTO> handleAgeBelowException(final AgeBelowException e) {
        logger.error(e.getMessage(), e);
        final APIErrorDTO errorDTO = new APIErrorDTO(e.getCode(), Map.of("en", e.getMessage()));
        return ResponseEntity.badRequest().body(errorDTO);
    }

}
