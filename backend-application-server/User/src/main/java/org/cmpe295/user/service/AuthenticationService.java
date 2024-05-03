package org.cmpe295.user.service;


import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.cmpe295.user.entity.Address;
import org.cmpe295.user.entity.User;
import org.cmpe295.user.entity.UserUtilityLink;
import org.cmpe295.user.entity.UtilityAccount;
import org.cmpe295.user.entity.enums.METER_TYPE;
import org.cmpe295.user.model.AuthenticationRequest;
import org.cmpe295.user.model.AuthenticationResponse;
import org.cmpe295.user.model.RegisterRequest;
import org.cmpe295.user.repository.UserRepository;
import org.cmpe295.user.repository.UserUtilityLinkRepository;
import org.cmpe295.user.repository.UtilityAccountRepository;
import org.cmpe295.user.security.service.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@AllArgsConstructor
public class AuthenticationService {
    @Autowired
    private UserRepository repository;
    @Autowired
    private UtilityAccountRepository utilityAccountRepository;
    @Autowired
    private UserUtilityLinkRepository userUtilityLinkRepository;
    @Autowired
    private  JWTService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public AuthenticationResponse register(RegisterRequest request) throws Exception {

        var user = User.builder()
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();

        //Update the address and Utility Account Link
        Address address = Address.builder()
                .country(request.getCountry())
                .aptSuite(request.getAptSuite())
                .street(request.getStreet())
                .city(request.getStreet())
                .state(request.getStreet())
                .zip(request.getZipCode())
                .build();
        user.setAddress(address);
        //Check if the utility account is already there
        //If not, create one
        Optional<UtilityAccount> utilityAccount = utilityAccountRepository.findByUtilityAccountNumber(request.getUtilityAccountNumber());
        if(utilityAccount.isPresent()){
            if(utilityAccountRepository.hasActiveUserUtilityLinks(utilityAccount.get())){
                throw new Exception("Cannot activate utility account. Active user utility links exist.");
            }
        }else{
            //Create utility account
            UtilityAccount newUtilityAccount = UtilityAccount.builder()
                    .utilityAccountNumber(request.getUtilityAccountNumber())
                    .address(address)
                    .meterType(METER_TYPE.ELECTRIC) //By default electric
                    .build();
            UtilityAccount savedUtilityAccount = utilityAccountRepository.save(newUtilityAccount);
        }
        Optional<UtilityAccount> savedUtilityAccount = utilityAccountRepository.findByUtilityAccountNumber(request.getUtilityAccountNumber());
        //Create user utility links
        User savedUser = repository.save(user);
        //Create the User UtilityLink
        UserUtilityLink userUtilityLink = UserUtilityLink.builder()
                .user(savedUser)
                .utilityAccount(savedUtilityAccount.get())
                .dateOfLink(LocalDateTime.now())
                .isActive(true)
                .build();
        userUtilityLinkRepository.save(userUtilityLink);
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
