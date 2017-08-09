package net.quedex.api.market;

import java.util.Map;

@FunctionalInterface
public interface InstrumentsListener {

    void onInstruments(Map<Integer, Instrument> instruments);
}
