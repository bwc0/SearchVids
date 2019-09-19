package com.searchvids.controller;

import com.searchvids.controller.ExceptionHandler.RestExceptionHandler;
import com.searchvids.exception.FileStorageException;
import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.*;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.UploadFileResponse;
import com.searchvids.service.ImageStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ImageStorageControllerTest {

    @Mock
    private ImageStorageService service;

    private ImageStorageController controller;
    private MockMvc mvc;
    private ResponseMessage message;
    private MockMultipartFile file;
    private User user;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        controller = new ImageStorageController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice
                (new RestExceptionHandler())
                .build();

        file = new MockMultipartFile("imageFile", "testing.txt",
                "text/plain", "UserImage".getBytes());

        Set<Role> roles = Stream.of(new Role(RoleName.ROLE_USER)).collect(Collectors.toSet());
        Set<Video> videos = Stream.of(new Video()).collect(Collectors.toSet());

        byte[] image = new byte[file.getBytes().length];
        int i = 0;

        for (byte b : file.getBytes()) {
            image[i++] = b;
        }

        Image userImage = new Image(file.getOriginalFilename(), file.getContentType(), image);

        user = new User("username", "test@test.com", "password", roles, videos);
        user.setId(1L);
        user.setImageDetails(userImage);

        message = new ResponseMessage("Image uploaded successfully", HttpStatus.OK.getReasonPhrase(), user);
    }

    @Test
    @DisplayName("POST /users/{id}/image endpoint test")
    void uploadImageSuccessfulTest() throws Exception {
        UploadFileResponse fileResponse = new UploadFileResponse();

        fileResponse.setFileName("testing.txt");
        fileResponse.setFileDownloadUri("http://localhost/downloadFile/testing.txt");
        fileResponse.setFileType("text/plain");
        fileResponse.setSize(9L);
        fileResponse.setUserId(1L);

        given(service.storeImage(anyLong(), any())).willReturn(message);

        mvc.perform(multipart("/users/1/image").file(file))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fileName", equalTo(fileResponse.getFileName())))
                .andExpect(jsonPath("$.fileDownloadUri", equalTo(fileResponse.getFileDownloadUri())))
                .andExpect(jsonPath("$.fileType", equalTo(fileResponse.getFileType())))
                .andExpect(jsonPath("$.size", equalTo(fileResponse.getSize().intValue())))
                .andExpect(jsonPath("$.userId", equalTo(fileResponse.getUserId().intValue())));

        then(service).should().storeImage(anyLong(), any());
    }

    @Test
    @DisplayName("POST /users/{id}/image endpoint User not found test")
    void uploadImageUserNotFoundFailureTest() throws Exception {
        given(service.storeImage(anyLong(), any())).willThrow(ResourceNotFoundException.class);

        mvc.perform(multipart("/users/1/image").file(file))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status",
                        equalTo(HttpStatus.NOT_FOUND.getReasonPhrase())))
                .andDo(print());
    }

    @Test
    @DisplayName("POST /users/{id}/image endpoint User not found test")
    void uploadImageFileFailureFailureTest() throws Exception {
        given(service.storeImage(anyLong(), any())).willThrow(FileStorageException.class);

        mvc.perform(multipart("/users/1/image").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status",
                        equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase())))
                .andDo(print());
    }
}