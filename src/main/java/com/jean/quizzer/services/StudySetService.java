package com.jean.quizzer.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jean.quizzer.models.StudySet;
import com.jean.quizzer.repositories.StudySetRepository;
import com.jean.quizzer.utils.custom_exceptions.StudySetNotFoundException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudySetService {
    private final StudySetRepository studySetRepository;

    public List<StudySet> getStudySets() {
        return studySetRepository.findAll();
    }

    public StudySet createStudySet(StudySet studySet) {
        return studySetRepository.save(studySet);
    }

    public StudySet updateStudySet(StudySet studySet) {
        return studySetRepository.save(studySet);
    }

    public StudySet getStudySetById(Long id) {
        return studySetRepository.findById(id).orElseThrow(() -> new StudySetNotFoundException("Study set not found"));
    }

    public void deleteStudySet(Long id) {
        studySetRepository.deleteById(id);
    }
}
