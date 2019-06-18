package com.searchvids.service;

import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.repository.UserRepository;
import com.searchvids.repository.VideoRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImplementation implements UserService {

    private UserRepository userRepository;
    private VideoRepository videoRepository;
    private PasswordEncoder passwordEncoder;

    public UserServiceImplementation(UserRepository userRepository, VideoRepository videoRepository,
                                     PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.videoRepository = videoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public ResponseMessage findUserById(Long id) {
        return new ResponseMessage("User found with id: " + id, HttpStatus.OK.getReasonPhrase(), getUser(id));
    }

    @Override
    public ResponseMessage updateUser(Long id, User user) {

        if (userRepository.existsByUsername(user.getUsername())) {
            return new ResponseMessage("Username must be unique", HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

       User updatedUser = userRepository.findById(id).map(data -> {

           if (user.getUsername() != null) {
               data.setUsername(user.getUsername());
           }

            if (user.getPassword() != null) {
                data.setPassword(passwordEncoder.encode(user.getPassword()));
            }

            return userRepository.save(data);
        }).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));

        return new ResponseMessage("User updated with id: " + id, HttpStatus.OK.getReasonPhrase(), updatedUser);
    }

    @Override
    public void addVideoToUserVideoList(Long id, Video video) {
        User user = getUser(id);

        Optional<Video> videoOptional = videoRepository.findByVideoId(video.getVideoId());

        if (!videoOptional.isPresent()) {
            videoRepository.save(video);
            user.getVideos().add(video);
        } else {
            user.getVideos().add(videoOptional.get());
        }

        userRepository.save(user);
    }

    @Override
    public void removeVideoFromUserVideoList(Long id, String videoId) {
        User user = getUser(id);

        user.getVideos().remove(videoRepository.findByVideoId(videoId)
                .orElseThrow(() -> new ResourceNotFoundException("Video", "videoid", videoId)));

        userRepository.save(user);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    private User getUser(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }
}
