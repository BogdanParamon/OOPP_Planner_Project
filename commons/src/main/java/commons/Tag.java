package commons;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Objects;

@Entity
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long tagId;
    private String text;
    private String color;

    public Tag() { }

    public Tag(String text, String color) {
        this.text = text;
        this.color = color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tag tag = (Tag) o;

        if (tagId != tag.tagId) return false;
        if (!Objects.equals(text, tag.text)) return false;
        return Objects.equals(color, tag.color);
    }

    @Override
    public int hashCode() {
        int result = (int) (tagId ^ (tagId >>> 32));
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (color != null ? color.hashCode() : 0);
        return result;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
