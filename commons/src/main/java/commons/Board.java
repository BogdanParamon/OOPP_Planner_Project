package commons;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long boardId;
    private String title;

    @OneToMany(mappedBy = "board")
    private Set<TaskList> lists;

    public Board(String title) {
        this.title = title;
        lists = new HashSet<TaskList>();
    }

    public Board() {
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

    public long getBoardId() {
        return boardId;
    }

    public void setBoardId(long boardId) {
        this.boardId = boardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Set<TaskList> getLists() {
        return lists;
    }

    public void setLists(Set<TaskList> lists) {
        this.lists = lists;
    }
}
