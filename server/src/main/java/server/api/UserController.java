package server.api;

import commons.Board;
import commons.User;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.database.BoardRepository;
import server.database.UserRepository;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserRepository repo;
    private BoardRepository boardRepo;

    public UserController(UserRepository repo, BoardRepository boardRepo) {
        this.repo = repo;
        this.boardRepo = boardRepo;
    }

    @GetMapping("/{userName}")
    public ResponseEntity<User> connectToUser(@PathVariable("userName") String userName) {
        var users = repo.findAll().stream()
                .filter(user -> user.userName.equals(userName))
                .findFirst();
        if (users.isEmpty()) {
            User newUser = new User(userName);
            newUser = repo.save(newUser);
            return ResponseEntity.ok(newUser);
        }
        return ResponseEntity.ok(users.get());
    }

    @GetMapping(path = "/{userId}/boardTitles&Ids")
    public ResponseEntity<Map<Long, String>> getBoardTitlesAndIdsByUserId(@PathVariable("userId")
                                                                              long userId) {
        Map<Long, String> map = new HashMap<>();
        User user = repo.findById(userId).get();
        for (Board board : user.boards)
            map.put(board.boardId, board.title);
        return ResponseEntity.ok(map);
    }
}
