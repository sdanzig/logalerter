package com.sdanzig.logalerter.server.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.sdanzig.logalerter.server.entities.AlertEntity;

@Repository
public interface AlertRepository extends JpaRepository<AlertEntity, Long> {
	List<AlertEntity> findAllByLogId(long logId);

	List<AlertEntity> findTopByIdGreaterThanOrderByIdAsc(Long newestAlertIdKnownByClient);

	@Modifying
	@Query("update AlertEntity a set a.occurrences = a.occurrences + :newOccurrences, a.lastOccurrence = :lastOccurrence, a.lastUserEmail = :lastUserEmail where a.id = :alertId")
	void updateWithNewOccurrences(@Param("alertId") Long alertId, @Param("newOccurrences") Integer newOccurrences, @Param("lastOccurrence") Date lastOccurrence, @Param("lastUserEmail") String lastUserEmail);
}
