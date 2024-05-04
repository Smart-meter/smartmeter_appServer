package org.cmpe295.user.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HomeController {
    @GetMapping({"/user"})
    public String home() {
        return "<h1>Welcome to Utility Management System Backend Server User Module</h1>";
    }
}
