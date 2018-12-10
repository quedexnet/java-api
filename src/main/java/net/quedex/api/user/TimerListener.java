package net.quedex.api.user;

public interface TimerListener {

    void onTimerAdded(TimerAdded timerAdded);

    void onTimerRejected(TimerRejected timerRejected);

    void onTimerExpired(TimerExpired timerExpired);

    void onTimerTriggered(TimerTriggered timerTriggered);

    void onTimerUpdated(TimerUpdated timerUpdated);

    void onTimerUpdateFailed(TimerUpdateFailed timerUpdateFailed);

    void onTimerCancelled(TimerCancelled timerCancelled);

    void onTimerCancelFailed(TimerCancelFailed timerCancelFailed);
}

