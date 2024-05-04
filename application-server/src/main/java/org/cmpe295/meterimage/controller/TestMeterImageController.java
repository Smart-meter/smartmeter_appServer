package org.cmpe295.meterimage.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestMeterImageController {
    @GetMapping("/meterimageservice")
    public String test(){
        return "Welcome to Meter Image Services";
    }
}
