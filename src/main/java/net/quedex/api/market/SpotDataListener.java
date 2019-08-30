package net.quedex.api.market;

@FunctionalInterface
public interface SpotDataListener {

    void onSpotData(SpotDataWrapper spotDataWrapper);
}

