package com.searchvids.service;

import com.searchvids.model.payload.JwtResponse;
import com.searchvids.model.payload.LoginForm;
import com.searchvids.model.payload.ResponseMessage;
import com.searchvids.model.payload.SignUpForm;

public interface AuthService {
    JwtResponse authentication(LoginForm loginForm);
    ResponseMessage registration(SignUpForm signUpForm);
}
