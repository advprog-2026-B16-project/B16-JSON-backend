package id.ac.ui.cs.advprog.jsonbackend.features.authprofile.controller;

import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserLoginResponse;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.dto.UserProfileUpdateRequest;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.model.User;
import id.ac.ui.cs.advprog.jsonbackend.features.authprofile.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

    @PatchMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> banUser(@PathVariable UUID id) {
        userService.banUser(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/demote")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> demoteUser(@PathVariable UUID id) {
        userService.demoteUser(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/profile")
    public ResponseEntity<Void> updateProfile(@RequestBody UserProfileUpdateRequest request, Principal principal) {
        String username = principal.getName();
        userService.getUserByUsername(username).ifPresent(user -> {
            userService.updateProfile(user.getId(), request);
        });
        return ResponseEntity.ok().build();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Principal principal) {
        String username = principal.getName();
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/profile/{username}")
    public ResponseEntity<User> getPublicProfile(@PathVariable String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
