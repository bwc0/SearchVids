package com.searchvids.service.security;

import com.searchvids.model.Role;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

class UserDetailsServiceImplementationTest {

    private UserDetailsServiceImplementation implementation;
    private User user;

    @Mock
    private UserRepository repository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        implementation = new UserDetailsServiceImplementation(repository);

        user = new User();
        user.setId(1L);
        user.setUsername("username");
        user.setEmail("email");
        user.setPassword("password");
        user.setRoles(Stream.of(new Role(RoleName.ROLE_USER)).collect(Collectors.toSet()));
    }

    @Test
    @DisplayName("Load user by user name is successful")
    void loadUserByUsername_Successful() {
        given(repository.findByUsername(anyString())).willReturn(Optional.of(user));
        UserDetails foundUser = implementation.loadUserByUsername("username");

        assertAll(
                () -> assertEquals(user.getUsername(), foundUser.getUsername()),
                () -> assertTrue(foundUser.isAccountNonLocked()),
                () -> assertTrue(foundUser.isEnabled()),
                () -> assertTrue(foundUser.isAccountNonExpired()),
                () -> assertTrue(foundUser.isCredentialsNonExpired()),
                () -> assertEquals(1, foundUser.getAuthorities().size())
        );

    }

    @Test
    @DisplayName("Load user by user name throws Username not found exception throwm")
    void loadUserByUsername_ThrowsUsernameNotFoundException() {
        given(repository.findByUsername(anyString())).willReturn(Optional.empty());
        Throwable ex = assertThrows(UsernameNotFoundException.class, () ->
                implementation.loadUserByUsername("username"));
        assertEquals("username doesn't not exist.", ex.getMessage());
    }
}