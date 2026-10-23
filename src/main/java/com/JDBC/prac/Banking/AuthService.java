
package com.JDBC.prac.Banking;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    // Check whether username already exists
    public boolean usernameExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    // Register a new user
    public boolean register(int customerID,
                            String username,
                            String password) {

        if (usernameExists(username)) {
            return false;
        }

        if (username == null || username.isBlank()
                || password == null || password.length() < 8) {
            return false;
        }

        String hashedPassword = passwordEncoder.encode(password);

        User user = new User(
                0,
                customerID,
                username.trim(),
                hashedPassword
        );

        userRepository.save(user);
        return true;
    }

    // Authenticate an existing user
    public User login(String username, String password) {

        if (username == null || password == null) {
            return null;
        }

        Optional<User> optionalUser =
                userRepository.findByUsername(username.trim());

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();

            if (passwordEncoder.matches(
                    password, user.getPasswordHash())) {
                return user;
            }
        }

        return null;
    }
}
