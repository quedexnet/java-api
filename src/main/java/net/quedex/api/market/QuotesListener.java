package net.quedex.api.market;

@FunctionalInterface
public interface QuotesListener
{
    void onQuotes(Quotes quotes);
}
