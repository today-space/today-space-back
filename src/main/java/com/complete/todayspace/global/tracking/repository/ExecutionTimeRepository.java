package com.complete.todayspace.global.tracking.repository;

import com.complete.todayspace.global.tracking.entity.ExecutionTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionTimeRepository extends JpaRepository<ExecutionTime, Long> {
}
