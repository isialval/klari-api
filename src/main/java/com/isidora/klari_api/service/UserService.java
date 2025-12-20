package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.isidora.klari_api.model.User;
import com.isidora.klari_api.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User create(User user) {
        return userRepository.save(user);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findbyId(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public Optional<User> findbyUserName(String userName) {
        return userRepository.findByUserName(userName);
    }
}
