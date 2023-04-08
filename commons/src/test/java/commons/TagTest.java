package commons;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TagTest {

    private Board board;
    private Tag tag;

    private Task task;

    @BeforeEach
    public void setup() {
        board = new Board("testBoard");
        tag = new Tag("testTag-1", "#ffffff");
        task = new Task("testTask-1");
    }

    @Test
    public void testConstructor() {
        Tag tag2 = new Tag("testTag-1", "#ffffff");
        assertNotNull(tag2);
    }

    @Test
    public void testEmptyConstructor() {
        Tag tag2 = new Tag();
        assertNotNull(tag2);
    }

    @Test
    public void testGetText() {
        assertEquals("testTag-1", tag.getText());
    }

    @Test
    public void testSetText() {
        tag.setText("testTag-2");
        assertEquals("testTag-2", tag.getText());
    }

    @Test
    public void testGetColor() {
        assertEquals("#ffffff", tag.getColor());
    }

    @Test
    public void testSetColor() {
        tag.setColor("#000000");
        assertEquals("#000000", tag.getColor());
    }

    @Test
    public void testEquals() {
        Tag tag2 = new Tag("testTag-1", "#ffffff");
        assertEquals(tag, tag2);
    }

    @Test
    public void testHashCode() {
        Tag tag2 = new Tag("testTag-1", "#ffffff");
        assertEquals(tag.hashCode(), tag2.hashCode());
    }

    @Test
    public void testAddTag() {
        board.addTag(tag);
        assertTrue(board.tags.contains(tag));
    }

    @Test
    public void addTagToTask() {
        task.addTag(tag);
        assertTrue(task.tags.contains(tag));
    }
}
