package com.searchvids.service;

import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.repository.UserRepository;
import com.searchvids.repository.VideoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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

    private User user;
    private UserService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private VideoRepository videoRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        user = new User();
        user.setId(ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        Video video = new Video();
        video.setId(VIDEO_ID);

        user.getVideos().add(video);

        service = new UserServiceImplementation(userRepository, videoRepository);
    }

    @Test
    @DisplayName("Find user by username test")
    void findUserByUsernameTest() {
        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        User data = service.findUserByUsername("testusername");

        then(userRepository).should().findByUsername(anyString());
        then(userRepository).should(never()).findAll();
        assertAll(
                () -> assertEquals(user.getId(), data.getId()),
                () -> assertEquals(user.getUsername(), data.getUsername()),
                () -> assertEquals(user.getEmail(), data.getEmail()),
                () -> assertEquals(user.getPassword(), data.getPassword()),
                () -> assertEquals(user.getVideos().size(), data.getVideos().size())
        );
    }

    @Test
    @DisplayName("User not found by username exception test")
    void userNotFoundByUsernameExceptionTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class, () -> service.findUserByUsername("testusername"));

        assertEquals("User not found with username: 'testusername'", ex.getMessage());
    }

    @Test
    @DisplayName("Find user by id test")
    void findUserByIdTest() {
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        User data = service.findUserById(1L);

        then(userRepository).should().findById(anyLong());
        then(userRepository).should(never()).findAll();
        assertAll(
                () -> assertEquals(user.getId(), data.getId()),
                () -> assertEquals(user.getUsername(), data.getUsername()),
                () -> assertEquals(user.getEmail(), data.getEmail()),
                () -> assertEquals(user.getPassword(), data.getPassword()),
                () -> assertEquals(user.getVideos().size(), data.getVideos().size())
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
        User updateData = new User();
        updateData.setUsername(USERNAME);
        updateData.setEmail("newEmail@email.com");
        updateData.setPassword(PASSWORD);

        given(userRepository.save(any(User.class))).willReturn(updateData);

        User updateUser = service.updateUser(1L, user);

        then(userRepository).should().save(any(User.class));
        assertEquals(updateData.getId(), updateUser.getId());
        assertEquals(updateData.getEmail(), updateUser.getEmail());
    }

    @Test
    @DisplayName("Add new video to User Video List Test")
    void addNewVideoIfVideoIdDoesNotExistInDataBaseToUserVideoListTest() {
        Video video = new Video();
        video.setId(2L);

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.empty());

        service.addVideoToUserVideoList(1L, video);

        then(userRepository).should().findById(anyLong());
        then(videoRepository).should().save(any());
        then(userRepository).should().save(any());
        assertEquals(2, user.getVideos().size());
    }

    @Test
    @DisplayName("Add existing video to User Video List Test")
    void addVideoToUserVideoListTest() {
        Video video = new Video();
        video.setId(2L);
        video.setVideoId("mnjokmojn");

        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));
        given(videoRepository.findByVideoId(anyString())).willReturn(Optional.of(video));

        service.addVideoToUserVideoList(1L, video);

        then(userRepository).should().findById(anyLong());
        then(videoRepository).should(never()).save(any());
        then(userRepository).should().save(any());
        assertEquals(2, user.getVideos().size());
    }

    @Test
    @DisplayName("User not found exception video not added to list test")
    void userNotFoundExceptionTest() {
        Video video = new Video();
        video.setId(2L);

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
}