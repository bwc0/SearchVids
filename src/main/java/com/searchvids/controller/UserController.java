package com.searchvids.controller;

import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {

    private UserService service;

    public UserController(UserService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable Long id) {
        return service.findUserById(id);
    }

    @GetMapping("/user/{username}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserByUsername(@PathVariable String username) {
        return service.findUserByUsername(username);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User putUpdateUser(@PathVariable Long id, @RequestBody User user) {
        return service.updateUser(id, user);
    }

    @PostMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void postVideoToUserVideoList(@PathVariable Long id, @RequestBody Video video) {
        service.addVideoToUserVideoList(id, video);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable Long id) {
        service.deleteUserById(id);
    }
}
