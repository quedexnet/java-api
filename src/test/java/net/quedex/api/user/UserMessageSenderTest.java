package net.quedex.api.user;

import net.quedex.api.pgp.BcEncryptor;
import org.java_websocket.client.WebSocketClient;
import org.mockito.Mock;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigDecimal;

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
}