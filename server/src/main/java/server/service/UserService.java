package server.service;

import commons.Board;
import commons.User;
import org.springframework.stereotype.Service;
import server.database.UserRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User connectToUser(String userName) {
        var users = repo.findAll().stream()
                .filter(user -> user.userName.equals(userName))
                .findFirst();
        if (users.isEmpty()) {
            User newUser = new User(userName);
            newUser = repo.save(newUser);
            return newUser;
        }
        return users.get();
    }

    public Map<Long, String> getBoardTitlesAndIdsByUserId(long userId) {
        Map<Long, String> map = new HashMap<>();
        User user = repo.findById(userId).get();
        for (Board board : user.boards)
            map.put(board.boardId, board.title);
        return map;
    }
}
