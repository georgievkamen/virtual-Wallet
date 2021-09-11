package com.team9.virtualwallet.config;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.EnumNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UnauthorizedOperationException.class)
    private ResponseEntity<Object> unauthorized(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, EnumNotFoundException.class})
    private ResponseEntity<Object> notFound(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, DuplicateEntityException.class})
    private ResponseEntity<Object> conflict(RuntimeException e) {
        return new ResponseEntity<>(e.getMessage(), new HttpHeaders(), HttpStatus.CONFLICT);
    }

}
