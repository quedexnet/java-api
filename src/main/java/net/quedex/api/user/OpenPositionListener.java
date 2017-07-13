package net.quedex.api.user;

@FunctionalInterface
public interface OpenPositionListener {

    void onOpenPosition(OpenPosition openPosition);
}
