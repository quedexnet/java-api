package net.quedex.api.user;

import com.google.common.collect.ImmutableList;
import net.quedex.api.common.Config;
import net.quedex.api.common.StreamFailureListener;
import net.quedex.api.testcommons.Utils;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.Timeout;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static net.quedex.api.testcommons.Utils.$;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

public class WebsocketUserStreamIT {

    private static final boolean TESTS_ENABLED = false; // enable to run
    private static final int FUTURES_INSTRUMENT_ID = 2894;

    @Mock private AccountStateListener accountStateListener;
    @Mock private OpenPositionListener openPositionListener;
    private CollectingOrderListener orderListener;
    @Mock private StreamFailureListener streamFailureListener;
    @Mock private TimerListener timerListener;

    private UserStream userStream;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        orderListener = spy(new CollectingOrderListener());

        Config config = Config.fromResource(Utils.getKeyPassphraseFromProps());
        userStream = new WebsocketUserStream(config);

        userStream.registerStreamFailureListener(streamFailureListener);
        userStream.start();
        userStream.registerAccountStateListener(accountStateListener);
        userStream.registerOpenPositionListener(openPositionListener);
        userStream.registerOrderListener(orderListener);
        userStream.registerTimerListener(timerListener);

        // when
        userStream.subscribeListeners();

        // then
        verify(accountStateListener, timeout(1000)).onAccountState(any());

