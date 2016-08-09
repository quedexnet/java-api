package net.quedex.client.user;

@FunctionalInterface
public interface OpenPositionListener {

    void onOpenPosition(OpenPosition openPosition);
}
