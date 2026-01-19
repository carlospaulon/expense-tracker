package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.LoginRequest;
import com.carlos.expensetracker.dto.request.SignUpRequest;
import com.carlos.expensetracker.dto.response.LoginResponse;
import com.carlos.expensetracker.dto.response.SignUpResponse;

public interface AuthService {

    //register user
    SignUpResponse signUp(SignUpRequest request);

    //auth user
    LoginResponse login(LoginRequest request);

}
