package com.jean.quizzer.utils;

import java.time.Duration;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseCookie;

@Configuration
public class CookieConfig {

    public ResponseCookie createJwtCookie(String token) {
        return ResponseCookie.from("jwt", token)
                .httpOnly(true)
                .secure(true)
                .maxAge(Duration.ofMinutes(15))
                .sameSite("Lax")
                .domain("localhost")
                .path("/")
                .build();
    }

}