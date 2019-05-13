package com.searchvids.service;

import com.searchvids.model.Role;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.SignUpForm;
import com.searchvids.repository.RoleRepository;
import com.searchvids.repository.UserRepository;
import com.searchvids.service.security.jwt.JwtProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class AuthServiceImplementationTest {

    private static final String EMAIL = "testUser@test.com";
    private static final String USERNAME = "testU94";
    private static final String PASSWORD = "testpassword";
    private static final Set<String> roles = new HashSet<>();
    private AuthService service;
    private SignUpForm signUpForm;
    private User user;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private AuthenticationManager manager;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtProvider provider;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        signUpForm = new SignUpForm();
        signUpForm.setEmail(EMAIL);
        signUpForm.setUsername(USERNAME);
        signUpForm.setPassword(PASSWORD);

        roles.add("user");
        signUpForm.setRole(roles);

        user = new User();

        user.setUsername(signUpForm.getUsername());
        user.setEmail(signUpForm.getEmail());
        user.setPassword(signUpForm.getPassword());

        service = new AuthServiceImplementation(manager, userRepository,
                roleRepository, encoder, provider);
    }

    @AfterEach
    void tearDown() {
        roles.remove("user");
        roles.remove("admin");
    }

    @Test
    @DisplayName("Successful registration Test Should Return ResponseEntity and 200")
    void registrationUser_ShouldReturn_ResponseEntity_And200() {

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(roleRepository.findByName(any())).willReturn(Optional.of(new Role(RoleName.ROLE_USER)));
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message = service.registration(signUpForm);

        assertEquals("User registered successfully!", message.getMessage());
        assertEquals(HttpStatus.OK, message.getStatus());
    }


    @Test
    @DisplayName("Failed registration, Username already Exist, Should Return ResponseEntity")
    void registration_UsernameExist_ShouldReturn_ResponseEntity() {

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(true);
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message =  service.registration(signUpForm);

        assertEquals("Fail -> Username already taken", message.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, message.getStatus());
    }

    @Test
    @DisplayName("Failed registration, Email already exist, Should Return ResponseEntity")
    void registration_EmailExist_ShouldReturn_ResponseEntity() {

        given(userRepository.existsByEmail(anyString())).willReturn(true);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message = service.registration(signUpForm);

        assertEquals("Fail -> Email already taken", message.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, message.getStatus());
    }
}