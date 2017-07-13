package net.quedex.api.common;

import net.quedex.api.market.SessionState;

@FunctionalInterface
public interface SessionStateListener {

    void onSessionState(SessionState newSessionState);
}
