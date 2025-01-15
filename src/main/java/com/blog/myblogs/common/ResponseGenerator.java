package com.blog.myblogs.common;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseGenerator {
    public static ResponseEntity<Object> generateResponse(
            String message,
            HttpStatus status,
            Object responseObj) {
        Map<String, Object> map = new HashMap<>();
        map.put("message", message);
        map.put("data", responseObj);

        return new ResponseEntity<>(map, status);
    }

}
