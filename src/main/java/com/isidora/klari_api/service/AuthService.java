package com.isidora.klari_api.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.isidora.klari_api.model.User;
import com.isidora.klari_api.repository.UserRepository;
import com.isidora.klari_api.security.JwtUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse register(String username, String email, String password) {

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("El correo ya está registrado");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername());

        return new AuthResponse(saved.getId(), saved.getUsername(), saved.getEmail(), token);
    }

    public AuthResponse login(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), token);
    }

    public record AuthResponse(Long userId, String username, String email, String token) {
    }

}
