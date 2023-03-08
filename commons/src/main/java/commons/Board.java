package commons;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long boardId;
    private String title;

    @OneToMany(mappedBy = "board")
    private Set<TaskList> lists;

    @OneToMany(mappedBy = "board")
    private Set<Task> tasks;
}
