package com.blog.myblogs.exceptions;

import com.blog.myblogs.common.*;

import org.springframework.http.*;
import org.springframework.security.authentication.BadCredentialsException;
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
        return ResponseGenerator.generateResponse("Validation failed", HttpStatus.BAD_REQUEST, null);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateData(DuplicateDataException ex) {
        return ResponseGenerator.generateResponse(ex.getMessage(), HttpStatus.CONFLICT, null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException ex) {
        return ResponseGenerator.generateResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, null);
    }
}