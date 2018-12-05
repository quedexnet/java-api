package net.quedex.api.user;

public interface TimeTriggeredBatchListener {

    void onTimeTriggeredBatchAdded(TimeTriggeredBatchAdded timeTriggeredBatchAdded);

    void onTimeTriggeredBatchRejected(TimeTriggeredBatchRejected timeTriggeredBatchRejected);

    void onTimeTriggeredBatchExpired(TimeTriggeredBatchExpired timeTriggeredBatchExpired);

    void onTimeTriggeredBatchTriggered(TimeTriggeredBatchTriggered timeTriggeredBatchTriggered);

    void onTimeTriggeredBatchUpdated(TimeTriggeredBatchUpdated timeTriggeredBatchUpdated);

    void onTimeTriggeredBatchUpdateFailed(TimeTriggeredBatchUpdateFailed timeTriggeredBatchUpdateFailed);

    void onTimeTriggeredBatchCancelled(TimeTriggeredBatchCancelled timeTriggeredBatchCancelled);

    void onTimeTriggeredBatchCancelFailed(TimeTriggeredBatchCancelFailed timeTriggeredBatchCancelFailed);
}

