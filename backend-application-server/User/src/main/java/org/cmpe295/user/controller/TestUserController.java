package org.cmpe295.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestUserController {
    @GetMapping("/userservice")
    public String test(){
        return "Welcome to Core Services";
    }
}
