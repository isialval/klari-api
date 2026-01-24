package com.isidora.klari_api.controller;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isidora.klari_api.dto.ProductSummaryDTO;
import com.isidora.klari_api.dto.UserProfileDTO;
import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.User;
import com.isidora.klari_api.model.enums.Goal;
import com.isidora.klari_api.model.enums.ProductCategory;
import com.isidora.klari_api.model.enums.SkinType;
import com.isidora.klari_api.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<UserProfileDTO> findById(@PathVariable Long id) {
        User u = userService.findById(id);
        UserProfileDTO dto = new UserProfileDTO(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getSkinType(),
                u.getGoals());
        return ResponseEntity.ok(dto);
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

    @GetMapping("/{id}/favorites/summary")
    public ResponseEntity<Page<ProductSummaryDTO>> getFavoriteSummaries(
            @PathVariable Long id,
            @RequestParam(required = false) ProductCategory category,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        if (category != null) {
            return ResponseEntity.ok(userService.getFavoriteSummariesByCategory(id, category, pageable));
        }
        return ResponseEntity.ok(userService.getFavoriteSummaries(id, pageable));
    }

    @GetMapping("/{id}/inventory/summary")
    public ResponseEntity<Page<ProductSummaryDTO>> getInventorySummaries(
            @PathVariable Long id,
            @RequestParam(required = false) ProductCategory category,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        if (category != null) {
            return ResponseEntity.ok(userService.getInventorySummariesByCategory(id, category, pageable));
        }
        return ResponseEntity.ok(userService.getInventorySummaries(id, pageable));
    }

    @GetMapping("/{userId}/favorites/{productId}/exists")
    public ResponseEntity<Boolean> isFavorite(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(userService.isFavorite(userId, productId));
    }

    @GetMapping("/{userId}/inventory/{productId}/exists")
    public ResponseEntity<Boolean> isInInventory(
            @PathVariable Long userId,
            @PathVariable Long productId) {
        return ResponseEntity.ok(userService.isInInventory(userId, productId));
    }
}