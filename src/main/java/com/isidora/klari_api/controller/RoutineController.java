package com.isidora.klari_api.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.service.RoutineService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/routines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RoutineController {

    private final RoutineService routineService;

    @PostMapping
    public ResponseEntity<Routine> create(@RequestBody Routine routine) {
        Routine created = routineService.create(routine);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Routine> findById(@PathVariable Long id) {
        return routineService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Routine>> findByUserId(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.findByUserId(userId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remove(@PathVariable Long id) {
        routineService.remove(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/day/active")
    public ResponseEntity<Routine> findActiveDayRoutine(@PathVariable Long userId) {
        return routineService.findActiveDayRoutine(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/day/inactive")
    public ResponseEntity<List<Routine>> findInactiveDayRoutines(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.findInactiveDayRoutines(userId));
    }

    @GetMapping("/user/{userId}/night/active")
    public ResponseEntity<Routine> findActiveNightRoutine(@PathVariable Long userId) {
        return routineService.findActiveNightRoutine(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}/night/inactive")
    public ResponseEntity<List<Routine>> findInactiveNightRoutines(@PathVariable Long userId) {
        return ResponseEntity.ok(routineService.findInactiveNightRoutines(userId));
    }

    // ✅ Sin try-catch - el GlobalExceptionHandler maneja los errores
    @PostMapping("/user/{userId}/day/initial")
    public ResponseEntity<Routine> createInitialDayRoutine(@PathVariable Long userId) {
        Routine routine = routineService.createInitialDayRoutine(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(routine);
    }

    @PostMapping("/user/{userId}/night/initial")
    public ResponseEntity<Routine> createInitialNightRoutine(@PathVariable Long userId) {
        Routine routine = routineService.createInitialNightRoutine(userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(routine);
    }

    @PostMapping("/{routineId}/products/{productId}")
    public ResponseEntity<Void> addProduct(
            @PathVariable Long routineId,
            @PathVariable Long productId) {
        routineService.addProduct(routineId, productId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{routineId}/products/{productId}")
    public ResponseEntity<Void> removeProduct(
            @PathVariable Long routineId,
            @PathVariable Long productId) {
        routineService.removeProduct(routineId, productId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Void> activate(@PathVariable Long id) {
        routineService.activate(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        routineService.deactivate(id);
        return ResponseEntity.ok().build();
    }
}