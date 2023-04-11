package server.service;

import commons.Board;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.UserRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    private UserRepository userRepository;
    private UserService userService;

    @BeforeEach
    public void setUp() {
        userRepository = mock(UserRepository.class);
        userService = new UserService(userRepository);
    }

    @Test
    public void testConnectToUser_NewUser() {
        String userName = "testUser";
        User newUser = new User(userName);

        when(userRepository.findAll()).thenReturn(List.of());
        when(userRepository.save(newUser)).thenReturn(newUser);

        User result = userService.connectToUser(userName);
        assertEquals(newUser, result);
    }

    @Test
    public void testConnectToUser_ExistingUser() {
        String userName = "testUser";
        User existingUser = new User(userName);
        existingUser.userId = 1l;

        when(userRepository.findAll()).thenReturn(List.of(existingUser));

        User result = userService.connectToUser(userName);
        assertEquals(existingUser, result);
    }

    @Test
    public void testGetBoardTitlesAndIdsByUserId() {
        User user = new User("testUser");
        user.userId = 1l;

        Board board1 = new Board("Board 1");
        board1.setId(1L);
        Board board2 = new Board("Board 2");
        board2.setId(2L);

        user.boards.add(board1);
        user.boards.add(board2);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Map<Long, String> result = userService.getBoardTitlesAndIdsByUserId(1L);

        assertEquals(2, result.size());
        assertTrue(result.containsKey(board1.boardId));
        assertTrue(result.containsKey(board2.boardId));
        assertEquals(board1.title, result.get(board1.boardId));
        assertEquals(board2.title, result.get(board2.boardId));
    }

    @Test
    public void testVerifyAdminPassword() {
        String adminPassword = "admin";
        assertTrue(userService.verifyAdminPassword(adminPassword));
    }

    @Test
    public void testLeaveBoard() {
        User user = new User("testUser");
        user.userId = 1l;

        Board board1 = new Board("Board 1");
        board1.setId(1L);
        Board board2 = new Board("Board 2");
        board2.setId(2L);

        user.boards.add(board1);
        user.boards.add(board2);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        String result = userService.leaveBoard(1L, 1L);

        assertEquals("Successful", result);
        assertEquals(1, user.boards.size());
        assertTrue(user.boards.contains(board2));
    }

    @Test
    public void testLeaveMessage() {
        User user = new User("testUser");
        user.userId = 1l;

        Board board1 = new Board("Board 1");
        board1.setId(1L);
        Board board2 = new Board("Board 2");
        board2.setId(2L);

        user.boards.add(board1);
        user.boards.add(board2);

        when(userRepository.existsById(1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);

        Long result = userService.leaveMessage(1L, 1L);

        assertEquals(1L, result);
        assertEquals(1, user.boards.size());
        assertTrue(user.boards.contains(board2));
    }
}
