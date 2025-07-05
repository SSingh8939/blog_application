package com.blog.myblogs.common;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {
    public static <T> ResponseEntity<ApiResponse<T>> generateResponse(
            String message,
            HttpStatus status,
            T data) {

        ApiResponse<T> response = ApiResponse.<T>builder()
                .message(message)
                .status(status.value())
                .data(data).build();

        return new ResponseEntity<>(response, status);
    }

}
