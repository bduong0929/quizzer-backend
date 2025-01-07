package com.jean.quizzer.utils.custom_exceptions;

public class StudySetNotFoundException extends RuntimeException {
    public StudySetNotFoundException(String message) {
        super(message);
    }
}
