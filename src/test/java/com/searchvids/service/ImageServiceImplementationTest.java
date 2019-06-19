package com.searchvids.service;

import com.searchvids.model.Role;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.model.Video;
import com.searchvids.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

class ImageServiceImplementationTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "testusername";
    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "testpassword";
    private User user;
    private MultipartFile file;

    @Mock
    private UserRepository repository;
    private ImageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new ImageServiceImplementation(repository);
        Set<Role> roles = Stream.of(new Role(RoleName.ROLE_USER)).collect(Collectors.toSet());
        Set<Video> videos = Stream.of(new Video()).collect(Collectors.toSet());

        user = new User(USERNAME, EMAIL, PASSWORD, roles, videos);
        user.setId(ID);

        file = new MockMultipartFile("image", "testing.txt",
                "text/plain", "RecipeImage".getBytes());
    }

    @Test
    @DisplayName("Upload image success test")
    void uploadImageFileSuccessTest() throws Exception {
        given(repository.findById(anyLong())).willReturn(Optional.of(user));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        service.uploadImageFile(1L, file);

        then(repository).should().save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();
        assertEquals(file.getBytes().length, savedUser.getImage().length);
    }
}