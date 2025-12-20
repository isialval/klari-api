package com.isidora.klari_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isidora.klari_api.model.Product;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> create(@RequestBody User user) {
        User created = userService.create(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<User> findByUsername(@PathVariable String username) {
        return userService.findbyUserName(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/favorites")
    public ResponseEntity<Set<Product>> getFavorites(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getFavorites(id));
    }

    @PostMapping("/{userId}/favorites/{productId}")
    public ResponseEntity<Void> addFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        userService.addFavorite(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/favorites/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        userService.removeFavorite(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/inventory")
    public ResponseEntity<Set<Product>> getInventory(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getInventory(id));
    }

    @PostMapping("/{userId}/inventory/{productId}")
    public ResponseEntity<Void> addToInventory(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        userService.addToInventory(userId, productId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{userId}/inventory/{productId}")
    public ResponseEntity<Void> removeFromInventory(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        userService.removeFromInventory(userId, productId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/skin-type")
    public ResponseEntity<SkinType> getSkinType(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getSkinType(id));
    }

    @PatchMapping("/{id}/skin-type")
    public ResponseEntity<Void> setSkinType(
            @PathVariable Long id,
            @RequestParam SkinType skinType) {
        userService.setSkinType(id, skinType);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/goals")
    public ResponseEntity<Set<Goal>> getGoals(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getGoals(id));
    }

    @PostMapping("/{id}/goals/{goal}")
    public ResponseEntity<Void> addGoal(
            @PathVariable Long id,
            @PathVariable Goal goal) {
        userService.addGoal(id, goal);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/goals/{goal}")
    public ResponseEntity<Void> removeGoal(
            @PathVariable Long id,
            @PathVariable Goal goal) {
        userService.removeGoal(id, goal);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/routines")
    public ResponseEntity<List<Routine>> getRoutines(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getRoutines(id));
    }
}