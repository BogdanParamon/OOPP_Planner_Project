package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubtaskTest {

    @Test
    void testConstructor() {
        var subtask = new Subtask("text");
        assertNotNull(subtask);
        assertEquals("text", subtask.subtaskText);
    }

    @Test
    void testEmptyConstructor() {
        var subtask = new Subtask();
        assertNotNull(subtask);
        assertEquals(Subtask.class, subtask.getClass());
    }

    @Test
    public void equalsHashCode() {
        var a = new Subtask("a");
        var b = new Subtask("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Subtask("a");
        var b = new Subtask("b");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }


    @Test
    public void hasToString() {
        var actual = new Subtask("testText").toString();
        assertTrue(actual.contains(Subtask.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("testText"));
    }
}