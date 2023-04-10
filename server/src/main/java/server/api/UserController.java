package server.api;

import commons.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
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

    @PostMapping(path = "/verifyAdmin")
    public ResponseEntity<Boolean> verifyAdminPassword(@RequestBody String password) {
        return ResponseEntity.ok(userService.verifyAdminPassword(password));
    }

    @DeleteMapping(path = "/leave")
    public ResponseEntity<String> leaveBoard(@RequestParam long userId,
                                             @RequestParam long boardId) {
        try {
            return ResponseEntity.ok(userService.leaveBoard(userId, boardId));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @MessageMapping("/users/leave/{userId}")
    @SendTo("/topic/users/leave/{userId}")
    @Transactional
    public Long leaveMessage(long boardId, @DestinationVariable("userId") long userId) {
        return userService.leaveMessage(userId, boardId);
    }
}
