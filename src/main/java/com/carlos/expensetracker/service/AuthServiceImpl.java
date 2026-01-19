package com.carlos.expensetracker.service;

import com.carlos.expensetracker.dto.request.LoginRequest;
import com.carlos.expensetracker.dto.request.SignUpRequest;
import com.carlos.expensetracker.dto.response.LoginResponse;
import com.carlos.expensetracker.dto.response.SignUpResponse;
import com.carlos.expensetracker.entity.User;
import com.carlos.expensetracker.entity.enums.UserRole;
import com.carlos.expensetracker.exception.BadRequestException;
import com.carlos.expensetracker.exception.UnauthorizedException;
import com.carlos.expensetracker.repository.UserRepository;
import com.carlos.expensetracker.security.CustomUserDetails;
import com.carlos.expensetracker.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;


    //register user
    @Override
    @Transactional
    public SignUpResponse signUp(SignUpRequest request) {
        log.info("Attempting to register a new user: {}", request.email());

        //if email exist
        if (userRepository.existsByEmail(request.email())) {
            log.warn("Signup failed: Email already registered - {}", request.email());

            throw new BadRequestException("Email already registered");
        }

        //if username exist
        if (userRepository.existsByUsername(request.username())) {
            log.warn("Signup failed: Username already registered - {}", request.username());

            throw new BadRequestException("Username already registered");
        }

        //create user
        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password())) //hash
                .role(UserRole.USER) //default
                .build();

        //saving user
        User savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getEmail());

        //without automatic auth -
        return new SignUpResponse(
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getRole(),
                savedUser.getCreatedAt()
                //message into the constructor
        );
    }

    //auth user
    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {

        log.info("Attempting to authenticate user: {}", request.email());

        try {
            //Authentication
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );

            log.info("User authenticated successfully: {}", request.email());

            //take user by userDetails - without extra query
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            //generate token
            String token = jwtTokenProvider.generateToken(authentication);
            Long expiresIn = jwtTokenProvider.getExpiresInSeconds();

            //return with token
            return LoginResponse.bearer(
                    token,
                    expiresIn,
                    userDetails.getUserId(),
                    userDetails.getUsernameField(),
                    userDetails.getEmail(),
                    userDetails.getUser().getRole()
            );


        } catch (AuthenticationException ex) {
            log.warn("Authentication failed for user: {}", request.email());
            throw new UnauthorizedException("Invalid credentials");
        }

    }
}
