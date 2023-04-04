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
    public Tag tag;

    public Packet() { }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Packet)) return false;
        Packet packet = (Packet) o;
        return longValue == packet.longValue
                && longValue2 == packet.longValue2
                && longValue3 == packet.longValue3
                && intValue == packet.intValue
                && Objects.equals(stringValue, packet.stringValue)
                && Objects.equals(task, packet.task)
                && Objects.equals(taskList, packet.taskList)
                && Objects.equals(board, packet.board)
                && Objects.equals(tag, packet.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hash(longValue, longValue2, longValue3,
                intValue, stringValue, task, taskList, board, tag);
    }

    @Override
    public String toString() {
        return "Packet{" + ", \n" +
                " longValue=" + longValue + ", \n" +
                " longValue2=" + longValue2 + ", \n" +
                " longValue3=" + longValue3 + ", \n" +
                " intValue=" + intValue + ", \n" +
                " stringValue='" + stringValue + "', \n" +
                " task=" + task + ", \n" +
                " taskList=" + taskList + ", \n" +
                " board=" + board + ", \n" +
                " tag=" + tag + ", \n" +
                '}';
    }
}
