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

    public AuthResponse register(String username, String password) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        User user = new User();

        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));

        User saved = userRepository.save(user);

        String token = jwtUtil.generateToken(saved.getId(), saved.getUsername());

        return new AuthResponse(saved.getId(), saved.getUsername(), token);
    }

    public AuthResponse login(String username, String password) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getUsername());

        return new AuthResponse(user.getId(), user.getUsername(), token);
    }

    public record AuthResponse(Long userId, String username, String token) {
    }
}
