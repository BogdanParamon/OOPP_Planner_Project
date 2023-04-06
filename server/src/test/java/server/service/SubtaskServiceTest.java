package server.service;

import commons.Subtask;
import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import server.database.SubtaskRepository;
import server.database.TaskRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class SubtaskServiceTest {
    private SubtaskRepository subtaskRepository;
    private TaskRepository taskRepository;
    private SubtaskService subtaskService;

    @BeforeEach
    public void setup() {
        subtaskRepository = mock(SubtaskRepository.class);
        taskRepository = mock(TaskRepository.class);
        subtaskService = new SubtaskService(subtaskRepository, taskRepository);
    }

    @Test
    public void testAddSubtask() {
        Task task = new Task();
        task.setId(1L);
        Subtask subtask = new Subtask();
        subtask.setSubtaskText("Test Subtask");

        when(taskRepository.existsById(1L)).thenReturn(true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(subtaskRepository.save(subtask)).thenReturn(subtask);

        Subtask result = subtaskService.add(1L, subtask);
        assertEquals(subtask, result);
    }

    @Test
    public void testAddSubtaskThrowsException() {
        Subtask subtask = new Subtask();
        subtask.setSubtaskText("");

        assertThrows(IllegalArgumentException.class, () -> subtaskService.add(1L, subtask));
    }

    @Test
    public void testUpdateSubtask() {
        Subtask subtask = new Subtask();
        subtask.setSubtaskText("Updated Subtask");

        when(subtaskRepository.existsById(anyLong())).thenReturn(true);
        when(subtaskRepository.save(subtask)).thenReturn(subtask);

        Subtask result = subtaskService.updateSubtask(subtask);
        assertEquals(subtask, result);
    }

    @Test
    public void testUpdateSubtaskThrowsException() {
        Subtask subtask = new Subtask();
        subtask.setSubtaskText("Updated Subtask");

        assertThrows(IllegalArgumentException.class, () -> subtaskService.updateSubtask(subtask));
    }

    @Test
    public void testDeleteSubtask() {
        Task task = new Task();
        task.setId(1L);
        Subtask subtask = new Subtask();
        subtask.setSubtaskText("Test Subtask");
        task.addSubtask(subtask);

        when(subtaskRepository.existsById(anyLong())).thenReturn(true);
        when(taskRepository.existsById(anyLong())).thenReturn(true);
        when(taskRepository.findById(anyLong())).thenReturn(Optional.of(task));
        when(subtaskRepository.findById(anyLong())).thenReturn(Optional.of(subtask));

        String result = subtaskService.delete(1L, 1L);
        assertEquals("Successful", result);
    }

    @Test
    public void testDeleteSubtaskThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> subtaskService.delete(-1L, 1L));
    }
}
