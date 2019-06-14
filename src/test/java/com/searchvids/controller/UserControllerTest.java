package com.searchvids.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchvids.controller.ExceptionHandler.RestExceptionHandler;
import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
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
    private ResponseMessage message1;
    private ResponseMessage message2;
    private ResponseMessage message3;

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

        message1 = new ResponseMessage("User found with id: " + user.getId(), HttpStatus.OK.getReasonPhrase(), user);
        message2 = new ResponseMessage("User updated with id: " + user.getId(), HttpStatus.OK.getReasonPhrase(), user);
        message3 = new ResponseMessage("Username must be unique", HttpStatus.BAD_REQUEST.getReasonPhrase());

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new RestExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("Get /{id} endpoint status should be 200 and return User")
    void getUserById_ShouldReturnUserAnd200Test() throws Exception {
        given(service.findUserById(anyLong())).willReturn(message1);

        mockMvc.perform(get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(jsonPath("$.message", equalTo("User found with id: " + user.getId())))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.OK.getReasonPhrase())))
                .andExpect(jsonPath("$.user.username", equalTo(user.getUsername())))
                .andExpect(jsonPath("$.user.email", equalTo(user.getEmail())))
                .andExpect(jsonPath("$.user.videos", hasSize(1)));
    }

    @Test
    @DisplayName("GET /users/{id} error message and 404")
    void getUserId_UserDoesNotExist_ShouldReturn404() throws Exception {
        given(service.findUserById(anyLong())).willThrow(ResourceNotFoundException.class);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("PATCH /{id} endpoint status should 200 and return User")
    void putUpdateUser_ShouldReturnUrlAnd200Test() throws Exception {
        given(service.updateUser(anyLong(), any())).willReturn(message2);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("User updated with id: 1")))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.OK.getReasonPhrase())))
                .andExpect(jsonPath("$.user.username", equalTo(user.getUsername())))
                .andExpect(jsonPath("$.user.email", equalTo(user.getEmail())))
                .andExpect(jsonPath("$.user.videos", hasSize(1)));
    }

    @Test
    @DisplayName("PATCH /{id} endpoint username Exists should return 400")
    void putUpdateUser_UsernameExistsShouldReturn400Test() throws Exception {
        given(service.updateUser(anyLong(), any())).willReturn(message3);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Username must be unique")))
                .andExpect(jsonPath("$.status", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase())));
    }

    @Test
    @DisplayName("PATCH /{id} endpoint error message and 404")
    void putUpdateUser_UserDoesNotExist_ShouldReturn404() throws Exception {
        given(service.updateUser(anyLong(), any())).willThrow(ResourceNotFoundException.class);

        mockMvc.perform(patch("/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(user)))
                .andExpect(status().isNotFound());
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