package server.api;

import commons.Board;
import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class TaskListControllerTest {

    private TestTaskListRepository repository;
    private TaskListController taskListController;

    @BeforeEach
    public void setup() {
        repository = new TestTaskListRepository();
        taskListController = new TaskListController(repository);
    }

    @Test
    public void cannotAddUntitledTaskList() {
        var actual = taskListController.add(getTaskList(null));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

//    @Test
//    public void getByIdTest() {
//        var actual = taskListController.add(getTaskList(null));
//        assertEquals(actual, repository.getById(actual.getBody().listId));
//    }

    private static TaskList getTaskList(String s) {
        return new TaskList(s);
    }

}