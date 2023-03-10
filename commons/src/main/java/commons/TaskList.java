package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long listId;
    public String title;

    @ManyToOne
    @JoinColumn(name = "boardId")
    public Board board;

    @OneToMany(mappedBy = "list")
    public Set<Task> tasks;

    public TaskList(String title, Board board) {
        this.title = title;
        this.board = board;
        tasks = new HashSet<Task>();
    }

    private TaskList() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskList taskList = (TaskList) o;
        return listId == taskList.listId && Objects.equals(title, taskList.title) && Objects.equals(board.boardId, taskList.board.boardId) && Objects.equals(tasks, taskList.tasks);
    }

    @Override
    public int hashCode() {
        return Objects.hash(listId, title, board.boardId, tasks);
    }

    public void addTask(Task task){
        tasks.add(task);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }
}
