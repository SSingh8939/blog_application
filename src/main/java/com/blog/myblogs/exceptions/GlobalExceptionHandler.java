package com.blog.myblogs.exceptions;

import com.blog.myblogs.common.*;

import java.util.Map;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(ResourceNotFoundException ex) {
        return ResponseGenerator.generateResponse(ex.getMessage(), HttpStatus.NOT_FOUND, null);
    }

    @ExceptionHandler(InvalidPaginationParameterException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidPagination(InvalidPaginationParameterException ex) {
        return ResponseGenerator.generateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception ex) {
        ex.printStackTrace();
        return ResponseGenerator.generateResponse("Unexpected error occurred ", HttpStatus.INTERNAL_SERVER_ERROR, null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        // Map<String, String> errors = new java.util.HashMap<>();
        // ex.getBindingResult().getFieldErrors()
        // .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseGenerator.generateResponse("Validation failed", HttpStatus.BAD_REQUEST, null);
    }
}