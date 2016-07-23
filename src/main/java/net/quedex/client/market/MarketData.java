package net.quedex.client.market;

import java.util.Map;

public interface MarketData {

    Map<Integer, Instrument> getInstrumentData();
}
