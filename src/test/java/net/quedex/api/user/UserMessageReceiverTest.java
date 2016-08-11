package net.quedex.api.user;

import net.quedex.api.common.CommunicationException;
import net.quedex.api.market.StreamFailureListener;
import net.quedex.api.pgp.BcPrivateKey;
import net.quedex.api.pgp.BcPublicKey;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static net.quedex.api.testcommons.Utils.$;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class UserMessageReceiverTest {

    @Mock private AccountStateListener accountStateListener;
    @Mock private OpenPositionListener openPositionListener;
    @Mock private OrderListener orderListener;
    @Mock private StreamFailureListener streamFailureListener;

    private UserMessageReceiver userMessageReceiver;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        userMessageReceiver = new UserMessageReceiver(
                BcPublicKey.fromArmored(Fixtures.PUB_KEY),
                BcPrivateKey.fromArmored(Fixtures.PRV_KEY, "qwer".toCharArray())
        );
        userMessageReceiver.registerStreamFailureListener(streamFailureListener);
    }

    @Test
    public void testLastNonceProcessing() throws Exception {

        // when
        userMessageReceiver.processMessage(Fixtures.LAST_NONCE_STR);

        // then
        assertThat(userMessageReceiver.getLastNonce()).isEqualTo(97);
    }

    @Test
    public void testAccountStateProcessing() throws Exception {

        // when
        userMessageReceiver.registerAccountStateListener(accountStateListener);
        userMessageReceiver.processMessage(Fixtures.ACCOUNT_STATE_STR);

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

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_MODIFICATION_FAILED_STR);

        // then
        verify(orderListener).onOrderModificationFailed(
                new OrderModificationFailed(-1, OrderModificationFailed.Cause.NOT_FOUND)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderPlaceFailedProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_PLACE_FAILED_STR);

        // then
        verify(orderListener).onOrderPlaceFailed(
                new OrderPlaceFailed(1470843409796L, OrderPlaceFailed.Cause.INSUFFICIENT_FUNDS)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderPlacedProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_PLACED_STR);

        // then
        verify(orderListener).onOrderPlaced(
                new OrderPlaced(1470843412276L, 47, $("0.01"), OrderSide.SELL, 5, 5)
        );
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderModifiedProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_MODIFIED_STR);

        // then
        verify(orderListener).onOrderModified(new OrderModified(1470843412276L));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderCanceledProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_CANCELED_STR);

        // then
        verify(orderListener).onOrderCanceled(new OrderCanceled(1470843412276L));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderCancelFailedProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_CANCEL_FAILED_STR);

        // then
        verify(orderListener).onOrderCancelFailed(new OrderCancelFailed(-1, OrderCancelFailed.Cause.NOT_FOUND));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOrderFilledProcessing() throws Exception {

        // when
        userMessageReceiver.registerOrderListener(orderListener);
        userMessageReceiver.processMessage(Fixtures.ORDER_FILLED_STR);

        // then
        verify(orderListener).onOrderFilled(new OrderFilled(1470844278115L, 5));
        verify(streamFailureListener, never()).onStreamFailure(any());
    }

    @Test
    public void testOpenPositionProcessing() throws Exception {

        // when
        userMessageReceiver.registerOpenPositionListener(openPositionListener);
        userMessageReceiver.processMessage(Fixtures.OPEN_POSITION_STR);

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
    public void testStreamFailure() throws Exception {

        // when
        userMessageReceiver.processMessage("L4rCH");

        // then
        verify(streamFailureListener).onStreamFailure(isA(CommunicationException.class));
    }

    @Test
    public void testKeepaliveProcessing() throws Exception {

        // when
        userMessageReceiver.processMessage("keepalive");

        // then
        verify(streamFailureListener, never()).onStreamFailure(any());
    }
}