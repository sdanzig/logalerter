package com.sdanzig.logalerter.server;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sdanzig.logalerter.common.dto.AlertUpdate;
import com.sdanzig.logalerter.common.dto.NewAlertOccurrenceData;
import com.sdanzig.logalerter.common.dto.UpdateFromClient;
import com.sdanzig.logalerter.common.dto.UpdateFromServer;
import com.sdanzig.logalerter.server.entities.AlertEntity;
import com.sdanzig.logalerter.server.entities.AlertUpdateEntity;
import com.sdanzig.logalerter.server.repository.AlertRepository;
import com.sdanzig.logalerter.server.repository.AlertUpdateRepository;

@Service
public class AlertUpdateServiceImpl implements AlertUpdateService {
	Logger logger = LoggerFactory.getLogger(AlertUpdateServiceImpl.class);

	private final AlertUpdateRepository alertUpdateRepository;
	private final AlertRepository alertRepository;

	@Autowired
	public AlertUpdateServiceImpl(AlertUpdateRepository alertUpdateRepository,
			AlertRepository alertRepository) {
		this.alertUpdateRepository = alertUpdateRepository;
		this.alertRepository = alertRepository;
	}

	@Async("asyncExecutor")
	@Transactional
	public CompletableFuture<UpdateFromServer> applyClientDataAndGetLatestServerData(UpdateFromClient fromClient,
			List<NewAlertOccurrenceData> newAlertOccurrenceDataList) {
		applyDataReceivedFromClient(fromClient, newAlertOccurrenceDataList);
		UpdateFromServer newAlertUpdatesForClient = generateNewAlertUpdatesToSendBackToClient(fromClient.getLatestAlertUpdateIdKnown());
		return CompletableFuture.completedFuture(newAlertUpdatesForClient);
	}

	@Override
	public AlertUpdateEntity createAddAlertUpdate(Long logId, AlertEntity createdAlert) {
		AlertUpdateEntity alertUpdate = new AlertUpdateEntity();
		alertUpdate.setAlertId(createdAlert.getId());
		alertUpdate.setChangeType(AlertUpdateEntity.AlertChange.ADD.name());
		alertUpdate.setLogId(logId);
		return alertUpdateRepository.save(alertUpdate);
	}

	private void applyDataReceivedFromClient(UpdateFromClient fromClient,
			List<NewAlertOccurrenceData> newAlertOccurrenceDataList) {
		if(newAlertOccurrenceDataList == null) {
			logger.error("Null list of alert occurrence data received from client with user email {}. " +
					"No data processed from client.", fromClient.getUserEmail());
			return;
		}
		List<AlertUpdateEntity> newAlertUpdates = new ArrayList<>();
		for (NewAlertOccurrenceData occurrenceData : newAlertOccurrenceDataList) {
			if (occurrenceData == null) {
				logger.error("Null alert occurrence data received from client with user email {}. Skipping.",
						fromClient.getUserEmail());
			}
			else {
				if (occurrenceData.getNewOccurrences() > 0) {
					alertRepository.updateWithNewOccurrences(occurrenceData.getAlertId(),
							occurrenceData.getNewOccurrences(),
							occurrenceData.getLastOccurrence(),
							fromClient.getUserEmail());
					AlertUpdateEntity alertUpdate = new AlertUpdateEntity();
					alertUpdate.setAlertId(occurrenceData.getAlertId());
					alertUpdate.setChangeType(AlertUpdateEntity.AlertChange.UPDATE.name());
					alertUpdate.setNewOccurrences(occurrenceData.getNewOccurrences());
					alertUpdate.setLastOccurrence(occurrenceData.getLastOccurrence());
					alertUpdate.setUserEmail(fromClient.getUserEmail());
					alertUpdate.setLogId(occurrenceData.getLogId());
					newAlertUpdates.add(alertUpdate);
				}
			}
		}
		alertUpdateRepository.saveAll(newAlertUpdates);
	}

	private UpdateFromServer generateNewAlertUpdatesToSendBackToClient(Long latestAlertUpdateIdKnown) {
		UpdateFromServer fromServer = new UpdateFromServer();
		List<AlertUpdateEntity> alertUpdateEntities = alertUpdateRepository.findByIdGreaterThanOrderByIdAsc(
				latestAlertUpdateIdKnown);
		List<AlertUpdate> alertUpdatesToSend = new ArrayList<>();
		loop:
		for(AlertUpdateEntity alertUpdateEntity : alertUpdateEntities) {
			AlertUpdate updateToSend = new AlertUpdate();
			updateToSend.setAlertUpdateId(alertUpdateEntity.getId());
			updateToSend.setAlertId(alertUpdateEntity.getAlertId());
			AlertUpdate.AlertChange changeType = AlertUpdate.AlertChange.valueOf(alertUpdateEntity.getChangeType());
			updateToSend.setChangeType(changeType);
			updateToSend.setAlertLogId(alertUpdateEntity.getLogId());
			switch(changeType) {
			case UPDATE:
				updateToSend.setNewOccurrences(alertUpdateEntity.getNewOccurrences());
				updateToSend.setLastOccurrence(alertUpdateEntity.getLastOccurrence());
			case ADD:
				// Flow through to provide full info for completely new alerts
				try {
					AlertEntity alert = alertRepository.findById(alertUpdateEntity.getAlertId()).orElseThrow();

					updateToSend.setNewAlertLabel(alert.getLabel());
					updateToSend.setNewAlertRegex(alert.getRegex());
					updateToSend.setNewAlertSeverity(alert.getSeverity());
				} catch (NoSuchElementException e) {
					logger.error("Alert ID {} not found for alert update ID {}. Skipping alert update",
							alertUpdateEntity.getAlertId(), alertUpdateEntity.getId());
					continue loop;
				}
				break;
			case REMOVE:
				break;
			default:
				logger.error("Alert update {}, for alert ID {}, detected with invalid change type {}",
						alertUpdateEntity.getId(),
						alertUpdateEntity.getAlertId(),
						alertUpdateEntity.getChangeType());
				continue loop;
			}
			alertUpdatesToSend.add(updateToSend);
		}
		fromServer.setAlertUpdates(alertUpdatesToSend);
		return fromServer;
	}
}
