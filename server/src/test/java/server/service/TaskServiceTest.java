package server.service;

import commons.Tag;
import commons.Task;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.TagRepository;
import server.database.TaskListRepository;
import server.database.TaskRepository;

import java.util.Optional;

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
        this.taskService = new TaskService(taskRepository, taskListRepository, tagRepository);
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
        assertThrows(IllegalArgumentException.class, () -> taskService.add(1L, task));
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
        assertThrows(IllegalArgumentException.class, () -> taskService.delete(-1L, -1L));
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

//    @Test
//    public void testAddTagThrowsException() {
//
//    }
}
