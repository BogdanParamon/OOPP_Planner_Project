package commons;

import java.util.Objects;

public class Packet {
    public long longValue;
    public long longValue2;
    public long longValue3;
    public int intValue;
    public String stringValue;
    public Task task;
    public TaskList taskList;
    public Board board;

    public Packet() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Packet packet = (Packet) o;
        return longValue == packet.longValue && longValue2 == packet.longValue2
                && Objects.equals(stringValue, packet.stringValue)
                && Objects.equals(task, packet.task) && Objects.equals(taskList, packet.taskList)
                && Objects.equals(board, packet.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longValue, longValue2, stringValue, task, taskList, board);
    }
}
