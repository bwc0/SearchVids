package com.searchvids.service.security.jwt;

import com.searchvids.model.User;
import com.searchvids.repository.UserRepository;
import com.searchvids.service.security.UserDetailsServiceImplementation;
import org.apache.catalina.core.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class JwtProviderTest {

    private static final String username = "testUsername";
    private static final String password = "testPassword";
    private static final String jwtSecret = "secret";
    private static final int jwtExpiration = 86400;
    private AuthenticationManager manager;
    private Authentication authentication;
    private JwtProvider provider;
    private UserDetailsServiceImplementation implementation;
    private User user;

    @Mock
    private UserRepository repository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        provider = new JwtProvider();


        implementation = new UserDetailsServiceImplementation(repository);
    }

    @Test
    @Disabled
    void generateJwtTokenTest() {
//        Authentication authentication =



    }

    @Test
    @Disabled
    void validateJwtToken_ReturnTrue() {

    }

    @Test
    @Disabled
    @DisplayName("Signature Exception thrown")
    void validateJwtToken_ThrowSignatureExceptionTest() {
        System.out.println(provider.generateJwtToken(authentication));
    }


    @Test
    @Disabled
    void getUsernameFromJwtToken() {
    }
}