package server.api;


import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class TaskControllerTest {
    private TestTaskRepository trt;
    private TaskController sut;

    @BeforeEach
    public void setup() {
        trt = new TestTaskRepository();
        sut = new TaskController(trt);
    }

    @Test
    public void cannotAddUntitledTask() {
        var actual = sut.add(getUntitledTask());
        assertEquals(BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void cannotAddNullTask() {
        var actual = sut.add(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());

    }

    @Test
    public void getTaskByIdNotFound() {
        ResponseEntity<Task> actual = sut.getTaskById(100L);
        assertEquals(HttpStatus.NOT_FOUND, actual.getStatusCode());
    }

    @Test
    public void deleteTaskBadRequest() {
        ResponseEntity<Task> actual = sut.delete(-1L);
        assertEquals(HttpStatus.BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void canAddValidTask() {
        Task validTask = new Task();
        validTask.setTitle("Test Task");

        ResponseEntity<Task> actual = sut.add(validTask);
        assertEquals(HttpStatus.OK, actual.getStatusCode());
        assertEquals("Test Task", actual.getBody().getTitle());
    }

    //@Test
    //public void canUpdateTask() {
        //Task task = new Task();
        //task.setId(1L);
        //task.setTitle("Test Task");
        //trt.save(task);

        //Task updatedTask = new Task();
        //updatedTask.setTitle("Updated Test Task");

        //ResponseEntity<Task> actual = sut.updateTask(1L, updatedTask);
        //assertEquals(HttpStatus.OK, actual.getStatusCode());
        //assertEquals("Updated Test Task", actual.getBody().getTitle());
    //}

    private static Task getUntitledTask() {
        return new Task("");
    }

}

