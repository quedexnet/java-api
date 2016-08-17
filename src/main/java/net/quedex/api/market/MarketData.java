package net.quedex.api.market;

import net.quedex.api.common.CommunicationException;

import java.util.Map;

public interface MarketData {

    /**
     * @return a map from instrument ids to instruments
     */
    Map<Integer, Instrument> getInstruments() throws CommunicationException;
}
