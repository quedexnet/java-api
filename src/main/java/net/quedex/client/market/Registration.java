package net.quedex.client.market;

import java.util.Collection;

public interface Registration {

    Registration subscribe(int instrumentId);

    Registration unsubscribe(int instrumentId);

    default Registration subscribe(Collection<Integer> instrumentIds) {
        instrumentIds.forEach(this::subscribe);
        return this;
    }

    default Registration unsubscribe(Collection<Integer> instrumentIds) {
        instrumentIds.forEach(this::unsubscribe);
        return this;
    }
}
