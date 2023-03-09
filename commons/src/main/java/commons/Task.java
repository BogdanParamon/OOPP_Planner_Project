package commons;

import javax.persistence.*;
import java.util.Objects;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long taskId;
    private String title;

    @ManyToOne
    @JoinColumn(name = "listId")
    private TaskList list;

    public Task(String title, TaskList list) {
        this.title = title;
        this.list = list;
    }

    public Task() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(title, task.title) && Objects.equals(list, task.list);
    }

    @Override
    public int hashCode() {
        return Objects.hash(taskId, title, list);
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public TaskList getList() {
        return list;
    }

    public void setList(TaskList list) {
        this.list = list;
    }
}
