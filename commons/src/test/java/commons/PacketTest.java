package commons;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PacketTest {
    @Test
    public void checkConstructor() {
        var t = new Packet();
        assertNotNull(t);
    }

    @Test
    public void equalsHashCode() {
        var a = new Packet();
        var b = new Packet();
        a.longValue = 1L;
        b.longValue = 1L;
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void notEqualsHashCode() {
        var a = new Packet();
        var b = new Packet();
        a.longValue = 1L;
        b.longValue2 = 1L;
        assertNotEquals(a, b);
        assertNotEquals(a.hashCode(), b.hashCode());
    }

    @Test
    public void hasToString() {
        var a = new Packet();
        a.longValue = 2L;
        a.intValue = 1;
        var actual = a.toString();
        assertTrue(actual.contains(Packet.class.getSimpleName()));
        assertTrue(actual.contains("intValue"));
    }
}
