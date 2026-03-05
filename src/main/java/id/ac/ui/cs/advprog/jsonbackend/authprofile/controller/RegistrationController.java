package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.service.RegistrationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/register")
public class RegistrationController {

    private final RegistrationService registrationService;

    @Autowired
    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @GetMapping
    public ResponseEntity<?> getRegistrationInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Registration endpoint is active. Use POST to register.");
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationRequest request,
                               BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        if (!request.passwordConfirmationMathces()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Passwords do not match");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        try {
            registrationService.register(request);
            Map<String, String> response = new HashMap<>();
            response.put("message", "User registered successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
