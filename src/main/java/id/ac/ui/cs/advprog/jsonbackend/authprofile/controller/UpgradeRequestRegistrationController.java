package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UpgradeRequestRegistrationRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UpgradeRequestService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upgrade-request")
@RequiredArgsConstructor
public class UpgradeRequestRegistrationController {

    private final UpgradeRequestService upgradeRequestService;
    private final UserService userService;

    @PostMapping("/apply")
    public ResponseEntity<?> applyForUpgrade(@Valid @RequestBody UpgradeRequestRegistrationRequest request,
                                             BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            String username = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            upgradeRequestService.createRequest(user, request);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Upgrade request submitted successfully");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }
    }
}
