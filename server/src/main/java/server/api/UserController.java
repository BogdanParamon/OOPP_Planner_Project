package server.api;

import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import server.service.UserService;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/{userName}")
    public ResponseEntity<User> connectToUser(@PathVariable("userName") String userName) {
        return ResponseEntity.ok(userService.connectToUser(userName));
    }

    @GetMapping(path = "/{userId}/boardTitles&Ids")
    public ResponseEntity<Map<Long, String>> getBoardTitlesAndIdsByUserId(@PathVariable("userId")
                                                                              long userId) {
        return ResponseEntity.ok(userService.getBoardTitlesAndIdsByUserId(userId));
    }
}
