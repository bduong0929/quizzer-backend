package com.jean.quizzer.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.jean.quizzer.dtos.requests.NewFlashCard;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "flash_cards")
@NoArgsConstructor
@AllArgsConstructor
public class FlashCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "term", columnDefinition = "TEXT")
    private String term;

    @Column(name = "definition", columnDefinition = "TEXT")
    private String definition;

    @Column(name = "position")
    private int position;

    @ManyToOne
    @JoinColumn(name = "study_set_id")
    @JsonBackReference
    private StudySet studySet;

    public FlashCard(NewFlashCard request, User user, StudySet studySet, int position) {
        this.term = request.getTerm();
        this.definition = request.getDefinition();
        this.position = position;
        this.studySet = studySet;
    }
}
