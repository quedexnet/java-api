package net.quedex.client.market;

@FunctionalInterface
public interface SessionStateListener {

    void onSessionState(SessionState newSessionState);
}
