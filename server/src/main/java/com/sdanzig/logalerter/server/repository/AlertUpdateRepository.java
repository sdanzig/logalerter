package com.sdanzig.logalerter.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sdanzig.logalerter.server.entities.AlertUpdateEntity;

public interface AlertUpdateRepository extends JpaRepository<AlertUpdateEntity, Long> {
	List<AlertUpdateEntity> findByIdGreaterThanOrderByIdAsc(Long newestAlertUpdateIdKnownByClient);
}
