package net.quedex.api.market;

@FunctionalInterface
public interface SessionStateListener {

    void onSessionState(SessionState newSessionState);
}
