package server.service;

import commons.Tag;
import commons.Task;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Example;
import server.database.TagRepository;
import server.database.TaskListRepository;
import server.database.TaskRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskServiceTest {

    private TaskRepository taskRepository;

    private TaskListRepository taskListRepository;

    private TagRepository tagRepository;

    private TaskService taskService;

    @BeforeEach
    public void setup() {
        this.taskRepository = mock(TaskRepository.class);
        this.taskListRepository = mock(TaskListRepository.class);
        this.tagRepository = mock(TagRepository.class);
        this.taskService =
                new TaskService(taskRepository, taskListRepository, tagRepository);
    }

    @Test
    public void testConstructor() {
        assertNotNull(taskService);
    }

    @Test
    public void testAddTask() {
        Task task = new Task();
        task.title = "Test task";

        TaskList taskList = new TaskList();
        taskList.listId = 1L;

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(taskList));
        when(taskRepository.save(task)).thenReturn(task);

        Task result = taskService.add(1L, task);
        assertEquals(result, task);
    }

    @Test
    public void testAddTaskThrowsException() {
        Task task = new Task();
        assertThrows(IllegalArgumentException.class,
                () -> taskService.add(1L, task));
    }

    @Test
    public void testGetById() {
        Task task = new Task();
        task.taskId = 1L;

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        taskRepository.save(task);
        assertEquals(taskService.getTaskById(1L), task);
    }

    @Test
    public void testGetByIdNegativeId() {
        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(-1L));
    }

    @Test
    public void testGetByIdNotExist() {
        assertThrows(IllegalArgumentException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    public void testGetAllTasks() {
        Task task1 = new Task();
        Task task2 = new Task();
        List<Task> result = new ArrayList<>();
        result.add(task1);
        result.add(task2);

        when(taskRepository.save(task1)).thenReturn(task1);
        when(taskRepository.save(task2)).thenReturn(task2);
        when(taskRepository.findAll()).thenReturn(result);

        taskRepository.save(task1);
        taskRepository.save(task2);
        assertEquals(taskService.getAll(), result);
    }

    @Test
    public void testGetAllEmptyList() {
        when(taskRepository.findAll()).thenReturn(new LinkedList<>());

        assertEquals(new LinkedList<>(), taskService.getAll());
    }

    @Test
    public void testGetAllNull() {
        when(taskRepository.findAll()).thenReturn(null);

        assertNull(taskService.getAll());
    }

    @Test
    public void testGetAllTasksSortBy() {
        Task task1 = new Task("a");
        task1.taskId = 1L;
        Task task2 = new Task("b");
        task2.taskId = 2L;
        List<Task> result = Arrays.asList(task1, task2);

        when(taskRepository.save(task1)).thenReturn(task1);
        when(taskRepository.save(task2)).thenReturn(task2);
        when(taskRepository.findAll()).thenReturn(result);

        taskRepository.save(task2);
        taskRepository.save(task1);
        assertEquals(taskService.getAllTasks(null), result);
    }

    @Test
    public void testGetAllSortByFalse() {
        Task task1 = new Task();
        task1.taskId = 1L;
        List<Task> result = List.of(task1);

        when(taskRepository.save(task1)).thenReturn(task1);
        when(taskRepository.findAll()).thenReturn(result);

        List<Task> list = Arrays.asList(
                new Task(), new Task()
        );
        assertNotEquals(taskService.getAllTasks(null), list);
    }

    @Test
    public void testUpdateTask() {
        Task task = new Task();
        task.taskId = 1L;

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        Task updated = taskService.updateTask(task);
        assertEquals(task, updated);
    }

    @Test
    public void testUpdateTaskThrowsException() {
        Task task = new Task();
        task.taskId = -1L;
        assertThrows(IllegalArgumentException.class, () -> taskService.updateTask(task));
    }

    @Test
    public void testDeleteTask() {
        Task task = new Task();
        TaskList taskList = new TaskList();
        task.taskId = 1L;
        taskList.listId = 1L;
        taskList.tasks.add(task);

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(taskList));
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        assertEquals("Successful", taskService.delete(1L, 1L));
    }

    @Test
    public void testDeleteTaskThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.delete(-1L, -1L));
    }

    @Test
    public void testAddTag() {
        Tag tag = new Tag();
        tag.tagId = 1L;
        Task task = new Task();
        task.taskId = 1L;
        task.tags.add(tag);

        when(tagRepository.existsById(1L)).thenReturn(true);
        when(tagRepository.findById(1L)).thenReturn(Optional.of(tag));
        when(tagRepository.save(tag)).thenReturn(tag);
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(task)).thenReturn(task);

        assertEquals(taskService.addTag(1L, 1L), task);
    }

    @Test
    public void testAddTagThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.addTag(-1L, -1L));
    }

    @Test
    public void testDragTask() {
        TaskList listFrom = new TaskList();
        listFrom.listId = 1L;
        TaskList listTo = new TaskList();
        listTo.listId = 2L;
        Task task = new Task();
        task.taskId = 1L;
        listFrom.tasks.add(task);

        when(taskListRepository.existsById(1L)).thenReturn(true);
        when(taskListRepository.findById(1L)).thenReturn(Optional.of(listFrom));
        when(taskListRepository.existsById(2L)).thenReturn(true);
        when(taskListRepository.findById(2L)).thenReturn(Optional.of(listTo));
        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskListRepository.save(listFrom)).thenReturn(listFrom);
        when(taskListRepository.save(listTo)).thenReturn(listTo);
        when(taskRepository.save(task)).thenReturn(task);

        assertEquals("Successful", taskService
                .dragTask(1L, 1L, 2L, 0));
    }

    @Test
    public void testDragTaskThrowsException() {
        assertThrows(IllegalArgumentException.class,
                () -> taskService.dragTask(-1L, 1L, 2L, 0));
    }
}
