package net.quedex.api.user;

import com.google.common.collect.ImmutableList;
import net.quedex.api.pgp.BcEncryptor;
import org.java_websocket.client.WebSocketClient;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class UserMessageSenderTest {
    private @Mock WebSocketClient wsClient;
    private @Mock BcEncryptor encryptor;

    private UserMessageSender sender;

    @BeforeMethod
    public void setUp() throws Exception {
        initMocks(this);
        sender = new UserMessageSender(wsClient, 1234, 5, encryptor);
        when(encryptor.encrypt(any(), anyBoolean()))
            .thenAnswer(invocation -> invocation.getArgumentAt(0, String.class));
    }

    @Test
    public void sendsOrderPlaceCommand() throws Exception {
        // given
        final LimitOrderSpec spec = new LimitOrderSpec(
            888L,
            512,
            OrderSide.BUY,
            1500,
            BigDecimal.valueOf(1L, 8)
        );

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
             "\"instrument_id\":512," +
             "\"side\":\"BUY\"," +
             "\"quantity\":1500," +
             "\"limit_price\":1E-8," +
             "\"post_only\":false," +
             "\"order_type\":\"LIMIT\"," +
             "\"type\":\"place_order\"," +
             "\"account_id\":1234," +
             "\"nonce\":1," +
             "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsPostOnlyOrderPlaceCommand() throws Exception {
        // given
        final LimitOrderSpec spec = new LimitOrderSpec(
            888L,
            512,
            OrderSide.BUY,
            1500,
            BigDecimal.valueOf(1L, 8),
            true
        );

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
             "\"instrument_id\":512," +
             "\"side\":\"BUY\"," +
             "\"quantity\":1500," +
             "\"limit_price\":1E-8," +
             "\"post_only\":true," +
             "\"order_type\":\"LIMIT\"," +
             "\"type\":\"place_order\"," +
             "\"account_id\":1234," +
             "\"nonce\":1," +
             "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsOrderModifyCommandWithPriceChange() throws Exception {
        // given
        final OrderModificationSpec spec = new OrderModificationSpec(888L, BigDecimal.valueOf(1400L));

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
             "\"new_quantity\":null," +
             "\"new_limit_price\":1.4E+3," +
             "\"post_only\":false," +
             "\"type\":\"modify_order\"," +
             "\"account_id\":1234," +
             "\"nonce\":1," +
             "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsPostOnlyOrderModifyCommandWithPriceChange() throws Exception {
        // given
        final OrderModificationSpec spec = new OrderModificationSpec(888L, BigDecimal.valueOf(1400L), true);

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
             "\"new_quantity\":null," +
             "\"new_limit_price\":1.4E+3," +
             "\"post_only\":true," +
             "\"type\":\"modify_order\"," +
             "\"account_id\":1234," +
             "\"nonce\":1," +
             "\"nonce_group\":5}"
        );
    }


    @Test
    public void sendsOrderModifyCommandWithQuantityChange() throws Exception {
        // given
        final OrderModificationSpec spec = new OrderModificationSpec(888L, 100);

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
                "\"new_quantity\":100," +
                "\"new_limit_price\":null," +
                "\"post_only\":false," +
                "\"type\":\"modify_order\"," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsOrderModifyCommandWithBothPriceAndQuantityChange() throws Exception {
        // given
        final OrderModificationSpec spec = new OrderModificationSpec(888L, 100, BigDecimal.valueOf(1400));

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
                "\"new_quantity\":100," +
                "\"new_limit_price\":1.4E+3," +
                "\"post_only\":false," +
                "\"type\":\"modify_order\"," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsPostOnlyOrderModifyCommandWithBothPriceAndQuantityChange() throws Exception {
        // given
        final OrderModificationSpec spec = new OrderModificationSpec(888L, 100, BigDecimal.valueOf(1400), true);

        // when
        sender.sendOrderSpec(spec);

        // then
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(wsClient, timeout(500)).send(captor.capture());
        assertThat(captor.getValue()).isEqualTo(
            "{\"client_order_id\":888," +
                "\"new_quantity\":100," +
                "\"new_limit_price\":1.4E+3," +
                "\"post_only\":true," +
                "\"type\":\"modify_order\"," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsInternalTransferMessage() throws Exception {
        // given
        InternalTransfer message = new InternalTransfer(5678, new BigDecimal("3.1416"));

        // when
        sender.sendInternalTransfer(message);

        // then
        verify(wsClient, timeout(100)).send(
            "{\"destination_account_id\":5678," +
                "\"amount\":3.1416," +
                "\"type\":\"internal_transfer\"," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsCancelAllMessage() throws Exception {
        // when
        sender.sendOrderSpec(CancelAllOrdersSpec.INSTANCE);

        verify(wsClient, timeout(100)).send(
            "{\"type\":\"cancel_all_orders\"," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5}"
        );
    }

    @Test
    public void sendsTimeTriggeredBatchMessage() throws Exception {
        // given
        final long timerId = 1L;
        final long executionStartTimestamp = 200L;
        final long executionExpirationTimestamp = 300L;
        final List<OrderSpec> batch = ImmutableList.of(
            CancelAllOrdersSpec.INSTANCE,
            new LimitOrderSpec(
                888L,
                512,
                OrderSide.BUY,
                1500,
                BigDecimal.valueOf(1L, 8)
            )
        );

        // when
        sender.sendTimeTriggeredBatch(timerId, executionStartTimestamp, executionExpirationTimestamp, batch);

        // then
        verify(wsClient, timeout(100)).send(
            "{" +
                "\"type\":\"add_timer\"," +
                "\"timer_id\":1," +
                "\"execution_start_timestamp\":200," +
                "\"execution_expiration_timestamp\":300," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5," +
                "\"command\":{" +
                    "\"type\":\"batch\"," +
                    "\"account_id\":1234," +
                    "\"batch\":[" +
                        "{" +
                            "\"type\":\"cancel_all_orders\"," +
                            "\"account_id\":1234," +
                            "\"nonce\":2," +
                            "\"nonce_group\":5" +
                        "}," +
                        "{" +
                            "\"client_order_id\":888," +
                            "\"instrument_id\":512," +
                            "\"side\":\"BUY\"," +
                            "\"quantity\":1500," +
                            "\"limit_price\":1E-8," +
                            "\"post_only\":false," +
                            "\"order_type\":\"LIMIT\"," +
                            "\"type\":\"place_order\"," +
                            "\"account_id\":1234," +
                            "\"nonce\":3," +
                            "\"nonce_group\":5" +
                        "}" +
                    "]" +
                "}" +
            "}"
        );
    }

    @Test
    public void sendTimeTriggeredBatchUpdate() throws Exception {
        // given
        final long timerId = 1L;
        final long executionStartTimestamp = 200L;
        final long executionExpirationTimestamp = 300L;
        final List<OrderSpec> batch = ImmutableList.of(
            CancelAllOrdersSpec.INSTANCE,
            new LimitOrderSpec(
                888L,
                512,
                OrderSide.BUY,
                1500,
                BigDecimal.valueOf(1L, 8)
            )
        );

        // when
        sender.sendTimeTriggeredBatchUpdate(timerId, executionStartTimestamp, executionExpirationTimestamp, batch);

        // then
        verify(wsClient, timeout(100)).send(
            "{" +
                "\"type\":\"update_timer\"," +
                "\"timer_id\":1," +
                "\"new_execution_start_timestamp\":200," +
                "\"new_execution_expiration_timestamp\":300," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5," +
                "\"new_command\":{" +
                    "\"type\":\"batch\"," +
                    "\"account_id\":1234," +
                    "\"batch\":[" +
                        "{" +
                            "\"type\":\"cancel_all_orders\"," +
                            "\"account_id\":1234," +
                            "\"nonce\":2," +
                            "\"nonce_group\":5" +
                        "}," +
                        "{" +
                            "\"client_order_id\":888," +
                            "\"instrument_id\":512," +
                            "\"side\":\"BUY\"," +
                            "\"quantity\":1500," +
                            "\"limit_price\":1E-8," +
                            "\"post_only\":false," +
                            "\"order_type\":\"LIMIT\"," +
                            "\"type\":\"place_order\"," +
                            "\"account_id\":1234," +
                            "\"nonce\":3," +
                            "\"nonce_group\":5" +
                        "}" +
                    "]" +
                "}" +
            "}"
        );
    }

    @Test
    public void sendTimeTriggeredBatchUpdateWithExecutionStartMovedOnly() throws Exception {
        // given
        final long timerId = 1L;
        final long executionStartTimestamp = 200L;

        // when
        sender.sendTimeTriggeredBatchUpdate(timerId, executionStartTimestamp, null, null);

        // then
        verify(wsClient, timeout(100)).send(
            "{" +
                "\"type\":\"update_timer\"," +
                "\"timer_id\":1," +
                "\"new_execution_start_timestamp\":200," +
                "\"new_execution_expiration_timestamp\":null," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5," +
                "\"new_command\":null" +
                "}"
        );
    }

    @Test
    public void sendTimeTriggeredBatchUpdateWithUpdatedCommandOnly() throws Exception {
        // given
        final long timerId = 1L;
        final List<OrderSpec> batch = ImmutableList.of(
            CancelAllOrdersSpec.INSTANCE,
            new LimitOrderSpec(
                888L,
                512,
                OrderSide.BUY,
                1500,
                BigDecimal.valueOf(1L, 8)
            )
        );

        // when
        sender.sendTimeTriggeredBatchUpdate(timerId, null, null, batch);

        // then
        verify(wsClient, timeout(100)).send(
            "{" +
                "\"type\":\"update_timer\"," +
                "\"timer_id\":1," +
                "\"new_execution_start_timestamp\":null," +
                "\"new_execution_expiration_timestamp\":null," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5," +
                "\"new_command\":{" +
                "\"type\":\"batch\"," +
                "\"account_id\":1234," +
                "\"batch\":[" +
                "{" +
                "\"type\":\"cancel_all_orders\"," +
                "\"account_id\":1234," +
                "\"nonce\":2," +
                "\"nonce_group\":5" +
                "}," +
                "{" +
                "\"client_order_id\":888," +
                "\"instrument_id\":512," +
                "\"side\":\"BUY\"," +
                "\"quantity\":1500," +
                "\"limit_price\":1E-8," +
                "\"post_only\":false," +
                "\"order_type\":\"LIMIT\"," +
                "\"type\":\"place_order\"," +
                "\"account_id\":1234," +
                "\"nonce\":3," +
                "\"nonce_group\":5" +
                "}" +
                "]" +
                "}" +
                "}"
        );
    }

    @Test
    public void sendTimeTriggeredBatchCancellation() throws Exception {
        // given
        final long timerId = 1L;

        // when
        sender.sendTimeTriggeredBatchCancellation(timerId);

        // then
        verify(wsClient, timeout(100)).send(
            "{" +
                "\"type\":\"cancel_timer\"," +
                "\"timer_id\":1," +
                "\"account_id\":1234," +
                "\"nonce\":1," +
                "\"nonce_group\":5" +
            "}"
        );
    }
}