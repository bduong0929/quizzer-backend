package com.jean.quizzer.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.jean.quizzer.dtos.requests.LoginRequest;
import com.jean.quizzer.dtos.requests.RegisterRequest;
import com.jean.quizzer.dtos.responses.Principal;
import com.jean.quizzer.models.User;
import com.jean.quizzer.services.JwtService;
import com.jean.quizzer.services.UserService;
import com.jean.quizzer.utils.CookieConfig;

import java.util.Arrays;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final UserService userService;
    private final JwtService jwtService;
    private final CookieConfig cookieConfig;

    // Fetch the current user
    @GetMapping("/me")
    public ResponseEntity<Principal> getAuth(HttpServletRequest request, HttpServletResponse response) {
        String token = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            token = Arrays.stream(cookies)
                    .filter((c) -> c.getName().equals("jwt"))
                    .map(Cookie::getValue)
                    .findFirst()
                    .orElse(null);
        }

        if (token == null || token.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Check if the token is valid
        User user = jwtService.decodeToken(token);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // Get the user from the database
        user = userService.getUserByEmail(user.getEmail());

        // Check if the token is close to expiry
        if (jwtService.isCloseToExpiry(token)) {
            // Refresh the token
            String newToken = jwtService.generateToken(user);
            ResponseCookie cookie = cookieConfig.createJwtCookie(newToken);
            response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        }

        return ResponseEntity.ok(new Principal(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletResponse response) {
        ResponseCookie cookie = cookieConfig.createJwtCookie("");
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        response.addHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.addHeader("Pragma", "no-cache");
        return ResponseEntity.ok("Logout successful");
    }

    @PostMapping("/login")
    public ResponseEntity<Principal> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        User user = userService.login(request.getEmail(), request.getPassword());
        String token = jwtService.generateToken(user);
        ResponseCookie cookie = cookieConfig.createJwtCookie(token);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(new Principal(user));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        if (!userService.isValidEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body("Invalid email");
        }
        if (!userService.isUnqiueEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Email already exists");
        }
        if (!userService.isValidPassword(request.getPassword())) {
            return ResponseEntity.badRequest().body("Invalid password");
        }
        if (!userService.isPasswordMatch(request.getPassword(), request.getConfirmPassword())) {
            return ResponseEntity.badRequest().body("Passwords do not match");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        user.setPassword(hashedPassword);
        userService.createUser(user);

        return ResponseEntity.ok("Register successful");
    }
}
