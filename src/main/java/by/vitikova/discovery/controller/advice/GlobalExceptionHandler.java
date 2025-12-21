package by.vitikova.discovery.controller.advice;

import by.vitikova.discovery.ErrorDto;
import by.vitikova.discovery.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorDto> error(ValidationException e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityIsExistsException.class)
    public ResponseEntity<ErrorDto> error(EntityIsExistsException e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.CONFLICT);
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityCreateException.class)
    public ResponseEntity<ErrorDto> error(EntityCreateException e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorDto> error(EntityNotFoundException e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.NOT_FOUND);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }

    @ExceptionHandler(PasswordUpdateException.class)
    public ResponseEntity<ErrorDto> error(PasswordUpdateException e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDto> error(Exception e) {
        var errorResponse = this.buildErrorResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse);
    }

    private ErrorDto buildErrorResponse(String message, HttpStatus code) {
        return new ErrorDto(message, code.value());
    }
}