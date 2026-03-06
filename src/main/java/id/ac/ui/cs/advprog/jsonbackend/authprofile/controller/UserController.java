package id.ac.ui.cs.advprog.jsonbackend.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.authprofile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/getUsers")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserLoginResponse>> getUsers() {
        List<UserLoginResponse> users = userService.getAllUsers().stream()
                .map(user -> UserLoginResponse.fromUser(user, null))
                .collect(Collectors.toList());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }
}
