package com.task.test.footballmanager.exception.handler;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.task.test.footballmanager.exception.EntityAlreadyExistsException;
import com.task.test.footballmanager.exception.EntityNotExistsException;
import com.task.test.footballmanager.exception.InvalidEntityException;

import static org.springframework.http.HttpStatus.NOT_ACCEPTABLE;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CustomRestExceptionHandler extends ResponseEntityExceptionHandler {

    private ResponseEntity<Object> buildResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(apiError, apiError.getStatus());
    }

    @ExceptionHandler(EntityNotExistsException.class)
    protected ResponseEntity<Object> handleEntityNotExists(
        EntityNotExistsException ex) {
        ApiError apiError = new ApiError(NOT_FOUND);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(InvalidEntityException.class)
    protected ResponseEntity<Object> handleInvalidEntity(
        InvalidEntityException ex) {
        ApiError apiError = new ApiError(NOT_ACCEPTABLE);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }

    @ExceptionHandler(EntityAlreadyExistsException.class)
    protected ResponseEntity<Object> handleEntityAlreadyExists(
        EntityAlreadyExistsException ex) {
        ApiError apiError = new ApiError(NOT_ACCEPTABLE);
        apiError.setMessage(ex.getMessage());
        return buildResponseEntity(apiError);
    }
}
