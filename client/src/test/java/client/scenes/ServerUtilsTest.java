package client.scenes;

import client.utils.ServerUtils;
import commons.Board;
import commons.Subtask;
import commons.Tag;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServerUtilsTest {

    private ServerUtils serverUtils;

    @BeforeEach
    public void setup() {
        serverUtils = mock(ServerUtils.class);
        ServerUtils.setSERVER("localhost:8080");
    }

    @Test
    public void testServer() {
        when(serverUtils.validServer()).thenReturn(true);
        assertTrue(serverUtils.validServer());
    }

    @Test
    public void testServer2() {
        ServerUtils.setSERVER("123");
        when(serverUtils.validServer()).thenReturn(false);
        assertFalse(serverUtils.validServer());
    }

    @Test
    public void getBoardByIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;
        when(serverUtils.getBoardById(42)).thenReturn(board);
        assertEquals(board, serverUtils.getBoardById(42));
    }

    @Test
    public void getBoardByIdTest2() {
        Board board = new Board("Board 42");
        board.boardId = 42;
        when(serverUtils.getBoardById(42)).thenReturn(board);
        assertNotEquals(board, serverUtils.getBoardById(43));
    }

    @Test
    public void getBoardTitlesAndIdsByUserIdTest() {
        Board board = new Board("Board 42");
        board.boardId = 42;
        Map<Long, String> map = new HashMap<>();
        map.put(42L, "Board 42");
        when(serverUtils.getBoardTitlesAndIdsByUserId(42)).thenReturn(map);
        assertEquals(map, serverUtils.getBoardTitlesAndIdsByUserId(42));
    }

    @Test
    public void getBoardTitlesAndIdsByUserIdTest2() {
        Board board = new Board("Board 42");
        board.boardId = 42;
        Map<Long, String> map = new HashMap<>();
        map.put(42L, "Board 42");
        when(serverUtils.getBoardTitlesAndIdsByUserId(42)).thenReturn(map);
        assertNotEquals(map, serverUtils.getBoardTitlesAndIdsByUserId(43));
    }

    @Test
    public void connectWebsocketTest() {
        StompSession session = mock(StompSession.class);
        when(serverUtils.connectWebsocket()).thenReturn(session);
        assertNotNull(serverUtils.connectWebsocket());
    }

    @Test
    public void websocketSendTest() {
        doNothing().when(serverUtils).send(anyString(), anyString());
        assertDoesNotThrow(() -> serverUtils.send("boards/addBoard/42", new Board("Board 42")));
    }

    @Test
    public void deleteAllBoardsTest() {
        doNothing().when(serverUtils).deleteAllBoards();
        assertDoesNotThrow(() -> serverUtils.deleteAllBoards());
    }

    @Test
    public void connectToUserTest() {
        User user = new User("User 42");
        when(serverUtils.connectToUser("User 42")).thenReturn(user);
        assertEquals(user, serverUtils.connectToUser("User 42"));
    }

    @Test
    public void connectToUserTest2() {
        User user = new User("User 42");
        when(serverUtils.connectToUser("User 42")).thenReturn(user);
        assertNotEquals(user, serverUtils.connectToUser("User 43"));
    }

    @Test
    public void addSubtaskTest() {
        Subtask subtask = new Subtask("Subtask 42");
        when(serverUtils.addSubtask(42, subtask)).thenReturn(subtask);
        assertEquals(subtask, serverUtils.addSubtask(42, subtask));
    }

    @Test
    public void addSubtaskTest2() {
        Subtask subtask = new Subtask("Subtask 42");
        when(serverUtils.addSubtask(42, subtask)).thenReturn(subtask);
        assertNotEquals(subtask, serverUtils.addSubtask(43, subtask));
    }

    @Test
    public void getTagByIdTest() {
        Tag tag = new Tag("Tag 42", "cyan");
        tag.tagId = 42;
        when(serverUtils.getTagById(42)).thenReturn(tag);
        assertEquals(tag, serverUtils.getTagById(42));
    }

    @Test
    public void getTagByIdTest2() {
        Tag tag = new Tag("Tag 42", "cyan");
        tag.tagId = 42;
        when(serverUtils.getTagById(42)).thenReturn(tag);
        assertNotEquals(tag, serverUtils.getTagById(43));
    }

    @Test
    public void disconnectWebsocketTest() {
        doNothing().when(serverUtils).disconnectWebsocket();
        assertDoesNotThrow(() -> serverUtils.disconnectWebsocket());
    }


}
