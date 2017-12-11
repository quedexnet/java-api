package net.quedex.api.user;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.quedex.api.common.CommunicationException;
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
    public void callsErrorCallbackOnListenerError() throws Exception {
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
        final IllegalStateException exception = new IllegalStateException("error");
        doThrow(exception).when(openPositionListener).onOpenPosition(any());

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        userMessageReceiver.processMessage(encryptToTrader(openPositionJson));

        // then
        final ArgumentCaptor<Exception> captor = ArgumentCaptor.forClass(Exception.class);
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