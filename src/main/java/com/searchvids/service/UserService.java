package com.searchvids.service;

import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;

public interface UserService {
    ResponseMessage findUserById(Long id);
    ResponseMessage updateUser(Long id, User user);
    ResponseMessage addVideoToUserVideoList(Long id, Video video);
    ResponseMessage removeVideoFromUserVideoList(Long id, String videoId);
    void deleteUserById(Long id);
}
