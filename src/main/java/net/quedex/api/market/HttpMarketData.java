package net.quedex.api.market;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.Config;
import net.quedex.api.common.MessageReceiver;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.pgp.BcSignatureVerifier;
import net.quedex.api.pgp.PGPExceptionBase;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

public class HttpMarketData implements MarketData {

    private static final ObjectMapper OBJECT_MAPPER = MessageReceiver.OBJECT_MAPPER;

    static {
        Unirest.setTimeouts(10_000, 10_000);
    }

    private final String instrumentDataUrl;
    private final BcSignatureVerifier signatureVerifier;

    public HttpMarketData(String instrumentDataUrl, BcPublicKey publicKey) {
        checkArgument(!instrumentDataUrl.isEmpty(), "Empty instrumentDataUrl");
        this.instrumentDataUrl = instrumentDataUrl;
        this.signatureVerifier = new BcSignatureVerifier(publicKey);
    }

    public HttpMarketData(Config config) {
        this(config.getInstrumentDataUrl(), config.getQdxPublicKey());
    }

    @Override
    public Map<Integer, Instrument> getInstruments() throws CommunicationException {
        try {
            String data = Unirest.get(instrumentDataUrl).asString().getBody();
            JsonNode metaJson = OBJECT_MAPPER.readTree(signatureVerifier.verifySignature(data));
            return OBJECT_MAPPER.treeToValue(metaJson.get("data"), InstrumentsMap.class);
        } catch (UnirestException | IOException | PGPExceptionBase e) {
            throw new CommunicationException("Error fetching instruments", e);
        }
    }

    private static class InstrumentsMap extends HashMap<Integer, Instrument> {}
}
