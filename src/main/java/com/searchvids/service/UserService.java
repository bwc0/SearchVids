package com.searchvids.service;

import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;

public interface UserService {
    ResponseMessage findUserById(Long id);
    ResponseMessage updateUser(Long id, User user);
    void addVideoToUserVideoList(Long id, Video video);
    void removeVideoFromUserVideoList(Long id, String videoId);
    void deleteUserById(Long id);
}
