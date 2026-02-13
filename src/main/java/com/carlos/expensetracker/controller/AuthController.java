package com.carlos.expensetracker.controller;

import com.carlos.expensetracker.dto.request.LoginRequest;
import com.carlos.expensetracker.dto.request.SignUpRequest;
import com.carlos.expensetracker.dto.response.LoginResponse;
import com.carlos.expensetracker.dto.response.SignUpResponse;
import com.carlos.expensetracker.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    //signup endpoint
    @Operation(summary = "Sign up a new user")
    @PostMapping("/signup")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody @Valid SignUpRequest signUpRequest) {
        log.info("POST /api/auth/signup");

        SignUpResponse response = authService.signUp(signUpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    //login endpoint
    @Operation(summary = "Log in for a user")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("POST /api/auth/login");

        LoginResponse response = authService.login(loginRequest);

        return ResponseEntity.ok(response);

    }
}
