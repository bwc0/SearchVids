package com.searchvids.bootstrap;

import com.searchvids.model.Role;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.repository.RoleRepository;
import com.searchvids.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyIterable;
import static org.mockito.BDDMockito.given;

class BootstrapDataTest {

    private Role role;
    private User user;
    private BootstrapData dataClass;
    private Set<Role> roles;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder encoder;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        role = new Role(RoleName.ROLE_USER);

        roles = Stream.of(role).collect(Collectors.toSet());

        user = new User("username", "test@test.com",
                encoder.encode("password"), roles, new HashSet<>());

        dataClass = new BootstrapData(userRepository, roleRepository, encoder);
    }

    @Test
    void loadUsersTest() {
        List<User> users = asList(user, new User());
        List<Role> rolesList = new ArrayList<>(roles);

        given(roleRepository.saveAll(any())).willReturn(rolesList);
        given(userRepository.saveAll(any())).willReturn(users);

        List<User> loadedUsersFromDatabase = dataClass.loadUsers();

        assertEquals(1, loadedUsersFromDatabase.get(0).getRoles().size());
        assertEquals(2, loadedUsersFromDatabase.size());
    }
}