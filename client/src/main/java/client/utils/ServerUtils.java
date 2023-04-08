/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.utils;

import commons.Board;
import commons.Subtask;
import commons.Tag;
import commons.User;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.ClientProperties;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static String SERVER;
    private static String websocketSERVER;
    private static StompSession session;
    private static Client client = ClientBuilder.newClient(new ClientConfig());
    private static StandardWebSocketClient webSocketClient = new StandardWebSocketClient();
    private static WebSocketStompClient stompClient = new WebSocketStompClient(webSocketClient);

    public static void setSERVER(String SERVER) {
        ServerUtils.SERVER = "http://" + SERVER;
        ServerUtils.websocketSERVER = "ws://" + SERVER + "/websocket";
    }

    public static void setSession(StompSession session) {
        ServerUtils.session = session;
    }

    public static void setClient(Client client) {
        ServerUtils.client = client;
    }

    public static void setWebSocketClient(StandardWebSocketClient webSocketClient) {
        ServerUtils.webSocketClient = webSocketClient;
    }

    public static void setStompClient(WebSocketStompClient stompClient) {
        ServerUtils.stompClient = stompClient;
    }

    public boolean validServer() {
        String regex = "^(http)://[a-zA-Z0-9-_.]+(:[0-9]+)?/?$";
        if (!SERVER.matches(regex)) return false;
        try {
            String resp = client.property(ClientProperties.CONNECT_TIMEOUT, 500)
                    .target(SERVER)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get(String.class);
            return resp.equals("Hello Talio!");
        } catch (Exception e) {
            return false;
        }
    }

    public Board getBoardById(long boardId) {
        return client.target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Board.class);
    }

    public Map<Long, String> getBoardTitlesAndIds() {
        return client.target(SERVER).path("api/boards/titles&ids")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Map<Long, String>>() {
                });
    }

    public Map<Long, String> getBoardTitlesAndIdsByUserId(long userId) {
        return client.target(SERVER).path("api/users/" + userId + "/boardTitles&Ids")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Map<Long, String>>() {
                });
    }

    public StompSession connectWebsocket() {
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stompClient.connect(websocketSERVER, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    public <T> StompSession.Subscription registerForMessages(String dest, Class<T> type,
                                                             Consumer<T> consumer) {
        return session.subscribe(dest, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return type;
            }

            @SuppressWarnings("unchecked")
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                consumer.accept((T) payload);
            }
        });
    }

    public void send(String dest, Object o) {
        session.send(dest, o);
    }

    public void deleteBoardById(long boardId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/delete")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public User connectToUser(String userName) {
        return client.target(SERVER).path("api/users/" + userName)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(User.class);
    }

    public Subtask addSubtask(long taskId, Subtask subtask) {
        return client.target(SERVER).path("api/subtasks/add")
                .queryParam("taskId", taskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(subtask, APPLICATION_JSON), Subtask.class);
    }

    public Tag getTagById(long tagId) {
        return client.target(SERVER).path("api/boards/getTagById/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Tag.class);
    }


    public Boolean verifyAdminPassword(String password) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/users/verifyAdmin")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(password, APPLICATION_JSON), Boolean.class);
    }

    public void disconnectWebsocket() {
        session.disconnect();
    }
}