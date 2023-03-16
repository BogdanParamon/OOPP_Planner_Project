package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {
    //private static final Board SOME_BOARD = new Board("BoardTitle");
    //private static final TaskList SOME_LIST = new TaskList("ListTitle", SOME_BOARD);

    @Test
    public void checkConstructor() {
        var t = new Task("Task Title");
        assertEquals("Task Title", t.title);
    }

    @Test
    public void equalsHashCode() {
        var a = new Task("a");
        var b = new Task("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Task("a");
        var b = new Task("b");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Task("a").toString();
        assertTrue(actual.contains(Task.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }
}
