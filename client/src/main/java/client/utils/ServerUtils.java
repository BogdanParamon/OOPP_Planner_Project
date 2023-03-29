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
import commons.Tag;
import commons.Task;
import commons.TaskList;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;

public class ServerUtils {

    private static String SERVER;
    private static String websocketSERVER;
    private static StompSession session;

    public static void setSERVER(String SERVER) {
        ServerUtils.SERVER = "http://" + SERVER;
        ServerUtils.websocketSERVER = "ws://" + SERVER + "/websocket";
    }

    public static void setSession(StompSession session) {
        ServerUtils.session = session;
    }

    public boolean validServer() {
        try {
            var x = ClientBuilder.newClient(new ClientConfig())
                    .target(SERVER)
                    .request(APPLICATION_JSON)
                    .accept(APPLICATION_JSON)
                    .get();
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    public Task addTask(Task task, long taskListId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/add")
                .queryParam("taskListId", taskListId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(task, APPLICATION_JSON), Task.class);
    }

    public List<Task> getAll() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Task>>() {
                });
    }

    public List<Task> getAllTasks(String sortBy) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/sorted")
                .queryParam("sortBy", sortBy)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Task>>() {
                });
    }

    public Task updateTask(Task task) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/update")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.json(task), Task.class);
    }

    public List<Board> getBoards() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<Board>>() {
                });
    }

    public Board addBoard(Board board) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/add")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(board, APPLICATION_JSON), Board.class);
    }

    public TaskList addList(TaskList list, long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/taskLists/add")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(list, APPLICATION_JSON), TaskList.class);
    }

    public TaskList updateList(TaskList taskList) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/taskLists/update")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .put(Entity.entity(taskList, APPLICATION_JSON), TaskList.class);
    }

    public List<TaskList> getListsByBoardId(long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/{boardId}/taskLists")
                .resolveTemplate("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<List<TaskList>>() {
                });
    }

    public Board getBoardById(long boardId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/" + boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Board.class);
    }

    public Map<Long, String> getBoardTitlesAndIds() {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/titles&ids")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(new GenericType<Map<Long, String>>() {
                });
    }

    public StompSession connectWebsocket() {
        var client = new StandardWebSocketClient();
        var stomp = new WebSocketStompClient(client);
        stomp.setMessageConverter(new MappingJackson2MessageConverter());
        try {
            return stomp.connect(websocketSERVER, new StompSessionHandlerAdapter() {
            }).get();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        throw new IllegalStateException();
    }

    public <T> void registerForMessages(String dest, Class<T> type, Consumer<T> consumer) {
        session.subscribe(dest, new StompFrameHandler() {
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

    public void deleteAllBoards() {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/deleteAll")
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }


    public void deleteTask(Task task) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/delete/")
                .queryParam("taskId", task.taskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Response deleteTaskList(TaskList taskList) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/taskLists/delete/")
                .queryParam("id", taskList.listId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Task getTaskById(long taskId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/tasks/" + taskId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Task.class);
    }

    public Tag addTagToBoard(long boardId, commons.Tag tag) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/addTag")
                .queryParam("boardId", boardId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .post(Entity.entity(tag, APPLICATION_JSON), Tag.class);
    }

    public void removeTag(long tagId) {
        ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/deleteTag")
                .queryParam("tagId", tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .delete();
    }

    public Tag getTagById(long tagId) {
        return ClientBuilder.newClient(new ClientConfig())
                .target(SERVER).path("api/boards/getTagById/" + tagId)
                .request(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .get(Tag.class);
    }
}