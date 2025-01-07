package com.jean.quizzer.dtos.requests;

import lombok.Data;

@Data
public class NewFlashCard {
    private String term;
    private String definition;
}
