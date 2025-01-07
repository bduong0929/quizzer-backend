package com.jean.quizzer.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jean.quizzer.models.FlashCard;

@Repository
public interface FlashCardRepository extends JpaRepository<FlashCard, Long> {
    void deleteAllByStudySetId(Long studySetId);

    List<FlashCard> findByStudySetId(Long studySetId);
}
