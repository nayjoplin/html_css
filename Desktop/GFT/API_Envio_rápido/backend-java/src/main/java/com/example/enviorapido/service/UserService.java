package com.example.enviorapido.service;

import com.example.enviorapido.model.User;
import com.example.enviorapido.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User register(String username, String rawPassword) {
        User u = new User();
        u.setUsername(username);
        u.setPassword(passwordEncoder.encode(rawPassword));
        u.setRoles(List.of("ROLE_USER"));
        return userRepository.save(u);
    }

    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
