package com.searchvids.service;

import com.searchvids.model.Role;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.model.payload.JwtResponse;
import com.searchvids.model.payload.LoginForm;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.SignUpForm;
import com.searchvids.repository.RoleRepository;
import com.searchvids.repository.UserRepository;
import com.searchvids.service.security.UserPrincipal;
import com.searchvids.service.security.jwt.JwtProvider;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class AuthServiceImplementationTest {

    private static final String EMAIL = "testUser@test.com";
    private static final String USERNAME = "testU94";
    private static final String PASSWORD = "testpassword";
    private static final Set<String> roles = new HashSet<>();
    private AuthService service;
    private SignUpForm signUpForm;
    private LoginForm loginForm;
    private User user;
    private JwtResponse response;
    private Authentication authentication;
    private String token;

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

        loginForm = new LoginForm();

        loginForm.setUsername(USERNAME);
        loginForm.setPassword(PASSWORD);

        roles.add("user");
        signUpForm.setRole(roles);

        user = new User();

        user.setUsername(signUpForm.getUsername());
        user.setEmail(signUpForm.getEmail());
        user.setPassword(signUpForm.getPassword());

        authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword())
        );

        service = new AuthServiceImplementation(manager, userRepository,
                roleRepository, encoder, provider);
    }

    @AfterEach
    void tearDown() {
        roles.remove("user");
        roles.remove("admin");
    }

    @Test
    @Disabled
    @DisplayName("Successful authentication")
    void authentication_SuccessfulAuthentication() {
        given(manager.authenticate(any())).willReturn(authentication);

        Calendar calendar = Calendar.getInstance();

        token = Jwts.builder()
                .setSubject(USERNAME)
                .setIssuedAt(calendar.getTime())
                .setExpiration(new Date((new Date()).getTime() + 86400*1000))
                .signWith(SignatureAlgorithm.HS512, "clientDevSecret")
                .compact();

        given(provider.generateJwtToken(authentication)).willReturn(token);

        given(userRepository.findByUsername(anyString())).willReturn(Optional.of(user));

        JwtResponse jwtResponse = service.authentication(loginForm);

    }

    @Test
    @DisplayName("Successful registration Test Should Return ResponseEntity and 200")
    void registrationUser_ShouldReturn_ResponseEntity_And200() {

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(roleRepository.findByName(any())).willReturn(Optional.of(new Role(RoleName.ROLE_USER)));
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message = service.registration(signUpForm);

        assertEquals("User registered successfully! Please log in", message.getMessage());
        assertEquals(HttpStatus.OK.getReasonPhrase(), message.getStatus());
    }


    @Test
    @DisplayName("Failed registration, Username already Exist, Should Return ResponseEntity")
    void registration_UsernameExist_ShouldReturn_ResponseEntity() {

        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(userRepository.existsByUsername(anyString())).willReturn(true);
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message =  service.registration(signUpForm);

        assertEquals("Username already taken", message.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), message.getStatus());
    }

    @Test
    @DisplayName("Failed registration, Email already exist, Should Return ResponseEntity")
    void registration_EmailExist_ShouldReturn_ResponseEntity() {

        given(userRepository.existsByEmail(anyString())).willReturn(true);
        given(userRepository.existsByUsername(anyString())).willReturn(false);
        given(userRepository.save(any())).willReturn(user);

        ResponseMessage message = service.registration(signUpForm);

        assertEquals("Email already taken", message.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST.getReasonPhrase(), message.getStatus());
    }
}