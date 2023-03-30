package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long taskId;

    public String title;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "taskId")
    public Set<Tag> tags = new HashSet<>();

    /**
     * Creates a new Task object with the given title and list.
     *
     * @param title The title to be given to the Task
     */
    public Task(String title) {
        this.title = title;
    }

    @SuppressWarnings("unused")
    public Task() {
        // For object mapper
    }

    /**
     * Compares this object to another object and returns true only if they are equal.
     * Two Tasks are equal if they have the same taskId, title and are in the same list.
     *
     * @param o The object that is going to be compared to this
     * @return True if and only if the other object is a Task with the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return taskId == task.taskId && Objects.equals(title, task.title)
                && Objects.equals(tags, task.tags);
    }

    /**
     * Generates a hash code for this object,
     * which uses the taskId, title and list attributes.
     *
     * @return The generated hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(taskId, title, tags);
    }

    /**
     * Generates a String representation of this object and its attributes in a multi-line style.
     *
     * @return The generated String representation for this object
     */
    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, MULTI_LINE_STYLE);
    }

    public void setId(Long id) {
        taskId = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

}
