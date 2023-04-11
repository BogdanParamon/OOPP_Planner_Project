package server.service;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.BoardRepository;
import server.database.TagRepository;
import server.database.UserRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class BoardServiceTest {

    private TagRepository tagRepository;
    private UserRepository userRepository;
    private BoardRepository boardRepository;
    private BoardService boardService;

    @BeforeEach
    public void setup() {
        tagRepository = mock(TagRepository.class);
        userRepository = mock(UserRepository.class);
        boardRepository = mock(BoardRepository.class);
        boardService = new BoardService(boardRepository, userRepository, tagRepository);
    }

    @Test
    public void getAll() {
        List<Board> expected = new ArrayList<>();
        expected.add(new Board("board1"));
        expected.add(new Board("board2"));
        expected.add(new Board("board3"));

        when(boardRepository.findAll()).thenReturn(expected);

        List<Board> actual = boardService.getAll();

        assertEquals(expected, actual);
    }

    @Test
    public void getAllWithEmptyList() {
        when(boardRepository.findAll()).thenReturn(new ArrayList<>());

        List<Board> actual = boardService.getAll();

        assertTrue(actual.isEmpty());
    }

    @Test
    public void getAllWithNull() {
        when(boardRepository.findAll()).thenReturn(null);

        List<Board> actual = boardService.getAll();

        assertNull(actual);
    }

    @Test
    public void getById() {
        long boardId = 1L;
        Optional<Board> expected = Optional.of(new Board("board1"));

        when(boardRepository.findById(boardId)).thenReturn(expected);

        Optional<Board> actual = boardService.getByid(boardId);

        assertEquals(expected, actual);
    }

    @Test
    public void getListsByBoardId() {
        Board board1 = new Board("board1");
        TaskList taskList1 = new TaskList("title1");
        TaskList taskList2 = new TaskList("title2");
        List<TaskList> expected = new ArrayList<>();
        expected.add(taskList1);
        expected.add(taskList2);
        board1.lists = expected;

        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));

        List<TaskList> actual = boardService.getListsByBoardId(1L);

        assertEquals(expected, actual);
    }

    @Test
    public void add() {
        Board expected = new Board("board1");
        User user = new User("user1");
        user.boards.add(expected);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.save(any())).thenReturn(expected);

        Board actual = boardService.add(expected, 1L);
        assertEquals(expected, actual);
    }

    @Test
    public void join() {
        Board expected = new Board("board1");
        User user = new User("user1");
        user.boards.add(expected);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.save(any())).thenReturn(expected);

        Board actual = boardService.add(expected, 1L);
        assertEquals(expected, actual);
    }

    @Test
    public void delete() {
        Board board1 = new Board("board1");
        board1.setId(1L);

        boardRepository.save(board1);

        when(boardRepository.existsById(1L)).thenReturn(true);

        String result = boardService.delete(1L);
        verify(boardRepository).deleteById(1L);

        List<Board> actual = boardRepository.findAll();

        assertTrue(actual.isEmpty());
        assertEquals("Successful", result);
    }

    @Test
    public void deleteWithInvalidId() {
        when(boardRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.delete(-1L));
    }

    @Test
    public void updateBoard() {
        Board first = new Board("first");
        first.setId(1L);
        first.backgroundColor = "#000000";
        first.boardColor = "#000000";
        first.listsColor = "#000000";
        first.backgroundColorFont = "#000000";
        first.buttonsColorFont = "#000000";
        first.buttonsBackground = "#000000";
        first.listsFontColor = "#000000";
        first.setPassword("abc");

        Board second = new Board("second");
        second.setId(2L);
        second.backgroundColor = "#FFFFFF";
        second.boardColor = "#FFFFFF";
        second.listsColor = "#FFFFFF";
        second.backgroundColorFont = "#FFFFFF";
        second.buttonsColorFont = "#FFFFFF";
        second.buttonsBackground = "#FFFFFF";
        second.listsFontColor = "#FFFFFF";
        second.setPassword("cba");

        when(boardRepository.existsById(2L)).thenReturn(true);
        when(boardRepository.save(second)).thenReturn(second);

        first = boardService.updateBoard(second);

        assertEquals(first.boardId, second.boardId);
        assertEquals(second, first);
        assertEquals(second.title, first.title);
        assertEquals(second.backgroundColor, first.backgroundColor);
        assertEquals(second.boardColor, first.boardColor);
        assertEquals(second.listsColor, first.listsColor);
        assertEquals(second.backgroundColorFont, first.backgroundColorFont);
        assertEquals(second.buttonsColorFont, first.buttonsColorFont);
        assertEquals(second.buttonsBackground, first.buttonsBackground);
        assertEquals(second.listsFontColor, first.listsFontColor);
        assertEquals(second.getPassword(), first.getPassword());
    }

    @Test
    public void updateBoardWithInvalidId() {
        when(boardRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class,() ->
                boardService.delete(-1L));
    }

    @Test
    public void deleteAll() {
        Board board1 = new Board("board1");
        board1.setId(1L);
        Board board2 = new Board("board2");
        board2.setId(2L);

        boardRepository.saveAll(Arrays.asList(board1, board2));
        boardRepository.deleteAll();

        String result = boardService.deleteAll();
        List<Board> actual = boardRepository.findAll();

        assertTrue(actual.isEmpty());
        assertEquals("Successful", result);
    }

