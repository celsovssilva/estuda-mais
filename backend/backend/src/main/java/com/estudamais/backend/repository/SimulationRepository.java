package com.estudamais.backend.repository;

import com.estudamais.backend.entity.StudentSimulation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SimulationRepository  extends JpaRepository<StudentSimulation,Long> {
}
