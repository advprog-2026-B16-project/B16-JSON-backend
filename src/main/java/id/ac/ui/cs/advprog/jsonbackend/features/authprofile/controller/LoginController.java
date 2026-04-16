package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.LoginService;
import id.ac.ui.cs.advprog.jsonbackend.common.config.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getLoginInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Login endpoint is active. Use POST to login.");
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest request,
                                       BindingResult result) {
        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            User user = loginService.login(request);
            String token = jwtService.generateToken(user);
            return new ResponseEntity<>(UserLoginResponse.fromUser(user, token), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
