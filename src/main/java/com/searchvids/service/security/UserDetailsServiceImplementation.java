package com.searchvids.service.security;

import com.searchvids.model.User;
import com.searchvids.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;

public class UserDetailsServiceImplementation implements UserDetailsService {

    private UserRepository repository;

    public UserDetailsServiceImplementation(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUsername(username).orElseThrow(() ->
                new UsernameNotFoundException(String.format("%s doesn't not exist.", username)));

        return UserPrincipal.build(user);
    }
}
