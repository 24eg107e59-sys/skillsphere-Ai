package com.skillsphere.service;

import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.skillsphere.entity.User;
import com.skillsphere.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(User user) {
        String normalizedEmail = user.getEmail().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new IllegalArgumentException("Email already registered");
        }

        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        if (user.getRole() == null || user.getRole().trim().isEmpty()) {
            user.setRole("STUDENT");
        } else {
            String roleUpper = user.getRole().trim().toUpperCase();
            user.setRole(roleUpper.equals("ADMIN") ? "ADMIN" : "STUDENT");
        }

        return userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        String normalizedEmail = email.trim().toLowerCase();
        Optional<User> optionalUser = userRepository.findByEmail(normalizedEmail);
        if (optionalUser.isEmpty()) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        User user = optionalUser.get();
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        return user;
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email.trim().toLowerCase());
    }
}