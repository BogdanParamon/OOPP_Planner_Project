package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    @Test
    void testConstructor() {
        var user = new User("name");
        assertNotNull(user);
        assertEquals("name", user.userName);
    }

    @Test
    void testEmptyConstructor() {
        var user = new User();
        assertNotNull(user);
        assertEquals(User.class, user.getClass());
    }

    @Test
    public void equalsHashCode() {
        var a = new User("a");
        var b = new User("a");
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new User("a");
        var b = new User("b");
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }


    @Test
    public void hasToString() {
        var actual = new User("testText").toString();
        assertTrue(actual.contains(User.class.getSimpleName()));
        assertTrue(actual.contains("\n"));
        assertTrue(actual.contains("testText"));
    }
}