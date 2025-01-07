package com.jean.quizzer.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.jean.quizzer.utils.custom_exceptions.AuthException;
import com.jean.quizzer.utils.custom_exceptions.StudySetNotFoundException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<String> handleAuthException(AuthException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(StudySetNotFoundException.class)
    public ResponseEntity<String> handleStudySetNotFoundException(StudySetNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
    }
}
