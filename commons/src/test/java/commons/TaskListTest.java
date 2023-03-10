package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {
    private static final Board SOME_BOARD = new Board("BoardTitle");

    @Test
    public void checkConstructor() {
        var t = new TaskList("Task List Title", SOME_BOARD);
        assertEquals("Task List Title", t.title);
        assertEquals(SOME_BOARD, t.board);
        assertNotNull(t.tasks);
    }

    @Test
    public void equalsHashCode() {
        var a = new TaskList("a", SOME_BOARD);
        var b = new TaskList("a", SOME_BOARD);
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new TaskList("a", SOME_BOARD);
        var b = new TaskList("b", SOME_BOARD);
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new TaskList("a", SOME_BOARD).toString();
        assertTrue(actual.contains(TaskList.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("board"));
    }

    @Test
    public void checkAddTask(){
        var board = new Board("a");
        var list = new TaskList("b", board);
        var task = new Task("c", list);
        list.addTask(task);
        assertTrue(list.tasks.contains(task));
    }
}
