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
    public String title;
    public String backgroundColor;
    public String buttonsBackground;
    public String backgroundColorFont;
    public String buttonsColorFont;
    public String boardColor;

    public String listsColor;

    public String listsFontColor;

    public String cardsBackground1, cardsBackground2,
            cardsBackground3, cardsFont1, cardsFont2, cardsFont3;

    public int currentPreset;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BoardID")
    public List<TaskList> lists = new ArrayList<>();
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "BoardId")
    public Set<Tag> tags = new HashSet<>();
    private String password;

    /**
     * Creates a new Board object with the given title and an empty set of TaskLists.
     *
     * @param title The title to be given to the new Board
     */
    public Board(String title) {
        this.title = title;
        //default colors
        this.backgroundColor = "ffffff";
        this.buttonsBackground = "ddd";
        this.backgroundColorFont = "Black";
        this.buttonsColorFont = "Black";
        this.boardColor = "ddd";
        this.listsColor = "ffffff";
        this.listsFontColor = "000000";
        this.cardsBackground1 = "ffffff";
        this.cardsBackground2 = "ffffff";
        this.cardsBackground3 = "ffffff";
        this.cardsFont1 = "000000";
        this.cardsFont2 = "000000";
        this.cardsFont3 = "000000";

        this.currentPreset = 0;
    }

    @SuppressWarnings("unused")
    public Board() {
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
        return boardId == board.boardId
                && title.equals(board.title)
                && backgroundColor.equals(board.backgroundColor)
                && buttonsBackground.equals(board.buttonsBackground)
                && backgroundColorFont.equals(board.backgroundColorFont)
                && buttonsColorFont.equals(board.buttonsColorFont)
                && boardColor.equals(board.boardColor) && listsColor.equals(board.listsColor)
                && listsFontColor.equals(board.listsFontColor)
                && cardsBackground1.equals(board.cardsBackground1)
                && cardsBackground2.equals(board.cardsBackground2)
                && cardsBackground3.equals(board.cardsBackground3)
                && cardsFont1.equals(board.cardsFont1)
                && cardsFont2.equals(board.cardsFont2) && cardsFont3.equals(board.cardsFont3)
                && Objects.equals(lists, board.lists)
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
                , buttonsColorFont, boardColor, tags, listsColor, listsFontColor
                , cardsBackground1, cardsBackground2, cardsBackground3,
                cardsFont1, cardsFont2, cardsFont3);
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

    public void setPassword(String password) {
        this.password = password;
    }

    public void setId(long id) {
        this.boardId = id;
    }
}
