package com.jean.quizzer.services;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import com.jean.quizzer.models.User;
import com.jean.quizzer.repositories.UserRepository;
import com.jean.quizzer.utils.custom_exceptions.AuthException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User login(String email, String password) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("Invalid email or password"));
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new AuthException("Invalid email or password");
        }
        return user;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(User user) {
        return userRepository.save(user);
    }

    public boolean isValidEmail(String email) {
        return email.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    }

    public boolean isUnqiueEmail(String email) {
        return userRepository.findByEmail(email).isEmpty();
    }

    public boolean isValidPassword(String password) {
        return password.matches("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$");
    }

    public boolean isPasswordMatch(String password, String confirmPassword) {
        return password.equals(confirmPassword);
    }
}
