package com.searchvids.service;

import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.repository.UserRepository;
import com.searchvids.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class UserServiceImplementationTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "testusername";
    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "testpassword";
    private static final Long VIDEO_ID = 1L;
    private static final String VIDEOID = "video id";

    private User user;
    private UserService service;
    private Video video;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoRepository videoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        video = new Video();
        video.setId(VIDEO_ID);


        user.getVideos().add(video);

        service = new UserServiceImplementation(userRepository, videoRepository, passwordEncoder);
    }

    @Test
    @DisplayName("Find user by id test")
    void findUserByIdTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        ResponseMessage data = service.findUserById(1L);

        then(userRepository).should().findById(anyLong());
        then(userRepository).should(never()).findAll();
        assertAll(
                () -> assertEquals("User found with id: 1", data.getMessage()),
                () -> assertEquals(HttpStatus.OK.getReasonPhrase(), data.getStatus()),
                () -> assertEquals(user.getId(), data.getUser().getId()),
                () -> assertEquals(user.getUsername(), data.getUser().getUsername()),
                () -> assertEquals(user.getEmail(), data.getUser().getEmail()),
                () -> assertEquals(user.getPassword(), data.getUser().getPassword()),
                () -> assertEquals(user.getVideos().size(), data.getUser().getVideos().size())
        );
    }

    @Test
    @DisplayName("User not found by Id exception test")
    void userNotFoundByIdExceptionTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class, () -> service.findUserById(1L));

        assertEquals("User not found with id: '1'", ex.getMessage());
    }

    @Test
    @DisplayName("Update user test")
    void updateUserTest() {
        User data = new User();
        data.setUsername("newusername");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(userRepository.save(any())).willReturn(user);
        given(userRepository.existsByUsername(anyString())).willReturn(false);

        ResponseMessage message = service.updateUser(1L, data);

        then(userRepository).should().findById(anyLong());
        then(userRepository).should().save(any());
        assertNotNull(message);
        assertEquals(data.getUsername(), message.getUser().getUsername());
    }

    @Test
    @DisplayName("Updated username already exists")
    void updateUserUsernameAlreadyExists() {
        User data = new User();
        data.setUsername("newusername");

        given(userRepository.existsByUsername(anyString())).willReturn(true);

        ResponseMessage message = service.updateUser(1L, data);

        then(userRepository).should(never()).findById(anyLong());
        then(userRepository).should(never()).save(any());
        assertEquals("Username must be unique", message.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), message.getStatus());
    }

    @Test
    @DisplayName("User not found by id exception test")
    void updateUserNotFoundByIdExceptionTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class, () -> service.updateUser(1L, user));

        then(userRepository).should().findById(anyLong());
        then(userRepository).should(never()).save(any());
        assertEquals("User not found with id: '1'", ex.getMessage());
    }

    @Test
    @DisplayName("Add new video to User Video List Test")
    void addNewVideoIfVideoIdDoesNotExistInDataBaseToUserVideoListTest() {
        Video video = new Video();
        video.setId(2L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.empty());

        ResponseMessage message = service.addVideoToUserVideoList(1L, video);

        then(userRepository).should().findById(anyLong());
        then(videoRepository).should().save(any());
        then(userRepository).should().save(any());
        assertAll(
                () -> assertEquals("Video added to favorites", message.getMessage()),
                () -> assertEquals(HttpStatus.OK.getReasonPhrase(), message.getStatus()),
                () -> assertEquals(2, message.getUser().getVideos().size())
        );
    }

    @Test
    @DisplayName("Add existing video to User Video List Test")
    void addVideoToUserVideoListTest() {
        Video video2 = new Video();
        video2.setId(2L);
        video2.setVideoId("mnjokmojn");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.of(video2));

        ResponseMessage message = service.addVideoToUserVideoList(1L, video2);

        then(userRepository).should().findById(anyLong());
        then(videoRepository).should(never()).save(any());
        then(userRepository).should().save(any());
        assertAll(
                () -> assertEquals("Video added to favorites", message.getMessage()),
                () -> assertEquals(HttpStatus.OK.getReasonPhrase(), message.getStatus()),
                () -> assertEquals(2, message.getUser().getVideos().size())
        );
    }

    @Test
    @DisplayName("User not found exception video not added to list test")
    void userNotFoundException_VideoNotAddedToListTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class, () -> service.addVideoToUserVideoList(1L, video));

        assertEquals("User not found with id: '1'", ex.getMessage());
    }

    @Test
    @DisplayName("Delete user by id test")
    void deleteUserByIdTest() {
        service.deleteUserById(1L);
        then(userRepository).should().deleteById(anyLong());
    }

    @Test
    @DisplayName("User found video removed from list test")
    void removeVideoFromUserVideoListTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.of(video));

        ResponseMessage message = service.removeVideoFromUserVideoList(1L, VIDEOID);

        then(userRepository).should().findById(anyLong());
        then(videoRepository).should().findByVideoId(anyString());
        then(userRepository).should().save(any());

        assertAll(
                () -> assertEquals("Video removed from favorites", message.getMessage()),
                () -> assertEquals(HttpStatus.OK.getReasonPhrase(), message.getStatus())
        );
    }

    @Test
    @DisplayName("User not found exception video not removed from list test")
    void removeVideoFromUserVideo_UserNotFoundExceptionListTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class,
                () -> service.removeVideoFromUserVideoList(1L, video.getVideoId()));

        then(userRepository).should(never()).save(any());
        then(videoRepository).should(never()).findByVideoId(anyString());
        assertEquals("User not found with id: '1'", ex.getMessage());
    }

    @Test
    @DisplayName("User not found exception video not removed from list test")
    void removeVideoFromUserVideo_VideoNotFoundExceptionListTest() {
        video.setVideoId(VIDEOID);
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class,
                () -> service.removeVideoFromUserVideoList(1L, video.getVideoId()));

        then(userRepository).should(never()).save(any());
        assertEquals("Video not found with videoid: 'video id'", ex.getMessage());
    }

}