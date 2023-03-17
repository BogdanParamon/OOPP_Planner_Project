package server.api;


import commons.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private static Task getUntitledTask() {
        return new Task("");
    }

}

