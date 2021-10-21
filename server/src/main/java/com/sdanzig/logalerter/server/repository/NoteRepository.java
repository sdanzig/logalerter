package com.sdanzig.logalerter.server.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sdanzig.logalerter.server.entities.NoteEntity;

@Repository
public interface NoteRepository extends JpaRepository<NoteEntity, Long> {
	List<NoteEntity> findByAlertId(Long alertId);
}