        cancelAllPendingOrders();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        userStream.stop();
    }

    @Test(enabled = TESTS_ENABLED)
    public void testPlacingModifyingAndCancelingSingleOrder() throws Exception {
        // this test will work correctly with 100 BTC balance

        // given
        long clOrId = System.currentTimeMillis();
        BigDecimal price = $("5000");
        OrderSide side = OrderSide.SELL;
        int qty = 5;

        // when
        userStream.placeOrder(new LimitOrderSpec(
                clOrId,
                FUTURES_INSTRUMENT_ID,
                side,
                qty,
                price
        ));

        // then
        verify(orderListener, timeout(1000)).onOrderPlaced(new OrderPlaced(
                clOrId,
                FUTURES_INSTRUMENT_ID,
                price,
                side,
                qty,
                qty
        ));

        // and then
        // when
        int newQty = 4;
        BigDecimal newPrice = $("0.05");
        userStream.modifyOrder(new OrderModificationSpec(clOrId, newQty, newPrice));

        // then
        verify(orderListener, timeout(1000)).onOrderModified(new OrderModified(clOrId));

        // and then
        // when
        userStream.cancelOrder(new OrderCancelSpec(clOrId));

        // then
        verify(orderListener, timeout(1000)).onOrderCancelled(new OrderCancelled(clOrId));
        verify(accountStateListener, new Timeout(1000, atLeastOnce())).onAccountState(any());

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test(enabled = TESTS_ENABLED)
    public void testFailedOrderPlace() throws Exception {
        // this test will work correctly with 100 BTC balance

        // given
        long clOrId = System.currentTimeMillis();

        // when
        userStream.placeOrder(new LimitOrderSpec(
                clOrId,
                FUTURES_INSTRUMENT_ID,
                OrderSide.SELL,
                1000,
                $(10000)
        ));

        // then
        verify(orderListener, timeout(1000)).onOrderPlaceFailed(
                new OrderPlaceFailed(clOrId, OrderPlaceFailed.Cause.INSUFFICIENT_FUNDS)
        );

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test(enabled = TESTS_ENABLED)
    public void testFailedOrderModify() throws Exception {

        // when
        userStream.modifyOrder(new OrderModificationSpec(-1, 3));

        // then
        verify(orderListener, timeout(1000)).onOrderModificationFailed(
                new OrderModificationFailed(-1, OrderModificationFailed.Cause.NOT_FOUND)
        );

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test(enabled = TESTS_ENABLED)
    public void testFailedOrderCancel() throws Exception {

        // when
        userStream.cancelOrder(new OrderCancelSpec(-1));

        // then
        verify(orderListener, timeout(1000)).onOrderCancelFailed(
                new OrderCancelFailed(-1, OrderCancelFailed.Cause.NOT_FOUND)
        );

        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test(enabled = TESTS_ENABLED)
    public void testPlacingModifyingAndCancelingBatchOrders() throws Exception {

        // given
        long clOrId1 = System.currentTimeMillis();
        long clOrId2 = clOrId1 + 1;
        long clOrId3 = clOrId1 + 2;
        BigDecimal price1 = $("0.01");
        BigDecimal price2 = $("0.02");
        BigDecimal price3 = $("0.03");
        OrderSide side = OrderSide.SELL;
        int qty1 = 5;
        int qty2 = 6;
        int qty3 = 7;

        // when
        userStream.batch()
                .placeOrder(new LimitOrderSpec(clOrId1, FUTURES_INSTRUMENT_ID, side, qty1, price1))
                .placeOrders(ImmutableList.of(
                        new LimitOrderSpec(clOrId2, FUTURES_INSTRUMENT_ID, side, qty2, price2),
                        new LimitOrderSpec(clOrId3, FUTURES_INSTRUMENT_ID, side, qty3, price3)
                ))
                .modifyOrder(new OrderModificationSpec(clOrId1, qty1 + 1))
                .modifyOrders(ImmutableList.of(
                        new OrderModificationSpec(clOrId2, qty2 + 1),
                        new OrderModificationSpec(clOrId3, qty3 + 1))
                )
                .cancelOrder(new OrderCancelSpec(clOrId1))
                .cancelOrders(ImmutableList.of(new OrderCancelSpec(clOrId2), new OrderCancelSpec(clOrId3)))
                .send();

        // then
        verify(orderListener, timeout(1000))
                .onOrderPlaced(new OrderPlaced(clOrId1, FUTURES_INSTRUMENT_ID, price1, side, qty1, qty1));
        verify(orderListener, timeout(1000))
                .onOrderPlaced(new OrderPlaced(clOrId2, FUTURES_INSTRUMENT_ID, price2, side, qty2, qty2));
        verify(orderListener, timeout(1000))
                .onOrderPlaced(new OrderPlaced(clOrId3, FUTURES_INSTRUMENT_ID, price3, side, qty3, qty3));
        verify(orderListener, timeout(1000)).onOrderModified(new OrderModified(clOrId1));
        verify(orderListener, timeout(1000)).onOrderModified(new OrderModified(clOrId2));
        verify(orderListener, timeout(1000)).onOrderModified(new OrderModified(clOrId3));
        verify(orderListener, timeout(1000)).onOrderCancelled(new OrderCancelled(clOrId1));
        verify(orderListener, timeout(1000)).onOrderCancelled(new OrderCancelled(clOrId2));
        verify(orderListener, timeout(1000)).onOrderCancelled(new OrderCancelled(clOrId3));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testSelfTrade() throws Exception {
        // this may not happen if volatility is too high and the orders trigger an auction

        // given
        long clOrId = System.currentTimeMillis();
        BigDecimal price = $("0.01");
        OrderSide side = OrderSide.SELL;
        int qty = 5;

        // when
        userStream.placeOrder(new LimitOrderSpec(
                clOrId,
                FUTURES_INSTRUMENT_ID,
                side,
                qty,
                price
        ));
        userStream.placeOrder(new LimitOrderSpec(
                clOrId + 1,
                FUTURES_INSTRUMENT_ID,
                OrderSide.BUY,
                qty,
                price
        ));

         // then
        verify(orderListener, timeout(1000)).onOrderFilled(any());
    }

    @Test(enabled = TESTS_ENABLED)
    public void testAddingTimeTriggeredBatchIteratively() throws Exception {

        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);

        long clientOrderId = currentTime;

        // when
        userStream.timeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp)
            .cancelAllOrders()
            .placeOrder(new LimitOrderSpec(clientOrderId, FUTURES_INSTRUMENT_ID, OrderSide.SELL, 5, $("5000")))
            .send();

        // then
        verify(timerListener, timeout(1000))
            .onTimerAdded(new TimerAdded(timerId));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testAddingTimeTriggeredBatchImmediately() throws Exception {
        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);

        long clientOrderId = currentTime;

        // when
        userStream.timeTriggeredBatch(
            timerId,
            executionStartTimestamp,
            executionExpirationTimestamp,
            ImmutableList.of(
                CancelAllOrdersSpec.INSTANCE,
                new LimitOrderSpec(clientOrderId, FUTURES_INSTRUMENT_ID, OrderSide.SELL, 5, $("5000"))
            )
        );

        // then
        verify(timerListener, timeout(1000))
            .onTimerAdded(new TimerAdded(timerId));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testUpdateTimeTriggeredBatchIteratively() throws Exception {

        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);

        long clientOrderId = currentTime;

        userStream.timeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp)
            .cancelAllOrders()
            .send();

        // when
        userStream.updateTimeTriggeredBatch(timerId, null, executionExpirationTimestamp + 1)
            .cancelAllOrders()
            .placeOrder(new LimitOrderSpec(clientOrderId + 1, FUTURES_INSTRUMENT_ID, OrderSide.SELL, 5, $("0.01")))
            .send();

        // then
        verify(timerListener, timeout(1000))
            .onTimerUpdated(new TimerUpdated(timerId));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testUpdateTimeTriggeredBatchImmediately() throws Exception {
        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);

        long clientOrderId = currentTime;

        userStream.timeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp)
            .cancelAllOrders()
            .send();

        // when
        userStream.updateTimeTriggeredBatch(
            timerId,
            null,
            executionExpirationTimestamp + 1,
            ImmutableList.of(
                CancelAllOrdersSpec.INSTANCE,
                new LimitOrderSpec(clientOrderId, FUTURES_INSTRUMENT_ID, OrderSide.SELL, 5, $("0.01"))
            )
        );

        // then
        verify(timerListener, timeout(1000))
            .onTimerUpdated(new TimerUpdated(timerId));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testUpdateTimeTriggeredBatchFails() throws Exception {

        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = 1L;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);

        // when
        userStream.updateTimeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp)
            .cancelAllOrders()
            .send();

        // then
        verify(timerListener, timeout(1000))
            .onTimerUpdateFailed(
                new TimerUpdateFailed(
                    timerId,
                    TimerUpdateFailed.Cause.NOT_FOUND
                )
            );
    }

    @Test(enabled = TESTS_ENABLED)
    public void testCancelTimeTriggeredBatch() throws Exception {

        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;
        final long executionStartTimestamp = currentTime + (5 * 60 * 1000);
        final long executionExpirationTimestamp = currentTime + (10 * 60 * 1000);
        
        userStream.timeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp)
            .cancelAllOrders()
            .send();

        // when
        userStream.cancelTimeTriggeredBatch(timerId);

        // then
        verify(timerListener, timeout(1000))
            .onTimerCancelled(new TimerCancelled(timerId));
    }

    @Test(enabled = TESTS_ENABLED)
    public void testCancelTimeTriggeredBatchFails() throws Exception {

        // given
        final long currentTime = System.currentTimeMillis();

        final long timerId = currentTime;

        // when
        userStream.cancelTimeTriggeredBatch(timerId);

        // then
        verify(timerListener, timeout(1000))
            .onTimerCancelFailed(
                new TimerCancelFailed(
                    timerId,
                    TimerCancelFailed.Cause.NOT_FOUND
                )
            );
    }

    private void cancelAllPendingOrders() throws Exception {
        Thread.sleep(1000); // let the orders arrive
        int numPending = orderListener.ordersToCancel.size();
        if (numPending > 0) {
            userStream.batch().cancelOrders(orderListener.ordersToCancel).send();
            orderListener.ordersToCancel.forEach(
                    cancelSpec -> verify(orderListener, timeout(1000))
                            .onOrderCancelled(new OrderCancelled(cancelSpec.getClientOrderId()))
            );
            verify(accountStateListener, new Timeout(1000, atLeastOnce())).onAccountState(any());
        }
    }

    private static class CollectingOrderListener implements OrderListener {

        private final List<OrderCancelSpec> ordersToCancel = new ArrayList<>();

        @Override
        public void onOrderPlaced(OrderPlaced orderPlaced) {
            ordersToCancel.add(new OrderCancelSpec(orderPlaced.getClientOrderId()));
        }

        @Override
        public void onOrderPlaceFailed(OrderPlaceFailed orderPlaceFailed) {}
        @Override
        public void onOrderCancelled(OrderCancelled orderCancelled) {}
        @Override
        public void onOrderCancelFailed(OrderCancelFailed orderCancelFailed) {}
        @Override
        public void onAllOrdersCancelled() {}
        @Override
        public void onCancelAllOrdersFailed(final CancelAllOrdersFailed cancelAllOrdersFailed) {}
        @Override
        public void onOrderModified(OrderModified orderModified) {}
        @Override
        public void onOrderModificationFailed(OrderModificationFailed orderModificationFailed) {}
        @Override
        public void onOrderFilled(OrderFilled orderFilled) {}
        @Override
        public void onOrderForcefullyCancelled(OrderForcefullyCancelled orderForcefullyCancelled) {}
        @Override
        public void onLiquidationOrderPlaced(LiquidationOrderPlaced liquidationOrderPlaced) {}
        @Override
        public void onLiquidationOrderCancelled(LiquidationOrderCancelled liquidationOrderCancelled) {}
        @Override
        public void onLiquidationOrderFilled(LiquidationOrderFilled liquidationOrderFilled) {}
    }
}