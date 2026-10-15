package com.springboot.careercrafter.service;

import com.springboot.careercrafter.enums.Role;
import com.springboot.careercrafter.exception.InvalidCallException;
import com.springboot.careercrafter.model.User;
import com.springboot.careercrafter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User createUser(String username, String password, Role role) {
        if (userRepository.existsByUsername(username))
            throw new InvalidCallException("Username already taken");

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        return userRepository.save(user);
    }
}
