package com.searchvids.controller;

import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;
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
    public ResponseMessage getUserById(@PathVariable Long id) {
        return service.findUserById(id);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseMessage patchUpdateUser(@PathVariable Long id, @RequestBody User user) {
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
