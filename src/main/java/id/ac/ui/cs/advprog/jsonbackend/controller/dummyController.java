package id.ac.ui.cs.advprog.jsonbackend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class dummyController {
    @GetMapping("/hello")
    public String sayHello() {
        return "Hello from Backend!";
    }
}