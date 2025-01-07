package com.jean.quizzer.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jean.quizzer.models.FlashCard;
import com.jean.quizzer.repositories.FlashCardRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FlashCardService {
    private final FlashCardRepository flashCardRepository;

    public FlashCard createFlashCard(FlashCard flashCard) {
        return flashCardRepository.save(flashCard);
    }

    public void deleteAllFlashCardsByStudySetId(Long studySetId) {
        flashCardRepository.deleteAllByStudySetId(studySetId);
    }

    public List<FlashCard> getFlashCardsByStudySetId(Long studySetId) {
        return flashCardRepository.findByStudySetId(studySetId);
    }
}
