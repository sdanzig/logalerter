package com.sdanzig.logalerter.server;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.sdanzig.logalerter.common.dto.NewAlertOccurrenceData;
import com.sdanzig.logalerter.common.dto.UpdateFromClient;
import com.sdanzig.logalerter.common.dto.UpdateFromServer;
import com.sdanzig.logalerter.server.entities.AlertEntity;
import com.sdanzig.logalerter.server.entities.AlertUpdateEntity;

public interface AlertUpdateService {
	CompletableFuture<UpdateFromServer> applyClientDataAndGetLatestServerData(UpdateFromClient fromClient, List<NewAlertOccurrenceData> newAlertOccurrenceDataList);

	AlertUpdateEntity createAddAlertUpdate(Long logId, AlertEntity createdAlert);
}
