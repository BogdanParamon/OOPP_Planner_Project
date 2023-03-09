package commons;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long boardId;
    public String title;

    @OneToMany(mappedBy = "board")
    public Set<TaskList> lists;

    public Board(String title) {
        this.title = title;
        lists = new HashSet<TaskList>();
    }

    private Board() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return boardId == board.boardId && Objects.equals(title, board.title) && Objects.equals(lists, board.lists);
    }

    @Override
    public int hashCode() {
        return Objects.hash(boardId, title, lists);
    }

    public void addList(TaskList list){
        lists.add(list);
    }
}
