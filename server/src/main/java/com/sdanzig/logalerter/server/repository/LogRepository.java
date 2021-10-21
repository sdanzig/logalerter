package com.sdanzig.logalerter.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sdanzig.logalerter.server.entities.LogEntity;

@Repository
public interface LogRepository extends JpaRepository<LogEntity, Long> {
}
