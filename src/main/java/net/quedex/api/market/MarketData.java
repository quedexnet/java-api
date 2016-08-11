package net.quedex.api.market;

import net.quedex.api.common.CommunicationException;

import java.util.Map;

public interface MarketData {

    Map<Integer, Instrument> getInstruments() throws CommunicationException;
}
