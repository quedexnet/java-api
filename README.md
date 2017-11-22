# Quedex Official Java API

> The best way to communicate with [Quedex Bitcoin Derivatives Exchange](https://quedex.net)
using Java.

## Important!

* Quedex Exchange uses an innovative [schedule of session states][faq-session-schedule]. Some
  session states employ different order matching model - namely, [Auction][faq-what-is-auction].
  Please consider this when placing orders.

## Getting the API

Include in your project as a Maven dependency:

```
<dependency>
    <groupId>net.quedex</groupId>
    <artifactId>java-api</artifactId>
    <version>0.5.0</version>
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