//    @Test
//    public void getBoardTitleAndIds() {
//
//    }

    @Test
    public void renameBoard() {
        Board board1 = new Board("board1");
        board1.setId(1L);

        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));
        when(boardRepository.save(board1)).thenReturn(board1);

        Board actual = boardService.renameBoard(1L, "boardX");

        assertEquals("boardX", actual.title);
    }

    @Test
    public void renameBoardWithNull() {
        when(boardRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.renameBoard(1L, null));
    }

    @Test
    public void renameInvalidBoardId() {
        when(boardRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.renameBoard(-1L, "new_name"));
    }

    @Test
    public void changePassword() {
        Board board1 = new Board("board1");
        board1.setId(1L);

        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));
        when(boardRepository.save(board1)).thenReturn(board1);

        Board actual = boardService.changePassword(1L, "abc");

        assertEquals("abc", actual.getPassword());
    }

    @Test
    public void changePasswordWhenNull() {
        when(boardRepository.existsById(1L)).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.changePassword(1L, null));
    }

    @Test
    public void addTag() {
        Board board1 = new Board("board1");
        board1.setId(1L);
        Tag tag1 = new Tag("tag1", "#000000");


        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board1));
        when(tagRepository.save(tag1)).thenReturn(tag1);

        Tag actual = boardService.addTag(1L, tag1);

        assertTrue(board1.tags.contains(actual));
    }

    @Test
    public void addTagInvalidBoardId() {
        when(boardRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.addTag(-1L, new Tag()));
    }

    @Test
    public void getTag() {
        Tag tag1 = new Tag("tag1", "#121212");
        tag1.tagId = 1L;

        when(tagRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag1));

        Tag actual = boardService.getTag(1L);

        assertEquals(tag1, actual);
        assertEquals(tag1.getColor(), actual.getColor());
        assertEquals(tag1.getText(), actual.getText());
    }

    @Test
    public void getTagInvalidId() {
        when(tagRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.getTag(-1L));
    }

    @Test
    public void deleteTag() {
        Tag tag1 = new Tag("tag1", "#121212");
        tag1.tagId = 1L;

        when(tagRepository.existsById(1L)).thenReturn(true);

        String result = boardService.deleteTag(1L);

        assertEquals("Successful", result);
    }

    @Test
    public void deleteTagInvalidId() {
        when(tagRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.deleteTag(-1L));
    }

    @Test
    public void updateTag() {
        Tag tag1 = new Tag("tag1", "#000000");
        tag1.tagId = 1L;
        Tag tag2 = new Tag("tag2", "#FFFFFF");
        tag2.tagId = 2L;

        when(tagRepository.existsById(2L)).thenReturn(true);
        when(tagRepository.save(tag2)).thenReturn(tag2);

        tag1 = boardService.updateTag(tag2);

        assertEquals(tag2, tag1);
        assertEquals(tag2.getText(), tag1.getText());
        assertEquals(tag2.getColor(), tag1.getColor());
    }

    @Test
    public void updateTagWithInvalidId() {
        Tag tag1 = new Tag("tag1", "#000000");
        tag1.tagId = -1L;
        when(tagRepository.existsById(-1L)).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () ->
                boardService.updateTag(tag1));
    }

}

