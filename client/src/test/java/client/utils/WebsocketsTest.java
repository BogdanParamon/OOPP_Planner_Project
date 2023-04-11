package client.utils;

import commons.Packet;
import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class WebsocketsTest {

    public ServerUtils serverUtils;
    public StandardWebSocketClient webSocketClient;
    public WebSocketStompClient stompClient;
    public StompSession stompSession;

    @BeforeEach
    public void setup() throws ExecutionException, InterruptedException {
        webSocketClient = mock(StandardWebSocketClient.class);
        stompClient = mock(WebSocketStompClient.class);
        stompSession = mock(StompSession.class);
        var listenableFuture = mock(ListenableFuture.class);

        when(stompClient.connect(eq("ws://localhost:8080/websocket"),
                any(StompSessionHandlerAdapter.class))).
                thenReturn(listenableFuture);
        when(listenableFuture.get()).thenReturn(stompSession);

        serverUtils = new ServerUtils();
        ServerUtils.setSERVER("localhost:8080");
        ServerUtils.setWebSocketClient(webSocketClient);
        ServerUtils.setStompClient(stompClient);
        ServerUtils.setSession(stompSession);
    }


    @Test
    public void testConnect() {
        assertEquals(stompSession, serverUtils.connectWebsocket());
    }

    @Test
    public void registerForNewBoardsTest() {
        Consumer<Packet> consumer = mock(Consumer.class);
        consumer.accept(new Packet());

        when(stompSession.subscribe(eq("/topic/boards/add/42"), any(StompFrameHandler.class))).
                thenReturn(mock(StompSession.Subscription.class));

        when(serverUtils.registerForMessages(
                "/topic/boards/add",
                Packet.class,
                consumer)).thenReturn(mock(StompSession.Subscription.class));

        assertNotNull(serverUtils.registerForMessages(
                "/topic/boards/add/42",
                Packet.class,
                consumer));
    }

    @Test
    public void sendTest() {
        Board board = new Board("Board 42");
        when(stompSession.send("/app/boards/add", board)).
                thenReturn(mock(StompSession.Receiptable.class));
        assertDoesNotThrow(() -> serverUtils.send("/app/boards/add/42", board));
    }

    @Test
    public void disconnectTest() {
        doNothing().when(stompSession).disconnect();
        assertDoesNotThrow(() -> serverUtils.disconnectWebsocket());
    }
}
