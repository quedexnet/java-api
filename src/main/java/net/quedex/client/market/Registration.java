package net.quedex.client.market;

import java.util.Collection;

public interface Registration {

    Registration subscribe(int instrumentId);

    Registration unsubscribe(int instrumentId);

    Registration subscribe(Collection<Integer> instrumentIds);

    Registration unsubscribe(Collection<Integer> instrumentIds);

    Registration unsubscribeAll();
}
