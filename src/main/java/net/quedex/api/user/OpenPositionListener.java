package net.quedex.api.user;

public interface OpenPositionListener {

    void onOpenPosition(OpenPosition openPosition);

    void onOpenPositionForcefullyClosed(OpenPositionForcefullyClosed openPositionForcefullyClosed);
}
