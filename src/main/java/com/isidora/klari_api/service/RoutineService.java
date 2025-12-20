package com.isidora.klari_api.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.enums.RoutineType;
import com.isidora.klari_api.repository.RoutineRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoutineService {

    private final RoutineRepository routineRepository;

    public Routine create(Routine routine) {
        return routineRepository.save(routine);
    }

    public Optional<Routine> findById(Long id) {
        return routineRepository.findById(id);
    }

    public List<Routine> findByUserId(Long id) {
        return routineRepository.findByUserId(id);
    }

    public Optional<Routine> findActiveDayRoutine(Long userId) {
        return routineRepository.findActiveRoutine(RoutineType.DIA, userId);
    }

    public List<Routine> findInactiveDayRoutines(Long userId) {
        return routineRepository.findInactiveRoutines(RoutineType.DIA, userId);
    }

    public Optional<Routine> findActiveNightRoutine(Long userId) {
        return routineRepository.findActiveRoutine(RoutineType.NOCHE, userId);
    }

    public List<Routine> findInactiveNightRoutines(Long userId) {
        return routineRepository.findInactiveRoutines(RoutineType.NOCHE, userId);
    }

    @Transactional
    public void remove(Long routineId) {
        Routine routine = routineRepository.findById(routineId).orElse(null);

        if (routine != null) {
            routine.getProducts().clear();
            routineRepository.delete(routine);
        }
    }

}
