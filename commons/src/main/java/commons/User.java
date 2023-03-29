package commons;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    public long userId;

    public String userName;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "UserID")
    public List<Board> boards = new ArrayList<>();

    public User(String userName) {
        this.userName = userName;
    }

    //unused
    private User() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userId == user.userId && userName.equals(user.userName) && Objects.equals(boards, user.boards);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, boards);
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
