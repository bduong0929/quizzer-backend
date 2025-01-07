package com.jean.quizzer.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.jean.quizzer.models.StudySet;

@Repository
public interface StudySetRepository extends JpaRepository<StudySet, Long> {

}
