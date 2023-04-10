package server.service;

import commons.Board;
import commons.User;
import org.springframework.stereotype.Service;
import server.database.UserRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User connectToUser(String userName) {
        var users = userRepository.findAll().stream()
                .filter(user -> user.userName.equals(userName))
                .findFirst();
        if (users.isEmpty()) {
            User newUser = new User(userName);
            newUser = userRepository.save(newUser);
            return newUser;
        }
        return users.get();
    }

    public Map<Long, String> getBoardTitlesAndIdsByUserId(long userId) {
        Map<Long, String> map = new HashMap<>();
        User user = userRepository.findById(userId).get();
        for (Board board : user.boards)
            map.put(board.boardId, board.title);
        return map;
    }

    public Boolean verifyAdminPassword(String password) {
        return Objects.equals(password, "admin");
    }


    public String leaveBoard(long userId, long boardId) throws IllegalArgumentException {
        if (!userRepository.existsById(userId))
            throw new IllegalArgumentException();

        User user = userRepository.findById(userId).get();
        user.boards.removeIf(board -> board.boardId == boardId);
        userRepository.save(user);
        return "Successful";
    }

    public Long leaveMessage(long userId, long boardId) {
        leaveBoard(userId, boardId);
        return boardId;
    }
}
