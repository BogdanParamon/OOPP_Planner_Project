package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.Objects;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long taskId;
    public String title;

    @ManyToOne
    @JoinColumn(name = "listId")
    public TaskList list;

    public Task(String title, TaskList list) {
        this.title = title;
        this.list = list;
    }

    private Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(title, task.title) && Objects.equals(list.listId, task.list.listId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, title, list.listId);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
