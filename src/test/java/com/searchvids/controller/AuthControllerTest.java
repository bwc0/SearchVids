package com.searchvids.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.searchvids.model.payload.JwtResponse;
import com.searchvids.model.payload.LoginForm;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.SignUpForm;
import com.searchvids.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collection;
import java.util.HashSet;

import static com.searchvids.controller.AuthController.AUTH_API;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private static final String FIRSTNAME = "Test";
    private static final String LASTNAME = "User";
    private static final String EMAIL = "testUser@test.com";
    private static final String USERNAME = "testU94";
    private static final String PASSWORD = "testpassword";
    private static final Collection<? extends GrantedAuthority> authorities = new HashSet<>();
    private LoginForm loginForm;
    private SignUpForm signUpForm;
    private MockMvc mockMvc;
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        authController = new AuthController(authService);

        loginForm = new LoginForm();
        loginForm.setPassword(PASSWORD);
        loginForm.setUsername(USERNAME);

        signUpForm = new SignUpForm();
        signUpForm.setEmail(EMAIL);
        signUpForm.setUsername(USERNAME);
        signUpForm.setPassword(PASSWORD);

        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .build();
    }

    @Test
    @DisplayName("POST /signin endpoint test should return 200 and JWTResponse")
    void authenticateUser_ShouldReturn_200_AndJWTResponse() throws Exception {
        given(authService.authentication(any(LoginForm.class)))
                .willReturn(new JwtResponse("token", USERNAME, authorities));

        mockMvc.perform(post(AUTH_API + "/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(loginForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token", equalTo("token")))
                .andExpect(jsonPath("$.username", equalTo(USERNAME)))
                .andExpect(jsonPath("$.type", equalTo("Bearer")))
                .andExpect(jsonPath("$.authorities", hasSize(0)));
    }

    @Test
    @DisplayName("POST /signup endpoint test should return 200 and JWTResponse")
    void registerUser_ShouldReturn200_AndJwtResponse() throws Exception {
        given(authService.registration(any(SignUpForm.class)))
                .willReturn(new ResponseMessage("User registered successfully!", HttpStatus.OK));

        mockMvc.perform(post(AUTH_API + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("User registered successfully!")))
                .andExpect(jsonPath("$.status", equalTo("OK")));
    }

    @Test
    @DisplayName("POST /signup endpoint test should return 400 and JWTResponse")
    void registerUser_UsernameExists_ShouldReturn400_AndJwtResponse() throws Exception {
        given(authService.registration(any(SignUpForm.class)))
                .willReturn(new ResponseMessage("Fail -> Username already taken", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(AUTH_API + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Fail -> Username already taken")))
                .andExpect(jsonPath("$.status", equalTo("BAD_REQUEST")));

    }

    @Test
    @DisplayName("POST /signup endpoint test should return 400 and JWTResponse")
    void registerUser_EmailExists_ShouldReturn400_AndJwtResponse() throws Exception {
        given(authService.registration(any(SignUpForm.class)))
                .willReturn(new ResponseMessage("Fail -> Email already taken", HttpStatus.BAD_REQUEST));

        mockMvc.perform(post(AUTH_API + "/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(signUpForm)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", equalTo("Fail -> Email already taken")))
                .andExpect(jsonPath("$.status", equalTo("BAD_REQUEST")));
    }
}