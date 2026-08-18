package com.estudamais.backend.repository;

import com.estudamais.backend.entity.StatusSimulado;
import com.estudamais.backend.entity.StudentSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SimulationRepository  extends JpaRepository<StudentSimulation,Long> {

    Optional<StudentSimulation> findByUserIdAndStatus(Long userId, StatusSimulado statusSimulado);
}
