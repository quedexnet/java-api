## Quedex Java API

### Usage

Include to your project as a Maven dependency:

```
<dependency>
    <groupId>net.quedex</groupId>
    <artifactId>java-api</artifactId>
    <version>0.1</version>
</dependency>

```

Using the API:

```
char[] pwd = ... // read private key passphrase 
Config qdxConfig = Config.fromResource(); // initialise the config using one of the factory methods

MarketData marketData = new HttpMarketData(qdxConfig);
MarketStream marketStream = new WebsocketMarketStream(qdxConfig);
UserStream userStream = new WebsocketUserStream(qdxConfig);

 // get tradeable instruments
Map<Integer, Instrument> instruments = marketData.getInstruments();

// register stream failure listeners
marketStream.registerStreamFailureListener(...);
userStream.registerStreamFailureListener(...)

// start streams
marketStream.start();
userStream.start();

// register and subscribe market stream listeners
marketStream.registerQuotesListener(...).subscribe(instruments.keySet()); // to subscribe all instruments
marketStream.register*(...).subscribe(...);

// register user stream listeners
userStream.registerOpenPositionListener(...);
userStream.register*(...);

// subscribe user stream listeners; see Javadoc for details
userStream.subscribeListeners();

// play with the streams: receive events, place orders and so on
userStream.placeOrder(...);
userStream.batch()
    .placeOrder(...)
    ...
    .send();
...

// once finished, stop the streams
userStream.stop();
marketStream.stop();
```