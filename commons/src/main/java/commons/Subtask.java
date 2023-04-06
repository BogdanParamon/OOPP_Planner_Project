package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Subtask {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long subTaskId;

    public String subtaskText = "New Subtask";
    public boolean subtaskBoolean;

    public Subtask(String subtaskText) {
        this.subtaskText = subtaskText;
    }

    Subtask() {
        // For object mapper
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Subtask)) return false;
        Subtask subtask = (Subtask) o;
        return subTaskId == subtask.subTaskId
                && subtaskBoolean == subtask.subtaskBoolean
                && Objects.equals(subtaskText, subtask.subtaskText);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subTaskId, subtaskText, subtaskBoolean);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "subTaskId=" + subTaskId +
                ", subtaskText='" + subtaskText + '\'' +
                ", subtaskBoolean=" + subtaskBoolean +
                '}';
    }

    public void setSubtaskText(String subtaskText) {
        this.subtaskText = subtaskText;
    }
}
