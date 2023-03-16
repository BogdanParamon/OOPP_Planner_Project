package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {
    //private static final Board SOME_BOARD = new Board("BoardTitle");

    @Test
    public void checkConstructor() {
        var t = new TaskList("Task List Title");
        assertEquals("Task List Title", t.title);
        assertNotNull(t.tasks);
    }

    @Test
    public void equalsHashCode() {
        var a = new TaskList("a");
        var b = new TaskList("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new TaskList("a");
        var b = new TaskList("b");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new TaskList("a").toString();
        assertTrue(actual.contains(TaskList.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }

    @Test
    public void checkAddTask() {
        var list = new TaskList("a");
        var task = new Task("b");
        list.addTask(task);
        assertTrue(list.tasks.contains(task));
    }
}
