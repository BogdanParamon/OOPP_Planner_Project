package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TaskListTest {

    @Test
    public void checkConstructor() {
        var t = new TaskList("Task List Title");
        assertEquals("Task List Title", t.title);
        assertNotNull(t.tasks);
    }

    @Test
    public void checkEmptyConstructor() {
        var t = new TaskList();
        assertNotNull(t);
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

    @Test
    public void testSetListId() {
        var list = new TaskList("a");
        list.setListId(1L);
        assertEquals(1L, list.listId);
    }

    @Test
    public void testSetTitle() {
        var list = new TaskList("a");
        list.setTitle("b");
        assertEquals("b", list.title);
    }
}
