package server.service;

import commons.Board;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.BoardRepository;
import server.database.TaskListRepository;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class TaskListServiceTest {
    private TaskListRepository taskListRepository;
    private BoardRepository boardRepository;
    private TaskListService taskListService;

    @BeforeEach
    public void setup() {
        taskListRepository = mock(TaskListRepository.class);
        boardRepository = mock(BoardRepository.class);
        taskListService = new TaskListService(taskListRepository, boardRepository);
    }

    @Test
    public void testAddTaskList() {
        TaskList taskList = new TaskList();
        taskList.title = "Test List";
        taskList.tasks = new ArrayList<>();

        Board board = new Board();
        board.setId(1L);

        when(boardRepository.existsById(1L)).thenReturn(true);
        when(boardRepository.findById(1L)).thenReturn(Optional.of(board));
        when(taskListRepository.save(taskList)).thenReturn(taskList);

        TaskList result = taskListService.add(taskList, 1L);
        assertEquals(taskList, result);
    }

    @Test
    public void testAddTaskListThrowsException() {
        TaskList taskList = new TaskList();
        taskList.title = "";
        taskList.tasks = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> taskListService.add(taskList, 1L));
    }

    @Test
    public void testRenameList() {
        TaskList taskList = new TaskList();
        taskList.listId = 1L;
        taskList.title = "Test List";
        taskList.tasks = new ArrayList<>();

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(taskList));
        when(taskListRepository.save(taskList)).thenReturn(taskList);

        TaskList result = taskListService.renameList(1L, "New Test List");
        assertEquals("New Test List", result.title);
    }

    @Test
    public void testRenameListThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskListService.renameList(1L, null));
    }

    @Test
    public void testDeleteList() {
        TaskList taskList = new TaskList();
        taskList.listId = 1L;
        taskList.title = "Test List";
        taskList.tasks = new ArrayList<>();

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(taskList));

        String result = taskListService.delete(1L);
        assertEquals("Successful", result);
    }

    @Test
    public void testDeleteListThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> taskListService.delete(-1L));
    }

    @Test
    public void testUpdateList() {
        TaskList taskList = new TaskList();
        taskList.listId = 1L;
        taskList.title = "Test List";
        taskList.tasks = new ArrayList<>();

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.save(taskList)).thenReturn(taskList);

        TaskList result = taskListService.updateList(taskList);
        assertEquals(taskList, result);
    }

    @Test
    public void testUpdateListThrowsException() {
        TaskList taskList = new TaskList();
        taskList.listId = -1L;
        taskList.title = "Test List";
        taskList.tasks = new ArrayList<>();

        assertThrows(IllegalArgumentException.class, () -> taskListService.updateList(taskList));
    }
}