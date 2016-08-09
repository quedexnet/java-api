package net.quedex.client.account;

@FunctionalInterface
public interface OpenPositionListener {

    void onOpenPosition(OpenPosition openPosition);
}
