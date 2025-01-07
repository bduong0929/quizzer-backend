package com.jean.quizzer.dtos.requests;

import java.util.List;

import lombok.Data;

@Data
public class FlashCardArray {
    private List<NewFlashCard> flashcards;
}
