package com.isidora.klari_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.isidora.klari_api.model.Routine;
import com.isidora.klari_api.model.enums.RoutineType;

@Repository
public interface RoutineRepository extends JpaRepository<Routine, Long> {

    List<Routine> findByUserId(Long userId);

    @Query("Select r FROM Routine r WHERE " +
            "r.routineType = :routineType " +
            "AND r.active = true " +
            "AND r.user.id = :userId")
    Optional<Routine> findActiveRoutine(RoutineType routineType, Long userId);

    @Query("Select r FROM Routine r WHERE " +
            "r.routineType = :routineType " +
            "AND r.active = false " +
            "AND r.user.id = :userId")
    List<Routine> findInactiveRoutines(RoutineType routineType, Long userId);

}