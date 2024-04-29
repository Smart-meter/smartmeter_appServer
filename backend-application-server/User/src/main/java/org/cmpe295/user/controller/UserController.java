package org.cmpe295.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cmpe295.user.model.MessageResponse;
import org.cmpe295.user.model.UpdateUserRequest;
import org.cmpe295.user.model.UserDetailsResponse;
import org.cmpe295.user.security.service.JWTService;
import org.cmpe295.user.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JWTService jwtService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    // Common method to retrieve current user details
    private UserDetails getCurrentUserDetails() {
        // Retrieve authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Retrieved authentication info from the security context holder");

        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            logger.info("Found user details in security context holder authentication object");
            return (UserDetails) authentication.getPrincipal();
        }
        return null;
    }
    @GetMapping("/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(HttpServletRequest request) {
        UserDetails userDetails = getCurrentUserDetails();
        if (userDetails != null) {
            logger.info("Found user details " + userDetails);
            return ResponseEntity.ok(userService.getUserDetails(userDetails.getUsername()));
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @GetMapping("/messages")
    public ResponseEntity<List<MessageResponse>> sendMessages(HttpServletRequest request) {
        UserDetails userDetails = getCurrentUserDetails();

        if (userDetails != null) {
            logger.info("Found user details " + userDetails);
            // Call the org.cmpe295.utilityaccount.service method to check conditions and generate messages
            List<MessageResponse> messages = userService.generateMessages(userDetails.getUsername());
            // Check if there are any messages to send
            return ResponseEntity.ok(messages);

        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    @PutMapping("/update")
    public ResponseEntity<UserDetailsResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Retrieved authentication info from the security context holder");
        UserDetails userDetails = getCurrentUserDetails();
        try{
            if (userDetails != null) {
                logger.info("Found user details " + userDetails);
                //Call the user org.cmpe295.utilityaccount.service to update the user details
                return  ResponseEntity.ok(userService.updateUserDetails(userDetails.getUsername(), updateUserRequest));
            }else{
                //Did not find user details from the JWT token
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

}
