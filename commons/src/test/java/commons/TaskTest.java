package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskTest {

    @Test
    public void checkConstructor() {
        var t = new Task("Task Title");
        assertEquals("Task Title", t.title);
    }

    @Test
    public void checkEmptyConstructor() {
        var t = new Task();
        assertNotNull(t);
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
        assertTrue(actual.contains("title"));
    }

    @Test
    public void testSetId() {
        var a = new Task("a");
        a.setId(1L);
        assertEquals(1L, a.taskId);
    }

    @Test
    public void testSetTitle() {
        var a = new Task("a");
        a.setTitle("b");
        assertEquals("b", a.title);
    }

    @Test
    public void testAddSubtask() {
        var a = new Task("a");
        var subtask = new Subtask("subtask");
        a.addSubtask(subtask);
        assertTrue(a.subtasks.contains(subtask));
    }
}
