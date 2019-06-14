package com.searchvids.service;

import com.searchvids.exception.ResourceNotFoundException;
import com.searchvids.model.RoleName;
import com.searchvids.model.User;
import com.searchvids.model.payload.JwtResponse;
import com.searchvids.model.payload.LoginForm;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.SignUpForm;
import com.searchvids.repository.RoleRepository;
import com.searchvids.repository.UserRepository;
import com.searchvids.service.security.jwt.JwtProvider;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImplementation implements AuthService {

    private AuthenticationManager manager;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private PasswordEncoder encoder;
    private JwtProvider provider;

    public AuthServiceImplementation(AuthenticationManager manager, UserRepository userRepository,
                                     RoleRepository roleRepository, PasswordEncoder encoder, JwtProvider provider) {
        this.manager = manager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.provider = provider;
    }

    @Override
    public JwtResponse authentication(LoginForm loginForm) {
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(loginForm.getUsername(), loginForm.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String jwt = provider.generateJwtToken(authentication);
        UserDetails details = (UserDetails) authentication.getPrincipal();

        User user = userRepository.findByUsername(details.getUsername()).orElseThrow(() ->
                new ResourceNotFoundException("User", "username", details.getUsername()));

        return new JwtResponse(user.getId(), jwt, user.getUsername(), details.getAuthorities());
    }

    @Override
    public ResponseMessage registration(SignUpForm signUpForm) {
        if (userRepository.existsByUsername(signUpForm.getUsername())) {
            return new ResponseMessage("Fail -> Username already taken", HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        if (userRepository.existsByEmail(signUpForm.getEmail())) {
            return new ResponseMessage("Fail -> Email already taken", HttpStatus.BAD_REQUEST.getReasonPhrase());
        }

        User user = new User();

        user.setUsername(signUpForm.getUsername());
        user.setEmail(signUpForm.getEmail());
        user.setPassword(encoder.encode(signUpForm.getPassword()));
        user.getRoles().add(roleRepository.findByName(RoleName.ROLE_USER).get());

        userRepository.save(user);

        return new ResponseMessage("User registered successfully! Please log in", HttpStatus.OK.getReasonPhrase());
    }
}