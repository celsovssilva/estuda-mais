package com.estudamais.backend.repository;

import com.estudamais.backend.entity.StatusSimulado;
import com.estudamais.backend.entity.StudentSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SimulationRepository  extends JpaRepository<StudentSimulation,Long> {

    @Query("SELECT s FROM StudentSimulation s WHERE s.userId = :userId AND s.status = :status")
    Optional<StudentSimulation> findByUserIdAndStatus(
            @Param("userId") Long userId,
            @Param("status") StatusSimulado status
    );
}
