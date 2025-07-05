package com.blog.myblogs.exceptions;

public class InvalidPaginationParameterException extends RuntimeException {
    public InvalidPaginationParameterException() {
        super("Invalid pagination parameters: page and size must be greater than zero.");
    }
}
