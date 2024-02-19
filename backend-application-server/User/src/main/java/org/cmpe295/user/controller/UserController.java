package org.cmpe295.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.cmpe295.user.entity.User;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    @GetMapping("/details")
    public ResponseEntity<UserDetailsResponse> getUserDetails(HttpServletRequest request) {
        // Retrieve authentication from SecurityContextHolder
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        logger.info("Retrieved authentication info from the security context holder");
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            logger.info("Found user details in security context holder authentication object");
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            logger.info("Found user details "+userDetails);
            return ResponseEntity.ok(userService.getUserDetails(userDetails.getUsername()));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
