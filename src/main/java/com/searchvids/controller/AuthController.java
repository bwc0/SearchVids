package com.searchvids.controller;

import com.searchvids.model.payload.LoginForm;
import com.searchvids.model.payload.SignUpForm;
import com.searchvids.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static com.searchvids.controller.AuthController.AUTH_API;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(AUTH_API)
public class AuthController {

    public static final String AUTH_API = "/api/auth";

    private AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginForm form) {
        return ResponseEntity.ok(authService.authentication(form));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpForm form) {
        return ResponseEntity.ok(authService.registration(form));
    }
}
