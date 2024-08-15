package org.vashishth.ratelimiter.tokenbucket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("user")
public class UserController {
    @Autowired
    UserService userService;

    @PostMapping("register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterUserRequest registerUserRequest) {
        return ResponseEntity.ok(userService.registerUser(registerUserRequest));
    }

    @GetMapping("busywait")
    public ResponseEntity<?> busyWait(@RequestParam Integer uid, @RequestParam long timespan) throws InterruptedException {
        return ResponseEntity.ok(userService.performBusyWait(uid, timespan));
    }
}
