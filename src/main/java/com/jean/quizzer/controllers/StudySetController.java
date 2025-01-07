package com.jean.quizzer.controllers;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jean.quizzer.dtos.requests.NewStudySetRequest;
import com.jean.quizzer.dtos.requests.UpdateStudySetRequest;
import com.jean.quizzer.models.StudySet;
import com.jean.quizzer.models.User;
import com.jean.quizzer.services.JwtService;
import com.jean.quizzer.services.StudySetService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/study-sets")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class StudySetController {
    private final StudySetService studySetService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<List<StudySet>> getStudySets(HttpServletRequest req) {
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

        return ResponseEntity.ok(studySetService.getStudySets());
    }

    @PostMapping
    public ResponseEntity<StudySet> createStudySet(HttpServletRequest req, @RequestBody NewStudySetRequest request) {
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
        StudySet studySet = new StudySet(request, user);
        return ResponseEntity.ok(studySetService.createStudySet(studySet));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<StudySet> updateStudySet(HttpServletRequest req, @PathVariable Long id,
            @RequestBody UpdateStudySetRequest request) {
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

        studySet.setTitle(request.getTitle());
        return ResponseEntity.ok(studySetService.updateStudySet(studySet));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudySet(HttpServletRequest req, @PathVariable Long id) {
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

        studySetService.deleteStudySet(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
