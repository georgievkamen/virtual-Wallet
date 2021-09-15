package com.team9.virtualwallet.config;

import com.team9.virtualwallet.exceptions.DuplicateEntityException;
import com.team9.virtualwallet.exceptions.EntityNotFoundException;
import com.team9.virtualwallet.exceptions.EnumNotFoundException;
import com.team9.virtualwallet.exceptions.UnauthorizedOperationException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(value = UnauthorizedOperationException.class)
    private void unauthorized(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.UNAUTHORIZED.value());
    }

    @ExceptionHandler(value = {EntityNotFoundException.class, EnumNotFoundException.class})
    private void notFound(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NOT_FOUND.value());
    }

    @ExceptionHandler(value = {IllegalArgumentException.class, DuplicateEntityException.class})
    public void conflict(HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.CONFLICT.value());
    }

    public static void checkFields(BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, Objects.requireNonNull(result.getFieldError()).getDefaultMessage());
        }
    }

}
