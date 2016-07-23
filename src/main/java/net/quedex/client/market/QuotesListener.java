package net.quedex.client.market;

@FunctionalInterface
public interface QuotesListener {

    void onQuotes(Quotes quotes);
}
