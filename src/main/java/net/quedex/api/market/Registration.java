package net.quedex.api.market;

import java.util.Collection;

/**
 * A registration of a single market stream listener. May be subscribed and unsubscribed for particular instruments.
 */
public interface Registration {

    Registration subscribe(int instrumentId);

    Registration unsubscribe(int instrumentId);

    Registration subscribe(Collection<Integer> instrumentIds);

    Registration unsubscribe(Collection<Integer> instrumentIds);

    Registration unsubscribeAll();
}
