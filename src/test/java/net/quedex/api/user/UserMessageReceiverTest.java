package net.quedex.api.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.quedex.api.common.CommunicationException;
import net.quedex.api.common.ListenerException;
import net.quedex.api.common.MaintenanceException;
import net.quedex.api.common.StreamFailureListener;
import net.quedex.api.pgp.BcEncryptor;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import net.quedex.api.testcommons.Keys;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static net.quedex.api.testcommons.Utils.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UserMessageReceiverTest {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Mock private AccountStateListener accountStateListener;
    @Mock private OpenPositionListener openPositionListener;
    @Mock private OrderListener orderListener;
    @Mock private InternalTransferListener internalTransferListener;
    @Mock private StreamFailureListener streamFailureListener;
    @Mock private TimerListener timerListener;
    private BcEncryptor encryptor;

    private UserMessageReceiver userMessageReceiver;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userMessageReceiver = new UserMessageReceiver(
                BcPublicKey.fromArmored(Keys.QUEDEX_PUBLIC),
                BcPrivateKey.fromArmored(Keys.TRADER_PRIVATE)
        );
        userMessageReceiver.registerStreamFailureListener(streamFailureListener);
        encryptor = new BcEncryptor(
            BcPublicKey.fromArmored(Keys.TRADER_PUBLIC),
            BcPrivateKey.fromArmored(Keys.QUEDEX_PRIVATE)
        );
    }

    @Test
    public void testLastNonceProcessing() throws Exception {
        // given
        JsonNode lastNonceJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "last_nonce")
            .put("last_nonce", 97);

        // when
        userMessageReceiver.processMessage(encryptToTrader(lastNonceJson));

        // then
        assertThat(userMessageReceiver.getLastNonce()).isEqualTo(97);
    }

    @Test
    public void testAccountStateProcessing() throws Exception {
        // given
        JsonNode accountStateJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "account_state")
            .put("balance", "139.27152122")
            .put("free_balance", "127.17152122")
            .put("total_initial_margin", "12")
            .put("total_maintenance_margin", "8")
            .put("total_unsettled_pnl", "0")
            .put("total_locked_for_orders", "0.1")
            .put("total_pending_withdrawal", "0")
            .put("account_status", "active");

        // when
        userMessageReceiver.registerAccountStateListener(accountStateListener);
        userMessageReceiver.processMessage(encryptToTrader(accountStateJson));

        // then

        verify(accountStateListener).onAccountState(new AccountState(
            $("139.27152122"),
            $("127.17152122"),
            $(12),
            $(8),
            $(0),
            $("0.1"),
            $(0),
            AccountState.Status.ACTIVE
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenAccountStateListenerThrows() throws Exception {
        // given
        JsonNode accountStateJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "account_state")
            .put("balance", "139.27152122")
            .put("free_balance", "127.17152122")
            .put("total_initial_margin", "12")
            .put("total_maintenance_margin", "8")
            .put("total_unsettled_pnl", "0")
            .put("total_locked_for_orders", "0.1")
            .put("total_pending_withdrawal", "0")
            .put("account_status", "active");

        // when
        userMessageReceiver.registerAccountStateListener(accountStateListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(accountStateListener).onAccountState(any());
        userMessageReceiver.processMessage(encryptToTrader(accountStateJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderModificationFailedProcessing() throws Exception {
        // given
        JsonNode modificationFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_modification_failed")
            .put("client_order_id", -1)
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(modificationFailedJson));

        // then
        verify(orderListener).onOrderModificationFailed(
                new OrderModificationFailed(-1, OrderModificationFailed.Cause.NOT_FOUND)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderModificationFailedListenerThrows() throws Exception {
        // given
        JsonNode modificationFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_modification_failed")
            .put("client_order_id", -1)
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderModificationFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(modificationFailedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderPlaceFailedProcessing() throws Exception {
        // given
        JsonNode placeFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_place_failed")
            .put("client_order_id", 1470843409796L)
            .put("cause", "insufficient_funds");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(placeFailedJson));

        // then
        verify(orderListener).onOrderPlaceFailed(
                new OrderPlaceFailed(1470843409796L, OrderPlaceFailed.Cause.INSUFFICIENT_FUNDS)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderPlaceFailedListenerThrows() throws Exception {
        // given
        JsonNode placeFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_place_failed")
            .put("client_order_id", 1470843409796L)
            .put("cause", "insufficient_funds");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderPlaceFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(placeFailedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderPlacedProcessing() throws Exception {
        // given
        JsonNode placedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_placed")
            .put("client_order_id", 1470843412276L)
            .put("instrument_id", "47")
            .put("side", "sell")
            .put("limit_price", "0.01")
            .put("initial_quantity", 5)
            .put("quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(placedJson));

        // then
        verify(orderListener).onOrderPlaced(
                new OrderPlaced(1470843412276L, 47, $("0.01"), OrderSide.SELL, 5, 5)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderPlacedListenerThrows() throws Exception {
        // given
        JsonNode placedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_placed")
            .put("client_order_id", 1470843412276L)
            .put("instrument_id", "47")
            .put("side", "sell")
            .put("limit_price", "0.01")
            .put("initial_quantity", 5)
            .put("quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderPlaced(any());
        userMessageReceiver.processMessage(encryptToTrader(placedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderModifiedProcessing() throws Exception {
        // given
        JsonNode modifiedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_modified")
            .put("client_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(modifiedJson));

        // then
        verify(orderListener).onOrderModified(new OrderModified(1470843412276L));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderModifiedListenerThrows() throws Exception {
        // given
        JsonNode modifiedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_modified")
            .put("client_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderModified(any());
        userMessageReceiver.processMessage(encryptToTrader(modifiedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderCancelledProcessing() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_cancelled")
            .put("client_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        verify(orderListener).onOrderCancelled(new OrderCancelled(1470843412276L));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderCancelledListenerThrows() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_cancelled")
            .put("client_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderCancelled(any());
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderForcefullyCancelledProcessing() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_forcefully_cancelled")
            .put("client_order_id", 1470843412276L)
            .put("cause", "liquidation");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        verify(orderListener).onOrderForcefullyCancelled(
            new OrderForcefullyCancelled(1470843412276L, OrderForcefullyCancelled.Cause.LIQUIDATION)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderForcefullyCancelledListenerThrows() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_forcefully_cancelled")
            .put("client_order_id", 1470843412276L)
            .put("cause", "liquidation");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderForcefullyCancelled(any());
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderCancelFailedProcessing() throws Exception {
        // given
        JsonNode cancelFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_cancel_failed")
            .put("client_order_id", -1)
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(cancelFailedJson));

        // then
        verify(orderListener).onOrderCancelFailed(new OrderCancelFailed(-1, OrderCancelFailed.Cause.NOT_FOUND));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderCancelFailedListenerThrows() throws Exception {
        // given
        JsonNode cancelFailedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_cancel_failed")
            .put("client_order_id", -1)
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderCancelFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(cancelFailedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOrderFilledProcessing() throws Exception {
        // given
        JsonNode filledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_filled")
            .put("client_order_id", 1470844278115L)
            .put("instrument_id", "2")
            .put("order_limit_price", "12.34")
            .put("order_type", "limit")
            .put("order_side", "buy")
            .put("order_initial_quantity", 10)
            .put("leaves_order_quantity", 5)
            .put("trade_price", "10.3")
            .put("trade_quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(filledJson));

        // then
        verify(orderListener).onOrderFilled(new OrderFilled(
            1470844278115L,
            2,
            new BigDecimal("12.34"),
            OrderSide.BUY,
            10,
            5,
            new BigDecimal("10.3"),
            5
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOrderFilledListenerThrows() throws Exception {
        // given
        JsonNode filledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "order_filled")
            .put("client_order_id", 1470844278115L)
            .put("instrument_id", "2")
            .put("order_limit_price", "12.34")
            .put("order_type", "limit")
            .put("order_side", "buy")
            .put("order_initial_quantity", 10)
            .put("leaves_order_quantity", 5)
            .put("trade_price", "10.3")
            .put("trade_quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onOrderFilled(any());
        userMessageReceiver.processMessage(encryptToTrader(filledJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testLiquidationOrderPlacedProcessing() throws Exception {
        // given
        JsonNode placedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_placed")
            .put("system_order_id", 123)
            .put("instrument_id", "47")
            .put("side", "sell")
            .put("initial_quantity", 5)
            .put("quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(placedJson));

        // then
        verify(orderListener).onLiquidationOrderPlaced(
            new LiquidationOrderPlaced(123, 47, OrderSide.SELL, 5, 5)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenLiquidationOrderPlacedListenerThrows() throws Exception {
        // given
        JsonNode placedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_placed")
            .put("system_order_id", 123)
            .put("instrument_id", "47")
            .put("side", "sell")
            .put("initial_quantity", 5)
            .put("quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onLiquidationOrderPlaced(any());
        userMessageReceiver.processMessage(encryptToTrader(placedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testLiquidationOrderCancelledProcessing() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_cancelled")
            .put("system_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        verify(orderListener).onLiquidationOrderCancelled(new LiquidationOrderCancelled(1470843412276L));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenLiquidationOrderCancelledListenerThrows() throws Exception {
        // given
        JsonNode cancelledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_cancelled")
            .put("system_order_id", 1470843412276L);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onLiquidationOrderCancelled(any());
        userMessageReceiver.processMessage(encryptToTrader(cancelledJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testLiquidationOrderFilledProcessing() throws Exception {
        // given
        JsonNode filledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_filled")
            .put("system_order_id", 1470844278115L)
            .put("instrument_id", "2")
            .put("order_type", "market")
            .put("order_side", "buy")
            .put("order_initial_quantity", 10)
            .put("leaves_order_quantity", 5)
            .put("trade_price", "10.3")
            .put("trade_quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(encryptToTrader(filledJson));

        // then
        verify(orderListener).onLiquidationOrderFilled(new LiquidationOrderFilled(
            1470844278115L,
            2,
            OrderSide.BUY,
            10,
            5,
            new BigDecimal("10.3"),
            5
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenLiquidationOrderFilledListenerThrows() throws Exception {
        // given
        JsonNode filledJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "liquidation_order_filled")
            .put("system_order_id", 1470844278115L)
            .put("instrument_id", "2")
            .put("order_type", "market")
            .put("order_side", "buy")
            .put("order_initial_quantity", 10)
            .put("leaves_order_quantity", 5)
            .put("trade_price", "10.3")
            .put("trade_quantity", 5);

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onLiquidationOrderFilled(any());
        userMessageReceiver.processMessage(encryptToTrader(filledJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOpenPositionProcessing() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "open_position")
            .put("instrument_id", "47")
            .put("pnl", "0.070676")
            .put("maintenance_margin", "0.155476")
            .put("initial_margin", "0.233216")
            .put("side", "long")
            .put("quantity", 4)
            .put("average_opening_price", "0.00176678");

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        verify(openPositionListener).onOpenPosition(new OpenPosition(
                47,
                $("0.070676"),
                $("0.155476"),
                $("0.233216"),
                OpenPosition.PositionSide.LONG,
                4,
                $("0.00176678")
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOpenPositionListenerThrows() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "open_position")
            .put("instrument_id", "47")
            .put("pnl", "0.070676")
            .put("maintenance_margin", "0.155476")
            .put("initial_margin", "0.233216")
            .put("side", "long")
            .put("quantity", 4)
            .put("average_opening_price", "0.00176678");

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(openPositionListener).onOpenPosition(any());
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testInternalTransferExecuted() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_executed")
            .put("destination_account_id", "47")
            .put("amount", "0.070676");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        verify(internalTransferListener)
            .onInternalTransferExecuted(new InternalTransferExecuted(47, new BigDecimal("0.070676")));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenInternalTransferExecutedListenerThrows() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_executed")
            .put("destination_account_id", "47")
            .put("amount", "0.070676");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(internalTransferListener).onInternalTransferExecuted(any());
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testInternalTransferRejected() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_rejected")
            .put("destination_account_id", "47")
            .put("amount", "0.070676")
            .put("cause", "forbidden");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        verify(internalTransferListener).onInternalTransferRejected(new InternalTransferRejected(
            47,
            new BigDecimal("0.070676"),
            InternalTransferRejected.Cause.FORBIDDEN
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenInternalTransferRejectedListenerThrows() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_rejected")
            .put("destination_account_id", "47")
            .put("amount", "0.070676")
            .put("cause", "forbidden");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(internalTransferListener).onInternalTransferRejected(any());
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testInternalTransferReceived() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_received")
            .put("source_account_id", "47")
            .put("amount", "7");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        verify(internalTransferListener).onInternalTransferReceived(new InternalTransferReceived(47,new BigDecimal("7")));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenInternalTransferReceivedListenerThrows() throws Exception {
        // given
        JsonNode openPositionJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "internal_transfer_received")
            .put("source_account_id", "47")
            .put("amount", "7");

        // when
        userMessageReceiver.registerInternalTransferListener(internalTransferListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(internalTransferListener).onInternalTransferReceived(any());
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testAllOrdersCanceledReceived() throws Exception {
        // given
        userMessageReceiver.registerOrderListener(orderListener);
        final JsonNode msg = MAPPER.getNodeFactory().objectNode()
            .put("type", "all_orders_cancelled");

        // when
        userMessageReceiver.processMessage(encryptToTrader(msg));

        // then
        verify(orderListener).onAllOrdersCancelled();
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenAllOrdersCanceledListenerThrows() throws Exception {
        // given
        userMessageReceiver.registerOrderListener(orderListener);
        final JsonNode msg = MAPPER.getNodeFactory().objectNode()
            .put("type", "all_orders_cancelled");

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onAllOrdersCancelled();
        userMessageReceiver.processMessage(encryptToTrader(msg));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testCancelAllOrdersFailed() throws Exception {
        // given
        userMessageReceiver.registerOrderListener(orderListener);
        final JsonNode msg = MAPPER.getNodeFactory().objectNode()
            .put("type", "cancel_all_orders_failed")
            .put("cause", "session_not_active");

        // when
        userMessageReceiver.processMessage(encryptToTrader(msg));

        // then
        verify(orderListener)
            .onCancelAllOrdersFailed(new CancelAllOrdersFailed(CancelAllOrdersFailed.Cause.SESSION_NOT_ACTIVE));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenCancelAllOrdersFailedListenerThrows() throws Exception {
        // given
        userMessageReceiver.registerOrderListener(orderListener);
        final JsonNode msg = MAPPER.getNodeFactory().objectNode()
            .put("type", "cancel_all_orders_failed")
            .put("cause", "session_not_active");

        // when
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(orderListener).onCancelAllOrdersFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(msg));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testStreamFailure() throws Exception {

        // when
        userMessageReceiver.processMessage("L4rCH");

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testKeepaliveProcessing() throws Exception {
        // given
        JsonNode jsonWrapper = MAPPER.getNodeFactory().objectNode()
            .put("type", "keepalive")
            .put("timestamp", 1506958410894L);

        // when
        userMessageReceiver.processMessage(MAPPER.writeValueAsString(jsonWrapper));

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testMaintenanceProcessing() throws Exception {
        // given
        JsonNode jsonWrapper = MAPPER.getNodeFactory().objectNode()
            .put("type", "error")
            .put("error_code", "maintenance");

        // when
        userMessageReceiver.processMessage(MAPPER.writeValueAsString(jsonWrapper));

        // then
        verify(streamFailureListener).onStreamFailure(isA(MaintenanceException.class));
    }

    @Test
    public void testNotRecognisedProcessing() throws Exception {
        // given
        JsonNode jsonWrapper = MAPPER.getNodeFactory().objectNode()
            .put("type", "error")
            .put("error_code", "not_recognised");

        // when
        userMessageReceiver.processMessage(MAPPER.writeValueAsString(jsonWrapper));

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testTimeTriggeredBatchAdded() throws Exception {
        // given
        JsonNode timerAddedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_added")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerAddedJson));

        // then
        verify(timerListener).onTimerAdded(new TimerAdded(1));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchAddedListenerThrows() throws Exception {
        // given
        JsonNode timerAddedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_added")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerAdded(any());
        userMessageReceiver.processMessage(encryptToTrader(timerAddedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchRejected() throws Exception {
        // given
        JsonNode timerRejectedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_rejected")
            .put("timer_id", "1")
            .put("cause", "timer_already_exists");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerRejectedJson));

        // then
        verify(timerListener).onTimerRejected(new TimerRejected(1, TimerRejected.Cause.TIMER_ALREADY_EXISTS));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchRejectedListenerThrows() throws Exception {
        JsonNode timerRejectedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_rejected")
            .put("timer_id", "1")
            .put("cause", "timer_already_exists");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerRejected(any());
        userMessageReceiver.processMessage(encryptToTrader(timerRejectedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchExpired() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_expired")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerExpired(new TimerExpired(1));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchExpiredListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_expired")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerExpired(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchTriggered() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_triggered")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerTriggered(new TimerTriggered(1));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchTriggeredListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_triggered")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerTriggered(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchUpdated() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_updated")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerUpdated(new TimerUpdated(1));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchUpdatedListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_updated")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerUpdated(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchUpdateFailed() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_update_failed")
            .put("timer_id", "1")
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerUpdateFailed(new TimerUpdateFailed(
            1,
            TimerUpdateFailed.Cause.NOT_FOUND
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchUpdateFailedListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_update_failed")
            .put("timer_id", "1")
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerUpdateFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchCancelled() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_cancelled")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerCancelled(new TimerCancelled(1));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchCancelledListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_cancelled")
            .put("timer_id", "1");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerCancelled(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testTimeTriggeredBatchCancelFailed() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_cancel_failed")
            .put("timer_id", "1")
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        verify(timerListener).onTimerCancelFailed(new TimerCancelFailed(
            1,
            TimerCancelFailed.Cause.NOT_FOUND
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenTimeTriggeredBatchCancelFailedListenerThrows() throws Exception {
        // given
        JsonNode timerExpiredJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "timer_cancel_failed")
            .put("timer_id", "1")
            .put("cause", "not_found");

        // when
        userMessageReceiver.registerTimeTriggeredBatchListener(timerListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(timerListener).onTimerCancelFailed(any());
        userMessageReceiver.processMessage(encryptToTrader(timerExpiredJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    @Test
    public void testOpenPositionForcefullyClosedProcessing() throws Exception {
        // given
        JsonNode openPositionForcefullyClosedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "open_position_forcefully_closed")
            .put("instrument_id", "77")
            .put("side", "long")
            .put("closed_quantity", 9)
            .put("remaining_quantity", 91)
            .put("close_price", "0.10000000")
            .put("cause", "deleveraging");

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionForcefullyClosedJson));

        // then
        verify(openPositionListener).onOpenPositionForcefullyClosed(new OpenPositionForcefullyClosed(
            77,
            OpenPosition.PositionSide.LONG,
            9,
            91,
            $("0.10000000"),
            OpenPositionForcefullyClosed.Cause.DELEVERAGING
        ));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testErrorListenerIsCalledWhenOpenPositionForcefullyClosedListenerThrows() throws Exception {
        // given
        JsonNode openPositionForcefullyClosedJson = MAPPER.getNodeFactory().objectNode()
            .put("type", "open_position_forcefully_closed")
            .put("instrument_id", "77")
            .put("side", "long")
            .put("closed_quantity", 9)
            .put("remaining_quantity", 91)
            .put("close_price", "0.10000000")
            .put("cause", "deleveraging");

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        final Exception exception = new RuntimeException("Fatal");
        doThrow(exception).when(openPositionListener).onOpenPositionForcefullyClosed(any());
        userMessageReceiver.processMessage(encryptToTrader(openPositionForcefullyClosedJson));

        // then
        final ArgumentCaptor<ListenerException> captor = ArgumentCaptor.forClass(ListenerException.class);
        verify(streamFailureListener).onStreamFailure(captor.capture());
        assertThat(captor.getValue().getCause()).isEqualTo(exception);
    }

    private String encryptToTrader(final Object object) throws Exception {
        JsonNode jsonContent = MAPPER.getNodeFactory().arrayNode()
            .add(MAPPER.valueToTree(object));
        JsonNode jsonWrapper = MAPPER.getNodeFactory().objectNode()
            .put("type", "data")
            .put("data", encryptor.encrypt(MAPPER.writeValueAsString(jsonContent), true));
        return MAPPER.writeValueAsString(jsonWrapper);
    }
}

