package commons;

import javax.persistence.*;
import java.util.Set;

@Entity
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long listId;
    private String title;

    @ManyToOne
    @JoinColumn(name = "boardId")
    private Board board;

    @OneToMany(mappedBy = "list")
    private Set<Task> tasks;
}
