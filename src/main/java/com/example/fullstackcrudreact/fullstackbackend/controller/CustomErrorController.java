package com.example.fullstackcrudreact.fullstackbackend.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error")
    public String handleError() {
        // Return your custom error page (e.g., error.html)
        return "error"; // This maps to a Thymeleaf or static HTML file named error.html
    }

}
