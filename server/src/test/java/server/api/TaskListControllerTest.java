package server.api;

import commons.TaskList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

class TaskListControllerTest {

    private TestTaskListRepository repository;
    private TestBoardRepository boardRepository;
    private TaskListController taskListController;

    @BeforeEach
    public void setup() {
        repository = new TestTaskListRepository();
        taskListController = new TaskListController(repository, boardRepository);
    }

    @Test
    public void cannotAddUntitledTaskList() {
        var actual = taskListController.add(getTaskList(""), 0);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddNullTaskList() {
        var actual = taskListController.add(getTaskList(null), 0);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotDeleteListWithNegativeId() {
        var actual = taskListController.delete(-1);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateListWithNegativeID() {
        var actual = taskListController.updateList( new TaskList("a"));
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotUpdateListWithNullTitle() {
        var actual = taskListController.updateList(null);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }


    private static TaskList getTaskList(String s) {
        return new TaskList(s);
    }

}