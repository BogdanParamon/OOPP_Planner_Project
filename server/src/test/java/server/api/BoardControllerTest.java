package server.api;

import commons.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

public class BoardControllerTest {

    private TestBoardRepository repo;
    private TestUserRepository userRepository;
    private BoardController sut;

    @BeforeEach
    public void setup() {
        repo = new TestBoardRepository();
        sut = new BoardController(repo, userRepository);
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
        sut.add(new Board("title"), 0);
        assertTrue(repo.calledMethods.contains("save"));
    }
}