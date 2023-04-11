package client.utils;

import commons.Board;
import commons.Subtask;
import commons.Tag;
import commons.User;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RestAPIsTest {

    public ServerUtils serverUtils;
    public Client client;
    public WebTarget webT;
    public Invocation.Builder builder;

    @BeforeEach
    public void setup() {
        serverUtils = new ServerUtils();
        client = mock(Client.class);
        webT = mock(WebTarget.class);
        builder = mock(Invocation.Builder.class);


        when(client.property(anyString(), any())).thenReturn(client);
        when(client.target(anyString())).thenReturn(webT);
        when(webT.request(APPLICATION_JSON)).thenReturn(builder);
        when(builder.accept(APPLICATION_JSON)).thenReturn(builder);


        ServerUtils.setClient(client);
        ServerUtils.setSERVER("localhost:8080");
    }

    @Test
    public void testValidServer() {
        ServerUtils.setSERVER("localhost:8080");
        when(builder.get(String.class)).thenReturn("Hello Talio!");
        assertNull(serverUtils.validServer());
    }

    @Test
    public void testInvalidServer() {
        ServerUtils.setSERVER("notavalidserver");
        assertEquals(serverUtils.validServer(), "Server is not running or is invalid");
    }

    @Test
    public void getBoardByIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        when(webT.path("api/boards/42")).thenReturn(webT);
        when(builder.get(Board.class)).thenReturn(board);

        assertEquals(board, serverUtils.getBoardById(42));
    }

    @Test
    public void getInvalidBoardTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        Board expected = new Board("Board 43");
        expected.boardId = 43;

        when(webT.path("api/boards/43")).thenReturn(webT);
        when(builder.get(Board.class)).thenReturn(expected);

        assertNotEquals(board, serverUtils.getBoardById(43));
    }

    @Test
    public void getBoardTitlesAndIdsByUserIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        Board board2 = new Board("Board 43");
        board2.boardId = 43;

        Map<Long, String> map = new HashMap<>();
        map.put(42L, "Board 42");
        map.put(43L, "Board 43");

        when(webT.path("api/users/42/boardTitles&Ids")).thenReturn(webT);
        when(builder.get(new GenericType<Map<Long, String>>() {
        })).thenReturn(map);

        assertEquals(map, serverUtils.getBoardTitlesAndIdsByUserId(42));
    }

    @Test
    public void getInvalidBoardTitlesAndIdsByUserIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        Board board2 = new Board("Board 43");
        board2.boardId = 43;

        Map<Long, String> map = new HashMap<>();
        map.put(42L, "Board 42");

        Map<Long, String> map2 = new HashMap<>();
        map2.put(43L, "Board 43");

        when(webT.path("api/users/43/boardTitles&Ids")).thenReturn(webT);
        when(builder.get(new GenericType<Map<Long, String>>() {
        })).thenReturn(map2);
        assertNotEquals(map, serverUtils.getBoardTitlesAndIdsByUserId(43));
    }

    @Test
    public void getSubtaskByIdTest() {
        Subtask subtask = new Subtask("Subtask 42");
        Subtask subtask2 = new Subtask("Subtask 42");
        subtask2.subTaskId = 42;

        when(webT.path("api/subtasks/add")).thenReturn(webT);
        when(webT.queryParam("taskId", 42L)).thenReturn(webT);
        when(builder.post(Entity.entity(subtask, APPLICATION_JSON), Subtask.class))
                .thenReturn(subtask2);

        assertEquals(subtask2, serverUtils.addSubtask(42L, subtask));
    }

    @Test
    public void getTagByIdTest() {
        Tag tag = new Tag("Tag 42", "white");
        tag.tagId = 42;

        when(webT.path("api/boards/getTagById/" + 42)).thenReturn(webT);
        when(builder.get(Tag.class)).thenReturn(tag);

        assertEquals(tag, serverUtils.getTagById(42));
    }

    @Test
    public void connectToUserTest() {
        User user = new User("user42");
        user.userId = 42;

        when(webT.path("api/users/user42")).thenReturn(webT);
        when(builder.get(User.class)).thenReturn(user);

        assertEquals(user, serverUtils.connectToUser("user42"));
    }

    @Test
    public void deleteBoardByIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        when(webT.path("api/boards/delete")).thenReturn(webT);
        when(webT.queryParam("boardId", 42L)).thenReturn(webT);
        when(builder.delete()).thenReturn(Response.ok().build());
        assertDoesNotThrow(() -> serverUtils.deleteBoardById(42L));
    }

    @Test
    public void verifyAdminPasswordTest() {
        when(webT.path("api/users/verifyAdmin")).thenReturn(webT);
        when(builder.post(Entity.entity("admin", APPLICATION_JSON), Boolean.class)).
                thenReturn(true);
        assertTrue(serverUtils.verifyAdminPassword("admin"));
    }

    @Test
    public void verifyAdminPasswordTestInvalid() {
        when(webT.path("api/users/verifyAdmin")).thenReturn(webT);
        when(builder.post(Entity.entity("admin42", APPLICATION_JSON), Boolean.class)).
                thenReturn(false);
        assertFalse(serverUtils.verifyAdminPassword("admin42"));
    }

    @Test
    public void getBoardTitlesAndIdsTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;

        Board board2 = new Board("Board 43");
        board2.boardId = 43;

        Map<Long, String> map = new HashMap<>();
        map.put(42L, "Board 42");
        map.put(43L, "Board 43");

        when(webT.path("api/boards/titles&ids")).thenReturn(webT);
        when(builder.get(new GenericType<Map<Long, String>>() {
        })).thenReturn(map);

        assertEquals(map, serverUtils.getBoardTitlesAndIds());
    }

    ;
}
