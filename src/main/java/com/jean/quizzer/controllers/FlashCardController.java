package com.jean.quizzer.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jean.quizzer.dtos.requests.FlashCardArray;
import com.jean.quizzer.models.FlashCard;
import com.jean.quizzer.models.StudySet;
import com.jean.quizzer.models.User;
import com.jean.quizzer.services.FlashCardService;
import com.jean.quizzer.services.JwtService;
import com.jean.quizzer.services.StudySetService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/flashcards")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class FlashCardController {
    private final FlashCardService flashCardService;
    private final StudySetService studySetService;
    private final JwtService jwtService;

    @GetMapping("/{id}")
    public ResponseEntity<List<FlashCard>> getFlashCardsByStudySetId(@PathVariable Long id, HttpServletRequest req) {
        String token = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            token = Arrays.stream(cookies)
                    .filter((c) -> c.getName().equals("jwt"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (token == null || token.isEmpty() || !jwtService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User user = jwtService.decodeToken(token);
        StudySet studySet = studySetService.getStudySetById(id);
        if (studySet.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(flashCardService.getFlashCardsByStudySetId(id));
    }

    @Transactional
    @PostMapping("/{id}")
    public ResponseEntity<FlashCard> createFlashCard(HttpServletRequest req, @PathVariable Long id,
            @RequestBody FlashCardArray request) {
        String token = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            token = Arrays.stream(cookies)
                    .filter((c) -> c.getName().equals("jwt"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }
        if (token == null || token.isEmpty() || !jwtService.isValidToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        User user = jwtService.decodeToken(token);
        StudySet studySet = studySetService.getStudySetById(id);
        if (studySet.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        // Delete all flashcards in the db
        flashCardService.deleteAllFlashCardsByStudySetId(id);

        for (int i = 0; i < request.getFlashcards().size(); i++) {
            FlashCard flashCard = new FlashCard(request.getFlashcards().get(i), user, studySet, i);
            flashCardService.createFlashCard(flashCard);
        }
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
