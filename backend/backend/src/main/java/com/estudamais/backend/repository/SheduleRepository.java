package com.estudamais.backend.repository;

import com.estudamais.backend.entity.Schedule;
import com.estudamais.backend.entity.ScheduleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SheduleRepository extends JpaRepository<Schedule,Long> {
    List<Schedule> findByUserId(Long userId);
    List<Schedule> findByUserIdAndType(Long userId, ScheduleType type);
}
