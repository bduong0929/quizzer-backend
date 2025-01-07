package com.jean.quizzer.dtos.responses;

import com.jean.quizzer.models.User;

import lombok.Data;

@Data
public class Principal {
    private String email;

    public Principal() {
    }

    public Principal(User user) {
        this.email = user.getEmail();
    }
}
