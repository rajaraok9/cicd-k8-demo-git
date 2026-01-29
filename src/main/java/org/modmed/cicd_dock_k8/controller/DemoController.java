package org.modmed.cicd_docker.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    @GetMapping("/")
    public String home() {
       return "Build #1: Success!";
        //return "System Status: Error - Version 1.1";
    }
}