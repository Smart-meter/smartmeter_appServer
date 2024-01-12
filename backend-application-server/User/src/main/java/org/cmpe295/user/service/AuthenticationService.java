package org.cmpe295.user.service;


import lombok.RequiredArgsConstructor;
import org.cmpe295.user.entity.User;
import org.cmpe295.user.model.AuthenticationRequest;
import org.cmpe295.user.model.AuthenticationResponse;
import org.cmpe295.user.model.RegisterRequest;
import org.cmpe295.user.repository.UserRepository;
import org.cmpe295.user.security.service.JWTService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository repository;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    private final PasswordEncoder passwordEncoder;
    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        //Save the user into the db
        var savedUser = repository.save(user);
        //Generate the JWT token for the user
        var jwtToken = jwtService.generateToken(user);
        //Return the JWT token as the access token in repsonse
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        var user = repository.findByEmail(request.getEmail())
                .orElseThrow();
        //Generate the token
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }
}
