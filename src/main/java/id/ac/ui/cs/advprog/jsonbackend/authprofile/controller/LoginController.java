package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.config.JwtService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginRequest;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.exception.BadCredentialsException;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginAttemptService;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.LoginService;
import jakarta.servlet.http.HttpServletRequest;
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
    private final LoginAttemptService loginAttemptService;
    private final JwtService jwtService;

    @GetMapping
    public ResponseEntity<?> getLoginInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("message", "Login endpoint is active. Use POST to login.");
        return ResponseEntity.ok(info);
    }

    @PostMapping
    public ResponseEntity<?> loginUser(@Valid @RequestBody UserLoginRequest request,
                                       BindingResult result,
                                       HttpServletRequest httpServletRequest) {
        String ip = getClientIP(httpServletRequest);
        
        if (loginAttemptService.isBlocked(ip)) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Too many failed login attempts. Please try again after 5 minutes.");
            return new ResponseEntity<>(error, HttpStatus.TOO_MANY_REQUESTS);
        }

        if (result.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            result.getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        try {
            User user = loginService.login(request);
            loginAttemptService.loginSucceeded(ip);
            String token = jwtService.generateToken(user);
            return new ResponseEntity<>(UserLoginResponse.fromUser(user, token), HttpStatus.OK);
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(ip);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "An internal server error occurred");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
