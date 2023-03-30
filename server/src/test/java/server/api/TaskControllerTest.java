package server.api;


import commons.Task;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TaskControllerTest {
    private TestTaskRepository trt;
    private TestTaskListRepository tlrt;
    private TaskController sut;

    private static Task getUntitledTask() {
        return new Task("");
    }

    @BeforeEach
    public void setup() {
        trt = new TestTaskRepository();
        tlrt = new TestTaskListRepository();
        sut = new TaskController(trt, tlrt);
    }

    @Test
    public void cannotAddUntitledTask() {
        var actual = sut.add(1L, getUntitledTask());
        assertEquals(BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void cannotAddNullTask() {
        var actual = sut.add(0L, null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void getTaskByIdNotFound() {
        ResponseEntity<Task> actual = sut.getTaskById(100L);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteTaskBadRequest() {
        ResponseEntity<Task> actual = sut.delete(-1L, -1L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void canUpdateTask() {
        Task task = new Task();
        task.setId(1L);
        task.setTitle("Test Task");
        trt.save(task);

        Task updatedTask = new Task();
        updatedTask.setId(1L);
        updatedTask.setTitle("Updated Test Task");

        ResponseEntity<Task> actual = sut.updateTask(updatedTask);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("Updated Test Task", actual.getBody().title);
    }

    @Test
    public void canAddValidTask() {
        Task validTask = new Task();
        validTask.setTitle("Test Task");
        TaskList taskList = new TaskList("Test List");
        taskList.setListId(1L);
        tlrt.save(taskList);

        ResponseEntity<Task> actual = sut.add(1L, validTask);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("Test Task", actual.getBody().title);
    }

}

