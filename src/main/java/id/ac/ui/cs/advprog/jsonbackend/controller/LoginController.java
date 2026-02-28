package id.ac.ui.cs.advprog.jsonbackend.controller;

import id.ac.ui.cs.advprog.jsonbackend.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.exception.UserNotFoundException;
import id.ac.ui.cs.advprog.jsonbackend.exception.WrongPasswordException;
import id.ac.ui.cs.advprog.jsonbackend.model.User;
import id.ac.ui.cs.advprog.jsonbackend.service.LoginService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/login")
public class LoginController {

    private final LoginService loginService;

    @Autowired
    public LoginController(LoginService loginService) {
        this.loginService = loginService;
    }

    @GetMapping
    public ResponseEntity<?> getLoginInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Login endpoint is active. Use POST to register.");
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
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "User logged in successfully");
            return new ResponseEntity<>(new UserLoginResponse(user), HttpStatus.CREATED);
        } catch (WrongPasswordException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFoundException e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
