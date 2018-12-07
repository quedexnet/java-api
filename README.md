# Quedex Official Java API

> The best way to communicate with [Quedex Bitcoin Derivatives Exchange](https://quedex.net)
using Java.

## Important!

* Next to this documentation, please read the [general documentation](https://quedex.net/doc/api) of our WebSocket API.
* Quedex Exchange uses an innovative [schedule of session states][faq-session-schedule]. Some
  session states employ different order matching model - namely, [Auction][faq-what-is-auction].
  Please consider this when placing orders.

## Getting the API

Include in your project as a Maven dependency:

```
<dependency>
    <groupId>net.quedex</groupId>
    <artifactId>java-api</artifactId>
    <version>0.6.0</version>
</dependency>

```

## Using the API:

To use the API you need to provide a configuration - the default way to do that is via a `.properties` file. An example
may be found in [qdxConfig.properties.example][example-config] - rename this file to `qdxConfig.propertie`, place on 
your classpath and fill in the following (the rest of the properties is done):
* `net.quedex.client.api.accountId`
* `net.quedex.client.api.userPrivateKey`

You may find your account id and encrypted private key in our web application - on the trading dashboard select the 
dropdown menu with your email address in the upper right corner and go to User Profile (equivalent to visiting 
https://quedex.net/webapp/profile when logged in).

Now you are ready to start hacking:

```java
char[] pwd = ... // read private key passphrase 
Config qdxConfig = Config.fromResource(pwd); // initialise the config from qdxConfig.properties using one of the factory methods

MarketStream marketStream = new WebsocketMarketStream(qdxConfig);
UserStream userStream = new WebsocketUserStream(qdxConfig);

// register stream failure listeners
marketStream.registerStreamFailureListener(...);
userStream.registerStreamFailureListener(...)

// start streams
marketStream.start();
userStream.start();

// receive tradable instruments
marketStream.registerInstrumentsListener(instruments -> {
    
    // register and subscribe other market stream listeners
    marketStream.registerQuotesListener(...).subscribe(instruments.keySet()); // to subscribe all instruments
    marketStream.register*(...).subscribe(...);
});

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
### Batching orders

The API provides means to batch multiple order commands, and send them all in one single request.
There are two types of batches:
- regular
- time triggered

#### Regular batches

When a *regular* batch is received by the exchange engine, all the carried order commands are immediately processed,
one by one, in the creation order.

To create a regular batch, use the ```batch()``` method, and then specify order commands.

Example:

```
userStream.batch()
    .placeOrder(...)
    ...
    .send();
```

#### Time triggered batches

When a *time triggered* batch is received by the exchange engine, a new timer is registered.
Based on the timer configuration, at some point in the future, all the carried order commands are processed, one by one, in the creation order.

To create a time triggered batch, use the ```timeTriggeredBatch()``` method, and then specify order commands.

Example:

```
userStream.timeTriggeredBatch(batchId, executionStartTimestamp, executionExpirationTimestamp)
    .placeOrder(...)
    ...
    .send();
```

All the arguments passed to the ```timeTriggeredBatch()``` method, are timer configuration.

```batchId``` is an identifier of a batch, but also of a corresponding timer, created in exchange engine. It can be used to cancel or update the timer.
The specified order commands will be evaluated between ```executionStartTimestamp``` and ```executionExpirationTimestamp```. 

**Execution guarantees**

There is no guarantee that the created timer will be triggered and the order commands will be processed. The possibility is higher
when the gap between ```executionStartTimestamp``` and ```executionExpirationTimestamp``` is smaller.

To be sure that your timer will be triggered just specify wider time gap, or simply specify very high ```executionExpirationTimestamp```.

**Update time triggered batch**

Time triggered batch can be updated. 

To update a time triggered batch, use the ```updateTimeTriggeredBatch()``` method.

```
userStream.updateTimeTriggeredBatch(batchId, executionStartTimestamp, executionExpirationTimestamp)
    .placeOrder(...)
    ...
    .send();
```

When only ```executionExpirationTimestamp``` or new order commands are specified, timer is modified in place. This means that the update
does not change the order of registered timers.

When ```executionStartTimestamp``` is modified, then the timer will be placed after other existing timers with the same ```executionStartTimestamp```.

**Cancel time triggered batch**

Time triggered batch can be cancelled. 

To cancel a time triggered batch, use the ```cancelTimeTriggeredBatch()``` method.

## Contributing Guide

Default channel for submitting **questions regarding the API** is [opening new issues][new-issue].
In cases when information disclosure is&nbsp;not possible, you can contact us at support@quedex.net.

In case you need to add a feature to the API, please [submit an issue][new-issue]
containing change proposal before submitting a PR.

Pull requests containing bugfixes are very welcome!

## License

Copyright &copy; 2017 Quedex Ltd. API is released under [Apache License Version 2.0](LICENSE).

[inverse-notation-docs]: https://quedex.net/doc/inverse_notation
[faq-session-schedule]: https://quedex.net/faq#session_schedule
[faq-what-is-auction]: https://quedex.net/faq#what_is_auction
[example-config]: src/main/resources/qdxConfig.properties.example
[new-issue]: https://github.com/quedexnet/python-api/issues/new
