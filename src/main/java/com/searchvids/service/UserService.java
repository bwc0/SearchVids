package com.searchvids.service;

import com.searchvids.model.User;
import com.searchvids.model.Video;

public interface UserService {
    User findUserByUsername(String username);
    User findUserById(Long id);
    User updateUser(Long id, User user);
    void addVideoToUserVideoList(Long id, Video video);
    void deleteUserById(Long id);
}
