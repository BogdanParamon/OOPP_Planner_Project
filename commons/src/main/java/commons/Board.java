package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.util.*;

import static org.apache.commons.lang3.builder.ToStringStyle.MULTI_LINE_STYLE;

@Entity
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long boardId;

    private String password;

    public String title;

    public String backgroundColor;

    public String buttonsBackground;

    public String backgroundColorFont;

    public String buttonsColorFont;

    public String boardColor;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BoardID")
    public List<TaskList> lists = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BoardId")
    public Set<Tag> tags = new HashSet<>();

    /**
     * Creates a new Board object with the given title and an empty set of TaskLists.
     *
     * @param title The title to be given to the new Board
     * @param password Password of the board
     */
    public Board(String title, String password) {
        this.title = title;
        this.password = password;
        //default colors
        this.backgroundColor = "ffffff";
        this.buttonsBackground = "ddd";
        this.backgroundColorFont = "Black";
        this.buttonsColorFont = "Black";
        this.boardColor = "ddd";
    }

    @SuppressWarnings("unused")
    private Board() {
        // For object mapper
    }

    /**
     * Compares this object to another object and returns true only if they are equal.
     * Two Boards are equal if they have the same boardId, title and set of lists.
     *
     * @param o The object that is going to be compared to this
     * @return True if and only if the other object is a Board with the same attributes
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return boardId == board.boardId && Objects.equals(title, board.title)
                && Objects.equals(lists, board.lists)
                && Objects.equals(backgroundColor, board.backgroundColor)
                && Objects.equals(buttonsBackground, board.buttonsBackground)
                && Objects.equals(backgroundColorFont, board.backgroundColorFont)
                && Objects.equals(buttonsColorFont, board.buttonsColorFont)
                && Objects.equals(boardColor, board.boardColor)
                && Objects.equals(tags, board.tags);
    }

    /**
     * Generates a hash code for this object,
     * which uses the boardId, title and lists attributes.
     *
     * @return The generated hash code for this object
     */
    @Override
    public int hashCode() {
        return Objects.hash(boardId, title, backgroundColor
                , buttonsBackground, lists, backgroundColorFont
                , buttonsColorFont, boardColor, tags);
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

    /**
     * Adds a TaskList to the set of all TaskLists on this Board
     *
     * @param list The TaskList to be added to this Board
     */
    public void addList(TaskList list) {
        lists.add(list);
    }

    public void addTag(Tag tag) {
        tags.add(tag);
    }

    public String getPassword() {
        return password;
    }
}
