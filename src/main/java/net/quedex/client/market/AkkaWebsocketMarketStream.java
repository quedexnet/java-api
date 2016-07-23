package net.quedex.client.market;

import akka.Done;
import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.Http;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.model.ws.Message;
import akka.http.javadsl.model.ws.TextMessage;
import akka.http.javadsl.model.ws.WebSocketRequest;
import akka.http.javadsl.model.ws.WebSocketUpgradeResponse;
import akka.japi.Pair;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.*;

public class AkkaWebsocketMarketStream implements MarketStream {

    private static final Logger LOGGER = LoggerFactory.getLogger(AkkaWebsocketMarketStream.class);

    private final ActorSystem actorSystem;
    private final Materializer materializer;
    private final String marketStreamUrl;
    private volatile Subscriber<? super Message> websocketsSubscriber;

    private volatile OrderBookListener orderBookListener;
    private final Set<Integer> orderBookSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile TradeListener tradeListener;
    private final Set<Integer> tradeSubscriptions = Collections.newSetFromMap(new ConcurrentHashMap<>(64, 0.75f, 2));

    private volatile StreamFailureListener streamFailureListener;

    public AkkaWebsocketMarketStream(String marketStreamUrl) {
        actorSystem = ActorSystem.create();
        materializer = ActorMaterializer.create(actorSystem);
        this.marketStreamUrl = marketStreamUrl;
    }

    @Override
    public void registerOrderBookListener(OrderBookListener orderBookListener) {
        this.orderBookListener = orderBookListener;
    }

    @Override
    public void subscribeOrderBookListener(int instrumentId) {
        orderBookSubscriptions.add(instrumentId);
    }

    @Override
    public void unsubscribeOrderBookListener(int instrumentId) {
        orderBookSubscriptions.remove(instrumentId);
    }

    @Override
    public void registerTradeListener(TradeListener tradeListener) {

    }

    @Override
    public void subscribeTradeListener(int instrumentId) {

    }

    @Override
    public void unsubscribeTradeListener(int instrumentId) {

    }

    @Override
    public void registerQuotesListener(QuotesListener quotesListener) {

    }

    @Override
    public void subscribeQuotesListener(int instrumentId) {

    }

    @Override
    public void unsubscribeQuotesListener(int instrumentId) {

    }

    @Override
    public void registerAndSubscribeSessionStateListener(SessionStateListener sessionStateListener) {

    }

    @Override
    public void unsubscribeGlobalSessionStateListener() {

    }

    @Override
    public void registerStreamFailureListener(StreamFailureListener streamFailureListener) {
        this.streamFailureListener = streamFailureListener;
    }

    @Override
    public void start() throws CommunicationException {
        Http http = Http.get(actorSystem);

        Sink<Message, CompletionStage<Done>> sink = // TODO: may not be strict
                Sink.foreach(message -> {
                    TextMessage textMessage = message.asTextMessage();
                    String strictText;
                    if (textMessage.isStrict()) {
                        strictText = textMessage.getStrictText();
                    } else {
                        strictText = textMessage
                                .getStreamedText()
                                .runReduce((String s1, String s2) -> {
                                    System.out.println("s1 = " + s1);
                                    System.out.println("s2 = " + s2);
                                    return s1 + s2;
                                }, materializer)
                                .toCompletableFuture()
                                .get(1, TimeUnit.SECONDS); // TODO: this didn't work - alway timeout on msg > MTU size
                    }
                    this.processMessage(strictText);
                });

        Source<Message, NotUsed> source = Source.fromPublisher(subscriber -> subscriber.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                AkkaWebsocketMarketStream.this.websocketsSubscriber = subscriber;
            }

            @Override
            public void cancel() { /* no-op, close is handled in start() */}
        }));

        Flow<Message, Message, CompletionStage<WebSocketUpgradeResponse>> webSocketFlow =
                http.webSocketClientFlow(WebSocketRequest.create(marketStreamUrl));

        Pair<CompletionStage<WebSocketUpgradeResponse>, CompletionStage<Done>> pair =
                source.viaMat(webSocketFlow, Keep.right())
                        .toMat(sink, Keep.both())
                        .run(materializer);

        CompletableFuture<WebSocketUpgradeResponse> upgradeCompletion = pair.first().toCompletableFuture();
        CompletionStage<Done> websocketsClosed = pair.second();

        try {
            WebSocketUpgradeResponse upgrade = upgradeCompletion.get(10, TimeUnit.SECONDS);
            if (upgrade.response().status().equals(StatusCodes.SWITCHING_PROTOCOLS)) {
                LOGGER.info("Websocket connected successfully");
            } else {
                throw new CommunicationException(("Connection failed: " + upgrade.response().status()));
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new CommunicationException("Error connecting websocket", e);
        } catch (TimeoutException e) {
            actorSystem.terminate();
            throw new CommunicationException("Timeout connecting websocket", e);
        }

        websocketsClosed.whenComplete((done, t) -> {
            StreamFailureListener streamFailureListener = AkkaWebsocketMarketStream.this.streamFailureListener;
            if (streamFailureListener != null) {
                if (done != null) {
                    streamFailureListener.onStreamFailure(new CommunicationException("Websocket connection closed"));
                } else if (t != null) {
                    streamFailureListener.onStreamFailure(new CommunicationException("Websocket connection closed with error", t));
                }
            }
            actorSystem.terminate();
        });
    }

    @Override
    public void stop() throws CommunicationException {
        Subscriber<? super Message> websocketsSubscriber = this.websocketsSubscriber;
        if (websocketsSubscriber != null) {
            websocketsSubscriber.onComplete();
        }
        ActorSystem system = this.actorSystem;
        if (system != null) {
            system.awaitTermination();
        }
    }

    private void processMessage(String message) {
        System.out.println("message = " + message);
    }

    public static void main(String... args) throws Exception {

        MarketStream ms = new AkkaWebsocketMarketStream("wss://quedex.net:63002/market_stream");

        ms.registerStreamFailureListener(e -> LOGGER.error("stream failure", e));

        ms.start();

        new BufferedReader(new InputStreamReader(System.in)).readLine();
    }
}
