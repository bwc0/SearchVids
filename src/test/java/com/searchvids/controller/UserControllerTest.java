package com.searchvids.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerTest {

    private static final Long ID = 1L;
    private static final String EMAIL = "testUser@test.com";
    private static final String USERNAME = "testU94";
    private static final String PASSWORD = "testpassword";
    private User user;
    private Video video;
    private UserController controller;

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        controller = new UserController(service);

        user = new User();
        user.setId(ID);
        user.setUsername(USERNAME);
        user.setEmail(EMAIL);
        user.setPassword(PASSWORD);

        video = new Video();
        video.setId(1L);
        video.setChannelTitle("Channel Title");
        video.setDescription("Description of video");
        video.setPublishedAt("Timed Published");
        video.setThumbnail("Thumbnail of video");
        video.setTitle("Video Title");
        video.setVideoId("video id");

        user.getVideos().add(video);

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .build();
    }

    @Test
    @DisplayName("Get /{id} endpoint status should be 200 and return User")
    void getUserById_ShouldReturnUserAnd200Test() throws Exception {
        given(service.findUserById(anyLong())).willReturn(user);

        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.username", equalTo(user.getUsername())))
                .andExpect(jsonPath("$.email", equalTo(user.getEmail())))
                .andExpect(jsonPath("$.videos", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/{id} error message and 404")
    void getTeamId_TeamDoesNotExist_ShouldReturn404() throws Exception {
        given(service.findUserById(anyLong())).willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Get /user/{username} endpoint status should be 200 and return User")
    void getUserByUsername_ShouldReturnUserAnd200Test() throws Exception {
        given(service.findUserByUsername(anyString())).willReturn(user);

        mockMvc.perform(get("/users/user/testU94")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.username", equalTo(user.getUsername())))
                .andExpect(jsonPath("$.email", equalTo(user.getEmail())))
                .andExpect(jsonPath("$.videos", hasSize(1)));
    }

    @Test
    @DisplayName("GET /user/{username} error message and 404")
    void getTeamUsername_TeamDoesNotExist_ShouldReturn404() throws Exception {
        given(service.findUserByUsername(anyString())).willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/user/testU94"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /{id} endpoint status should 200 and return User")
    void putUpdateUser_ShouldReturnUrlAnd200Test() throws Exception {
        given(service.updateUser(anyLong(), any())).willReturn(user);

        mockMvc.perform(put("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username", equalTo(user.getUsername())))
                .andExpect(jsonPath("$.email", equalTo(user.getEmail())))
                .andExpect(jsonPath("$.videos", hasSize(1)));
    }

    @Test
    @DisplayName("POST /{id} endpoint status should be 200")
    void postVideoToUserVideoList_StatusShouldBe200Test() throws Exception {
        mockMvc.perform(post("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(video)))
                .andExpect(status().isOk());

        then(service).should().addVideoToUserVideoList(anyLong(), any());
    }

    @Test
    @DisplayName("POST /{id} endpoint user does not exist status should be 404")
    void postVideoToUserVideoListUserDoesNotExist_ShouldReturn404Test() throws Exception {
        doThrow(ResourceNotFoundException.class).when(service)
                .addVideoToUserVideoList(anyLong(), any());

        mockMvc.perform(post("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(video)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /{id} endpoint Status should be 200")
    void deleteUserTest() throws Exception {
        mockMvc.perform(delete("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        then(service).should().deleteUserById(anyLong());
    }
}