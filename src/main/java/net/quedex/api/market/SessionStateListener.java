package net.quedex.api.market;

import net.quedex.api.market.SessionState;

@FunctionalInterface
public interface SessionStateListener {

    void onSessionState(SessionState newSessionState);
}
