package net.quedex.client.commons;

import net.quedex.client.market.SessionState;

@FunctionalInterface
public interface SessionStateListener {

    void onSessionState(SessionState newSessionState);
}
