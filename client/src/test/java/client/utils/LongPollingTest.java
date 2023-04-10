package client.utils;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class LongPollingTest {

    public ServerUtils serverUtils;
    public Client client;
    public WebTarget webT;
    public Invocation.Builder builder;
    public StompSession stompSession;

    @BeforeEach
    public void setup() {
        client = mock(Client.class);
        webT = mock(WebTarget.class);
        builder = mock(Invocation.Builder.class);
        stompSession = mock(StompSession.class);

        when(client.property(anyString(), any())).thenReturn(client);
        when(client.target(anyString())).thenReturn(webT);
        when(webT.request(anyString())).thenReturn(builder);
        when(builder.accept(anyString())).thenReturn(builder);

        when(stompSession.isConnected()).thenReturn(true);

        serverUtils = new ServerUtils();

        ServerUtils.setClient(client);
        ServerUtils.setSERVER("localhost:8080");
        ServerUtils.setSession(stompSession);
    }

    @Test
    public void registerForBoardDeletesTest() {
        Consumer<Long> consumer = mock(Consumer.class);
        consumer.accept(42L);

        when(webT.path("api/boards/deleteUpdates")).thenReturn(webT);
        when(builder.get(Response.class)).thenReturn(Response.ok().build());

        when(webT.path("api/boards/deleteUpdates")).thenReturn(webT);
        when(builder.get(Response.class)).thenReturn(Response.ok().build());
        doNothing().when(consumer).accept(anyLong());

        assertDoesNotThrow(() -> serverUtils.registerForBoardDeletes(consumer));
    }

    @Test
    public void stopTest() {
        assertDoesNotThrow(() -> serverUtils.stop());
    }


}
