package server.api;

import commons.Board;
import commons.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BoardControllerTest {

    private TestBoardRepository repo;

    private TestUserRepository userRepository;
    private TestTagRepository tagRepo;
    private BoardController sut;

    @BeforeEach
    public void setup() {
        repo = new TestBoardRepository();
        userRepository = new TestUserRepository();
        tagRepo = new TestTagRepository();
        sut = new BoardController(repo, userRepository, tagRepo);

    }

    @Test
    public void cannotAddNullBoard() {
        var actual = sut.add(null, 0);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void cannotAddUntitledBoard() {
        var actual = sut.add(new Board(""), 0);
        assertEquals(BAD_REQUEST, actual.getStatusCode());
    }

    @Test
    public void databaseIsUsed() {
        userRepository.save(new User("user 0"));
        sut.add(new Board("title"), 0);
        assertTrue(repo.calledMethods.contains("save"));
    }
}