package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardTest {

    @Test
    public void checkConstructor() {
        var b = new Board("BoardTitle");
        assertEquals("BoardTitle", b.title);
        assertNotNull(b.lists);
    }

    @Test
    public void equalsHashCode() {
        var a = new Board("a");
        var b = new Board("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Board("a");
        var b = new Board("b");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var actual = new Board("a").toString();
        assertTrue(actual.contains(Board.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("title"));
    }

    @Test
    public void checkAddList() {
        var board = new Board("a");
        var list = new TaskList("b", board);
        board.addList(list);
        assertTrue(board.lists.contains(list));
    }
}
