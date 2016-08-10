package net.quedex.client.market;

import net.quedex.client.commons.CommunicationException;

import java.util.Map;

public interface MarketData {

    Map<Integer, Instrument> getInstruments() throws CommunicationException;
}
