package com.searchvids.service;

import com.searchvids.exception.FileStorageException;
import com.searchvids.exception.ResourceNotFoundException;
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

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

class ImageStorageServiceImplementationTest {

    private static final Long ID = 1L;
    private static final String USERNAME = "testusername";
    private static final String EMAIL = "test@email.com";
    private static final String PASSWORD = "testpassword";
    private User user;
    private MultipartFile goodFile;
    private MultipartFile badFile;

    @Mock
    private UserRepository repository;
    private ImageStorageService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        service = new ImageStorageServiceImplementation(repository);
        Set<Role> roles = Stream.of(new Role(RoleName.ROLE_USER)).collect(Collectors.toSet());
        Set<Video> videos = Stream.of(new Video()).collect(Collectors.toSet());

        user = new User(USERNAME, EMAIL, PASSWORD, roles, videos);
        user.setId(ID);

        goodFile = new MockMultipartFile("image", "testing.txt",
                "text/plain", "RecipeImage".getBytes());

        badFile = new MockMultipartFile("image", "testing..txt",
                "text/plain", "RecipeImage".getBytes());
    }

    @Test
    @DisplayName("Upload image success test")
    void storeImageFileSuccessfullyTest() throws Exception {
        given(repository.findById(anyLong())).willReturn(Optional.of(user));

        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);

        service.storeImage(1L, goodFile);

        then(repository).should().save(argumentCaptor.capture());
        User savedUser = argumentCaptor.getValue();
        assertEquals(goodFile.getBytes().length, savedUser.getImageDetails().getData().length);
    }

    @Test
    @DisplayName("Upload image failed test")
    void storeImageFileFailedTest() throws Exception {
        given(repository.findById(anyLong())).willReturn(Optional.of(user));

        Throwable ex = assertThrows(FileStorageException.class, () -> service.storeImage(1L, badFile));

        then(repository).should(never()).save(any());
        assertEquals("Could not store file " + badFile.getOriginalFilename() + " please try again",
                ex.getMessage());
    }

    @Test
    @DisplayName("Upload image user not found")
    void storeImageFileFailedUserNotFoundTest() throws Exception {
        given(repository.findById(anyLong())).willReturn(Optional.empty());

        Throwable ex = assertThrows(ResourceNotFoundException.class, () -> service.storeImage(1L, goodFile));

        assertEquals("User not found with id: '1'", ex.getMessage());
    }
}