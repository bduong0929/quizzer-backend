package com.jean.quizzer.utils.custom_exceptions;

public class AuthException extends RuntimeException {
    public AuthException(String msg) {
        super(msg);
    }
}
