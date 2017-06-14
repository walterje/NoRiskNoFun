package gmbh.norisknofun.game.client;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.LinkedList;

import gmbh.norisknofun.game.networkmessages.Message;
import gmbh.norisknofun.network.Session;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Tests for testing {@link ClientConnectingState}.
 */
public class ClientConnectingStateTests {

    private LinkedList<Message> messageQueue;
    private Client client;
    private Session mockSession;
    private Message mockMessage;

    @Before
    public void setUp() {

        messageQueue = new LinkedList<>();
        client = new Client(messageQueue);
        mockSession = mock(Session.class);
        mockMessage = mock(Message.class);
    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void enterDoesNothing() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        // when
        target.enter();

        // then
        assertThat(client.getSession(), is(nullValue()));
        assertThat(client.getMessageBuffer().length(), is(0));
        assertThat(client.getCurrentState(), is(instanceOf(ClientConnectingState.class)));
        assertThat((ClientConnectingState)client.getCurrentState(), is(not(sameInstance(target))));
        assertThat(messageQueue.isEmpty(), is(true));
    }

    @Test
    public void exitDoesNothing() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        // when
        target.exit();

        // then
        assertThat(client.getSession(), is(nullValue()));
        assertThat(client.getMessageBuffer().length(), is(0));
        assertThat(client.getCurrentState(), is(instanceOf(ClientConnectingState.class)));
        assertThat((ClientConnectingState)client.getCurrentState(), is(not(sameInstance(target))));
        assertThat(messageQueue.isEmpty(), is(true));
    }

    @Test
    public void handleOutboundMessageThrowsException() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("cannot handle outbound message");

        // when/then
        target.handleOutboundMessage(mockMessage);
    }

    @Test
    public void handleSessionClosedThrowsException() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("unexpected session closed event");

        // when/then
        target.handleSessionClosed(mockSession);
    }

    @Test
    public void handleDataReceivedThrowsException() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("unexpected data received event");

        // when/then
        target.handleDataReceived();
    }

    @Test
    public void handleNewSessionSetsReceivedSessionAndMakesTransition() {

        // given
        ClientConnectingState target = new ClientConnectingState(client);

        // when
        target.handleNewSession(mockSession);

        // then
        assertThat(client.getSession(), is(notNullValue()));
        assertThat(client.getSession(), is(sameInstance(mockSession)));
        assertThat(client.getCurrentState(), is(instanceOf(ClientHandshakeState.class)));
    }
}
