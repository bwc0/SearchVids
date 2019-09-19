package com.searchvids.service.security.jwt;

import com.searchvids.service.security.UserDetailsServiceImplementation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.security.SignatureException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class JwtAuthTokenFilterTest {

    private JwtAuthTokenFilter filter;

    @Mock
    private JwtProvider provider;

    @Mock
    private UserDetailsServiceImplementation implementation;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        filter = new JwtAuthTokenFilter(provider, implementation);
    }

    @Test
    @Disabled
    void doFilterInternal_ValidateTokenThrows_SignatureException() {
        given(provider.validateJwtToken(anyString()))
                .willAnswer( invocation -> {throw new SignatureException();});

        Throwable ex = assertThrows(SignatureException.class, () ->
                filter.doFilterInternal(any(), any(), any()));

        assertEquals("Message", ex.getMessage());
    }
}